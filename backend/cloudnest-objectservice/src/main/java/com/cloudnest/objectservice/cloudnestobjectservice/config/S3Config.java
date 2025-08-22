package com.cloudnest.objectservice.cloudnestobjectservice.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;

import java.net.URI;


@Configuration
    public class S3Config {

        @Value("${s3.endpoint}")
        private String endpoint;

        @Value("${s3.access-key}")
        private String accessKey;

        @Value("${s3.secret-key}")
        private String secretKey;

        @Value("${s3.bucket}")
        private String bucket;

        @Bean
        public S3Client s3Client() throws Exception {
            S3Client s3 = S3Client.builder()
                    .endpointOverride(new URI(endpoint))
                    .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                    .region(Region.US_EAST_1)
                    .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                    .build();

            try {
                s3.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
            } catch (Exception e) {
                s3.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
            }

            return s3;
        }
    }
