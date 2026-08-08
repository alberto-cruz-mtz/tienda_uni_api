package tienda.uni.api.post.util;

import org.springframework.stereotype.Component;
import tienda.uni.api.post.persistence.entity.PublicationEntity;
import tienda.uni.api.post.persistence.entity.PublicationMediaEntity;
import tienda.uni.api.post.presentation.dto.MediaContentRequest;
import tienda.uni.api.post.presentation.dto.PostRequest;

import java.util.List;

@Component
public class PublicationMediaMapper {

    public PublicationMediaEntity fromDto(MediaContentRequest dto) {
        return PublicationMediaEntity.builder()
                .mediaUrl(dto.url())
                .mediaType(dto.type())
                .displayOrder(dto.position())
                .build();
    }

    public List<PublicationMediaEntity> fromDto(List<MediaContentRequest> dtoList) {
        return dtoList.stream()
                .map(this::fromDto)
                .toList();
    }

    public MediaContentRequest fromEntity(PublicationMediaEntity entity) {
        return new MediaContentRequest(
                entity.getMediaUrl(),
                entity.getMediaType(),
                entity.getDisplayOrder()
        );
    }

    public List<MediaContentRequest> fromEntity(List<PublicationMediaEntity> entityList) {
        return entityList.stream()
                .map(this::fromEntity)
                .toList();
    }
}
