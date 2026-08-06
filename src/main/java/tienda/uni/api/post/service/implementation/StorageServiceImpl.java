package tienda.uni.api.post.service.implementation;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import tienda.uni.api.post.configuration.S3Properties;
import tienda.uni.api.post.presentation.dto.UploadRequest;
import tienda.uni.api.post.service.interfaces.StorageService;
import tienda.uni.api.post.service.interfaces.UploadTarget;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {

    private final S3Presigner s3Presigner;
    private final S3Properties properties;

    @Override
    public String presignUpload(@NonNull UploadTarget target, UploadRequest request) {
        String bucketName = switch (target) {
            case PROFILE_PICTURE -> properties.buckets().profilePictures();
            case PUBLICATION_MEDIA -> properties.buckets().postMedia();
        };

        String uniqueFileName = UUID.randomUUID() + "-" + request.fileName();

        PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueFileName)
                .contentType(request.contentType())
                .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(objectRequest)
                .build();

        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        return presignedRequest.url().toString();
    }
}
