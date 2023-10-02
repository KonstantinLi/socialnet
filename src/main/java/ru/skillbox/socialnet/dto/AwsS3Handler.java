package ru.skillbox.socialnet.dto;

import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.TriConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.socialnet.exception.EmptyFileException;
import ru.skillbox.socialnet.exception.FileSizeException;
import ru.skillbox.socialnet.exception.UnsupportedFileTypeException;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.DirectoryUpload;
import software.amazon.awssdk.transfer.s3.model.UploadDirectoryRequest;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static software.amazon.awssdk.transfer.s3.SizeConstant.MB;

@Slf4j
@Component
public class AwsS3Handler implements LogUploader {

    @Value("${aws.image-bucket-name}")
    private String imageBucketName;

    @Value("${aws.log-bucket-name}")
    private String logBucketName;

    @Value("${aws.access-key-id}")
    private String accessKeyId;

    @Value("${aws.secret-access-key}")
    private String secretAccessKey;

    @Value("${aws.region}")
    private String regionName;

    @Value("${aws.max-image-file-size}")
    private String maxImageSize;

    private S3Client client;
    private S3AsyncClient asyncClient;

    @Override
    public void uploadLog(String path) {
        initializeS3AsyncClient();

        try (S3TransferManager manager = S3TransferManager.builder().s3Client(asyncClient).build()) {
            DirectoryUpload directoryUpload =
                    manager.uploadDirectory(UploadDirectoryRequest.builder()
                            .source(Paths.get(path))
                            .bucket(logBucketName)
                            .build());

            directoryUpload.completionFuture().join();
        }

        deleteFilesWithExtension(logBucketName, ".log");
    }

    @Override
    public void deleteExpiredLogs(Duration expired) {
        deleteOldFiles(logBucketName, expired);
    }

    public void uploadImage(String type, MultipartFile image, String generatedFileName) throws IOException {
        initializeS3Client();
        checkImageForUpload(image);
        clearDuplicateImages(generatedFileName);

        byte[] imageContent = image.getBytes();

        uploadFile(type, imageContent, generatedFileName, imageBucketName);
    }

    private void uploadFile(String type,
                            byte[] fileContent,
                            String generatedFileName,
                            String destinationBucketName) {


        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(destinationBucketName)
                .key(generatedFileName)
                .contentType(type)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        client.putObject(putObjectRequest, RequestBody.fromBytes(fileContent));
    }

    private void deleteFilesWithExtension(String bucketName,
                                          @SuppressWarnings("SameParameterValue") String fileExtension) {
        deleteFiles(bucketName, fileExtension, deleteFileWithExtension());
    }

    private void deleteOldFiles(String bucketName, Duration expired) {
        deleteFiles(bucketName, expired, deleteExpiredFile());
    }

    private void deleteFiles(
            String bucketName,
            Object condition,
            TriConsumer<String, S3Object, Object> triConsumer) {

        initializeS3Client();

        ListObjectsV2Request listObjectsRequest = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        ListObjectsV2Response listObjectsResponse;
        do {
            listObjectsResponse = client.listObjectsV2(listObjectsRequest);

            for (S3Object s3Object : listObjectsResponse.contents()) {
                triConsumer.accept(bucketName, s3Object, condition);
            }

            listObjectsRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .continuationToken(listObjectsResponse.nextContinuationToken())
                    .build();

        } while (listObjectsResponse.isTruncated());
    }

    private TriConsumer<String, S3Object, Object> deleteExpiredFile() {
        return (bucketName, s3Object, expired) -> {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime lastModified = LocalDateTime.ofInstant(
                    s3Object.lastModified(), ZoneId.of("UTC+3"));

            if (Duration.between(lastModified, now).compareTo((Duration) expired) > 0) {
                deleteFile(s3Object.key(), bucketName);
            }
        };
    }

    private TriConsumer<String, S3Object, Object> deleteFileWithExtension() {
        return (bucketName, s3Object, extension) -> {
            String key = s3Object.key();
            if (key.endsWith((String) extension)) {
                deleteFile(key, bucketName);
            }
        };
    }

    public List<String> getBucketContentNames(String bucketName) {
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(bucketName)
                .build();

        initializeS3Client();

        ArrayList<String> fileNames = new ArrayList<>();

        client.listObjectsV2(listObjectsV2Request)
                .contents()
                .forEach(s3Object -> fileNames.add(s3Object.key()));

        return fileNames;
    }

    //TODO remove unused method?
    private String checkForSameFileName(String generatedFileName) {
        String fileIdentifier = generatedFileName.split("_", 2)[0];

        List<String> fileNames = getBucketContentNames(logBucketName);

        for (String name : fileNames) {
            if (name.contains(fileIdentifier)) {
                generatedFileName = "%s_%s".formatted(generatedFileName, generateSalt());
                log.warn("File with the same name already exists." +
                        " New file will be uploaded with name: " +
                        generatedFileName);
            }
        }

        return generatedFileName;
    }

    private void checkImageForUpload(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new EmptyFileException("File is empty");
        } else if (file.getSize() > getMaxImageSize()) {
            throw new FileSizeException("File is too large");
        } else if (file.getOriginalFilename() == null) {
            throw new UnsupportedFileTypeException("File name cannot be null");
        } else if (!file.getOriginalFilename().endsWith("jpg") &&
                !file.getOriginalFilename().endsWith("png")) {
            String[] fileNameSplit = file.getOriginalFilename().split("\\.");
            String fileType = fileNameSplit[fileNameSplit.length - 1].toLowerCase();
            throw new UnsupportedFileTypeException("File type ." + fileType + " is not supported");
        }
    }

    /**
     * Method deletes all files with the same identifier as the uploaded file
     *
     * @param fileName - name of the uploaded file
     */
    private void clearDuplicateImages(String fileName) {

        String userId = fileName.split("_", 2)[0];

        List<String> fileNames = getBucketContentNames(imageBucketName);

        for (String name : fileNames) {
            if (name.contains(userId)) {
                deleteFile(name, imageBucketName);
            }
        }

    }

    private void deleteFile(String fileName, String bucketName) {

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        client.deleteObject(deleteObjectRequest);
    }

    private void initializeS3Client() {
        if (this.client == null) {

            Region region = getRegion();
            AwsCredentialsProvider credentialsProvider = getCredentialsProvider();

            this.client = S3Client.builder()
                    .credentialsProvider(credentialsProvider)
                    .region(region)
                    .build();
        }
    }

    private void initializeS3AsyncClient() {
        if (this.asyncClient == null) {

            Region region = getRegion();
            AwsCredentialsProvider credentialsProvider = getCredentialsProvider();

            this.asyncClient = S3AsyncClient.crtBuilder()
                    .credentialsProvider(credentialsProvider)
                    .region(region)
                    .targetThroughputInGbps(20.0)
                    .minimumPartSizeInBytes(8 * MB)
                    .build();
        }
    }

    private Region getRegion() {
        return Region.of(regionName);
    }

    private Long getMaxImageSize() {
        return Long.parseLong(maxImageSize);
    }

    private AwsCredentialsProvider getCredentialsProvider() {
        return () -> AwsBasicCredentials.create(
                accessKeyId,
                secretAccessKey);
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] generatedBytes = new byte[20];
        random.nextBytes(generatedBytes);

        return new String(generatedBytes);
    }
}
