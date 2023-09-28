package ru.skillbox.socialnet.dto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.socialnet.exception.BadRequestException;
import ru.skillbox.socialnet.exception.EmptyFileException;
import ru.skillbox.socialnet.exception.FileSizeException;
import ru.skillbox.socialnet.exception.UnsupportedFileTypeException;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

//TODO remove log-files-handle methods
@Slf4j
@Component
public class AwsS3Handler {

    @Value("${aws.image-bucket-name}")
    private String imageBucketName;

    @Value("${aws.access-key-id}")
    private String accessKeyId;

    @Value("${aws.secret-access-key}")
    private String secretAccessKey;

    @Value("${aws.region}")
    private String regionName;

    @Value("${aws.max-image-file-size}")
    private String maxImageSize;

    @Value("${aws.log-bucket-name}")
    private String logBucketName;

    @Value("${aws.max-log-file-size}")
    private String maxLogFileSize;

    @Value("${aws.log-url-prefix}")
    private String logUrlPrefix;

    private S3Client client;

    public void uploadImage(String type, MultipartFile image, String generatedFileName) throws IOException {
        initializeS3Client();
        checkImageForUpload(image);
        clearDuplicateImages(generatedFileName);

        byte[] imageContent = image.getBytes();

        uploadFile(type, imageContent, generatedFileName, imageBucketName);
    }


    public void uploadLogFile(String type, File file, String logFileName) {
        initializeS3Client();
        checkLogForUpload(file);
        logFileName = checkForSameFileName(logFileName);

        byte[] fileContent;

        try {
            fileContent = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            throw new BadRequestException("File cannot be read");
        }

        uploadFile(type, fileContent, logFileName, logBucketName);
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

    public List<String> getLogFilesUrls() {
        initializeS3Client();
        List<String> logFilesList = getBucketContentNames(logBucketName);
        List<String> logFilesUrls = new ArrayList<>();

        logFilesList.forEach(logFileName -> {
            logFileName = logUrlPrefix + logFileName;
            logFilesUrls.add(logFileName);
        });

        return logFilesUrls;
    }

    private void checkLogForUpload(File file) {
        if (file == null) {
            log.error("File is null");
        } else if (!file.exists()) {
            log.error("File does not exist");
        } else if (!file.canRead()) {
            log.error("File cannot be read");
        } else if (file.length() > getMaxLogFileSize()) {
            log.error("File is too large");
        } else if (!file.getName().endsWith("log") && !file.getName().endsWith("txt")) {
            log.error("File type is not supported. Supported fileTypes: .log, .txt");
        }
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

        Region region = getRegion();
        AwsCredentialsProvider credentialsProvider = getCredentialsProvider();

        this.client = S3Client.builder()
                .credentialsProvider(credentialsProvider)
                .region(region)
                .build();
    }

    private Region getRegion() {
        return Region.of(regionName);
    }

    private Long getMaxLogFileSize() {
        return Long.parseLong(maxLogFileSize);
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
