package ru.storage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

import java.net.URI;

@Configuration
public class S3Config {

    @Value("${cloud.aws.credentials.access-key}")
    private String AWS_ACCESS_KEY_ID;
    @Value("${cloud.aws.credentials.secret-key}")
    private String AWS_SECRET_ACCESS_KEY;
    @Value("${cloud.aws.s3.endpoint}")
    private String S3_ENDPOINT;
    @Value("${cloud.aws.region}")
    private String REGION;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(REGION))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY)
                ))
                .endpointOverride(URI.create(S3_ENDPOINT))
                .serviceConfiguration(S3Configuration.builder()
                        .pathStyleAccessEnabled(true) // Yandex requires path-style access
                        .build())
                .build();
    }

}
