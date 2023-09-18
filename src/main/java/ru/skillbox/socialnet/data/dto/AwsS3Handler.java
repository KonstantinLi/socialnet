package ru.skillbox.socialnet.data.dto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;

@Component
public class AwsS3Handler {

    @Value("")
    private String awsBucketName;

    @Value("${aws.accessKeyId}")
    private String accessKeyId;

    @Value("${aws.secretAccessKey}")
    private String secretAccessKey;

    @Value("${aws.region}")
    private String regionName;

    public void listObjects() {

        Region region = getRegion();
        AwsCredentialsProvider credentialsProvider = getCredentialsProvider();

        S3Client client = S3Client.builder()
                .credentialsProvider(credentialsProvider)
                .region(region)
                .build();

        ListBucketsResponse listedBuckets = client.listBuckets();

        ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                .bucket(awsBucketName)
                .build();

        client.listObjectsV2(listObjectsV2Request).contents().forEach(System.out::println);
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
