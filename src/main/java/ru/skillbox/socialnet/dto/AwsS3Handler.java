package ru.skillbox.socialnet.dto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.socialnet.errs.BadRequestException;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class AwsS3Handler {

    @Value("${aws.bucket-name}")
    private String awsBucketName;

    @Value("${aws.access-key-id}")
    private String accessKeyId;

    @Value("${aws.secret-access-key}")
    private String secretAccessKey;

    @Value("${aws.region}")
    private String regionName;

    @Value("${aws.max-file-size}")
    private String maxFileSize;

    private S3Client client;

    //TODO change RuntimeExceptions to custom exceptions
    public void uploadFile(String type, MultipartFile file, String generatedFileName) throws BadRequestException, InterruptedException {

        checkFileForUpload(file);

        initializeS3Client();

        clearDuplicates(generatedFileName);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(awsBucketName)
                .key(generatedFileName)
                .contentType(type)
                .acl(ObjectCannedACL.PUBLIC_READ)
                .build();

        byte[] bufferedFile;
        try {
            bufferedFile = file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException("File could not be read");
        }

        try {
            client.putObject(putObjectRequest, RequestBody.fromBytes(bufferedFile));
        } finally {
            client.close();
        }

    }

    private void checkFileForUpload(MultipartFile file) throws BadRequestException {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is empty");
        } else if (file.getSize() > getMaxFileSize()) {
            throw new BadRequestException("File is too large");
        } else if (!Objects.requireNonNull(file.getOriginalFilename()).endsWith("jpg") &&
                !file.getOriginalFilename().endsWith("png")) {
            throw new BadRequestException("File type is not supported");
        }
    }

    private List<String> listObjects() {
        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(awsBucketName)
                .build();

        ArrayList<String> fileNames = new ArrayList<>();

        client.listObjectsV2(listObjectsV2Request).contents()
                .forEach(s3Object -> {
            fileNames.add(s3Object.key());
        });

        return fileNames;
    }
    /**
     * Method deletes all files with the same identifier as the uploaded file
     *
     * @param fileName - name of the uploaded file
     * @return true if duplicates were found and deleted, false if no duplicates were found
     */
    private boolean clearDuplicates(String fileName) {
        String userId = fileName.split("_", 2)[0];

        List<String> fileNames = listObjects();
        boolean duplicatesFound = false;

        for (String name : fileNames) {
            if (name.contains(userId)) {
                deleteFile(name);
                duplicatesFound = true;
            }
        }

        return duplicatesFound;
    }

    private void deleteFile(String fileName) {

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(awsBucketName)
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

    private Long getMaxFileSize() {
        return Long.parseLong(maxFileSize);
    }

    private Region getRegion() {
        return Region.of(regionName);
    }
    private AwsCredentialsProvider getCredentialsProvider() {
        return () -> AwsBasicCredentials.create(
                accessKeyId,
                secretAccessKey);
    }
}
