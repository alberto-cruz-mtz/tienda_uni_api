package tienda.uni.api.service.implementation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import tienda.uni.api.configuration.S3Properties;
import tienda.uni.api.presentation.dto.BatchUploadRequest;
import tienda.uni.api.presentation.dto.BatchUploadResponse;
import tienda.uni.api.presentation.dto.FileMetadata;
import tienda.uni.api.presentation.dto.PresignedUrlItem;
import tienda.uni.api.presentation.dto.UploadRequest;
import tienda.uni.api.service.interfaces.StorageService;
import tienda.uni.api.service.interfaces.UploadTarget;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final S3Presigner s3Presigner;
    private final S3Properties properties;

    private final Duration FIVE_MINUTES = Duration.ofMinutes(5);

    @Override
    public String presignUpload(@NonNull UploadTarget target, UploadRequest request) {
        String bucketName = this.determineBucketName(target);
        String uniqueFileName = this.generateKey(request.fileName());

        return this.generatePresignedUrl(bucketName, uniqueFileName, request.contentType());
    }

    @Override
    public BatchUploadResponse presignBatchUpload(UploadTarget target, BatchUploadRequest request) {
        String bucketName = this.determineBucketName(target);
        List<PresignedUrlItem> presignedUrlItems = this.generatePresignedUrls(bucketName, request.files());

        return new BatchUploadResponse(presignedUrlItems);
    }

    private String determineBucketName(UploadTarget target) {
        return switch (target) {
            case PROFILE_PICTURE -> properties.buckets().profilePictures().name();
            case PUBLICATION_MEDIA -> properties.buckets().postMedia().name();
        };
    }

    private String generateKey(String fileName) {
        return UUID.randomUUID() + "/" + fileName;
    }

    private PutObjectRequest createPutObjectRequest(String bucket, String key, String contentType) {
        return PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();
    }

    private PutObjectPresignRequest createPutObjectPresignRequest(PutObjectRequest objectRequest) {
        return PutObjectPresignRequest.builder()
                .signatureDuration(FIVE_MINUTES)
                .putObjectRequest(objectRequest)
                .build();
    }

    private String generatePresignedUrl(String bucketName, String key, String contentType) {
        PutObjectRequest objectRequest = this.createPutObjectRequest(bucketName, key, contentType);
        PutObjectPresignRequest presignRequest = this.createPutObjectPresignRequest(objectRequest);
        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        return presignedRequest.url().toString();
    }

    private List<PresignedUrlItem> generatePresignedUrls(String bucketName, List<FileMetadata> files) {
        return files.stream()
                .map(file -> {
                    String uniqueFileName = this.generateKey(file.fileName());
                    String uploadUrl = this.generatePresignedUrl(bucketName, uniqueFileName, file.contentType());
                    return new PresignedUrlItem(file.fileId(), uploadUrl, uniqueFileName);
                }).toList();
    }
}