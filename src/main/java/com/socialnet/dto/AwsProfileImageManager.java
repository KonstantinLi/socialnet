package com.socialnet.dto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.socialnet.annotation.Debug;
import com.socialnet.exception.EmptyFileException;
import com.socialnet.exception.FileSizeException;
import com.socialnet.exception.UnsupportedFileTypeException;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Debug
@Component
public class AwsProfileImageManager implements ProfileImageManager {

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

    private S3Client client;

    public void uploadProfileImage(String type, MultipartFile image, String generatedFileName) throws IOException {
        initializeS3Client();
        checkImageForUpload(image);
        clearOldPhotos(generatedFileName);

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

    private Region getRegion() {
        return Region.of(regionName);
    }

    private AwsCredentialsProvider getCredentialsProvider() {
        return () -> AwsBasicCredentials.create(
                accessKeyId,
                secretAccessKey);
    }

    private void checkImageForUpload(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new EmptyFileException("File is empty");
        } else if (file.getSize() > getMaxImageSize()) {
            throw new FileSizeException("File is too large");
        }

        String originalFilename = file.getOriginalFilename();

        if (originalFilename == null) {
            throw new UnsupportedFileTypeException("File name cannot be null");
        } else if (!originalFilename.endsWith("jpg") &&
                !originalFilename.endsWith("png")) {
            String[] fileNameSplit = originalFilename.split("\\.");
            String fileType = fileNameSplit[fileNameSplit.length - 1].toLowerCase();
            throw new UnsupportedFileTypeException("File type ." + fileType + " is not supported");
        }
    }

    private Long getMaxImageSize() {
        return Long.parseLong(maxImageSize);
    }

    private void clearOldPhotos(String fileName) {
        String userId = fileName.split("_", 2)[0];

        List<String> fileNames = getBucketContentNames(imageBucketName);

        for (String name : fileNames) {
            String photoOwnerId = name.split("_", 2)[0];
            if (photoOwnerId.equals(userId)) {
                deleteFile(name, imageBucketName);
            }
        }
    }

    @Override
    public void deleteProfileImage(Long userId) {
        initializeS3Client();

        clearOldPhotos(userId + "_");
    }

    private List<String> getBucketContentNames(String bucketName) {
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

    private void deleteFile(String fileName, String bucketName) {

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        client.deleteObject(deleteObjectRequest);
    }
}
