package com.socialnet.dto;

import org.apache.logging.log4j.util.TriConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.socialnet.annotation.Debug;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.transfer.s3.S3TransferManager;
import software.amazon.awssdk.transfer.s3.model.DirectoryUpload;
import software.amazon.awssdk.transfer.s3.model.UploadDirectoryRequest;

import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;

import static software.amazon.awssdk.transfer.s3.SizeConstant.MB;

@Debug
@Component
public class AwsLogManager implements LogUploader {

    @Value("${aws.log-bucket-name}")
    private String logBucketName;

    @Value("${aws.access-key-id}")
    private String accessKeyId;

    @Value("${aws.secret-access-key}")
    private String secretAccessKey;

    @Value("${aws.region}")
    private String regionName;

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

        } while (Boolean.TRUE.equals(listObjectsResponse.isTruncated()));
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

    private AwsCredentialsProvider getCredentialsProvider() {
        return () -> AwsBasicCredentials.create(
                accessKeyId,
                secretAccessKey);
    }
}
