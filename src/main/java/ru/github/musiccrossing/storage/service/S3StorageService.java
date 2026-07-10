package ru.github.musiccrossing.storage.service;

import io.awspring.cloud.s3.S3Template;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.github.musiccrossing.storage.FileType;

import java.io.ByteArrayInputStream;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class S3StorageService implements StorageService {

    private final S3Template s3Template;

    @Value("${app.s3-bucket}")
    private String bucket;

    @Value("${spring.cloud.aws.s3.endpoint}")
    private String endpoint;

    public String getEndpoint() {
        return endpoint;
    }

    public String getBucket() {
        return bucket;
    }

    public String generateUrl(final UUID id, final FileType fileType) {
        String key = id + fileType.getExtension();
        return endpoint + "/" + bucket + "/" + key;
    }

    @Override
    public String upload(final byte[] data, final UUID id, final FileType filetype) {
        final ByteArrayInputStream inputStream = new ByteArrayInputStream(data);
        final String key = id + filetype.getExtension();
        s3Template.upload(bucket, key, inputStream);
        return endpoint + "/" + bucket + "/" + key;
    }
}
