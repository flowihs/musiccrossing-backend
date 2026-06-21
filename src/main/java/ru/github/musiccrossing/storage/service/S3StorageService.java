package ru.github.musiccrossing.storage.service;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

@Service
@RequiredArgsConstructor
public class S3StorageService implements StorageService {

    private final S3Template s3Template;

    @Value("${app.s3-bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.s3.endpoint}")
    private String endpoint;

    @Override
    public String upload(byte[] data, String path, String filename) {
        String key = path + filename;

        ByteArrayInputStream inputStream = new ByteArrayInputStream(data);

        s3Template.upload(bucket, key, inputStream);

        return endpoint + "/" + bucket + "/" + key;
    }
}
