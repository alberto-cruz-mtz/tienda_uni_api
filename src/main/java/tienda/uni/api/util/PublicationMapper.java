package tienda.uni.api.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tienda.uni.api.persistence.entity.PublicationEntity;
import tienda.uni.api.persistence.entity.SalePersonEntity;
import tienda.uni.api.persistence.entity.TagEntity;
import tienda.uni.api.persistence.model.TagName;
import tienda.uni.api.presentation.dto.MediaContentRequest;
import tienda.uni.api.presentation.dto.PostRequest;
import tienda.uni.api.presentation.dto.PostResponse;
import tienda.uni.api.presentation.dto.ProductResponse;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PublicationMapper {

    private final PublicationMediaMapper publicationMediaMapper;
    private final ProductMapper productMapper;

    public PublicationEntity fromDto(PostRequest dto) {
        var publishAt = dto.publishRightAway() ? Instant.now() : null;

        return PublicationEntity.builder()
                .title(dto.title())
                .description(dto.description())
                .expiresAt(dto.hiddenUntil())
                .postedAt(publishAt)
                .build();
    }

    public PublicationEntity toEntity(PostRequest dto, List<TagEntity> tags, SalePersonEntity salePerson) {
        var publication = this.fromDto(dto);
        publication.setTags(tags);
        publication.setSalePerson(salePerson);
        return publication;
    }

    public PostResponse toDto(PublicationEntity entity, List<TagName> tagNames, ProductResponse productResponse, List<MediaContentRequest> mediaContentRequest) {
        return new PostResponse(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                tagNames,
                mediaContentRequest,
                productResponse,
                entity.getPostedAt()
        );
    }

    public PostResponse toPostResponse(PublicationEntity publication) {
        var mediaContent = publicationMediaMapper.fromEntity(publication.getMedia());
        var productResponse = productMapper.fromEntity(publication.getProduct());
        var tags = publication.getTags().stream().map(TagEntity::getName).toList();

        return this.toDto(publication, tags, productResponse, mediaContent);
    }

    public List<PostResponse> toPostResponse(List<PublicationEntity> publications) {
        return publications.stream().map(this::toPostResponse).toList();
    }
}
