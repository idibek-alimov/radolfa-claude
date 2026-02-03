package com.radolfa.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * AWS S3 client bean.
 * <p>
 * Credentials are resolved automatically by the SDK v2 default-credential chain
 * (environment variables, {@code ~/.aws/credentials}, EC2 instance metadata, etc.).
 * No credentials are hard-coded here.
 * </p>
 */
@Configuration
public class S3Config {

    @Value("${aws.s3.region:us-east-1}")
    private String region;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .build();
    }
}
