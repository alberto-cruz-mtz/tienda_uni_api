package tienda.uni.api.post.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tienda.uni.api.post.persistence.entity.ProductEntity;
import tienda.uni.api.post.persistence.entity.PublicationEntity;
import tienda.uni.api.post.persistence.entity.PublicationMediaEntity;
import tienda.uni.api.post.persistence.entity.SalePersonEntity;
import tienda.uni.api.post.persistence.entity.TagEntity;
import tienda.uni.api.post.persistence.repository.ProductRepository;
import tienda.uni.api.post.persistence.repository.PublicationMediaRepository;
import tienda.uni.api.post.persistence.repository.PublicationRepository;
import tienda.uni.api.post.persistence.repository.SalePersonRepository;
import tienda.uni.api.post.persistence.repository.TagRepository;
import tienda.uni.api.post.presentation.dto.MediaContentRequest;
import tienda.uni.api.post.presentation.dto.PostRequest;
import tienda.uni.api.post.presentation.dto.PostResponse;
import tienda.uni.api.post.service.interfaces.PostService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PublicationRepository publicationRepository;
    private final PublicationMediaRepository publicationMediaRepository;
    private final SalePersonRepository salePersonRepository;
    private final TagRepository tagRepository;
    private final ProductRepository productRepository;

    @Override
    public PostResponse createPost(PostRequest request, UUID userId) {
        SalePersonEntity salePerson = salePersonRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Aun no estas registrado como vendedor, por favor registrate para poder crear publicaciones"));

        var tags = tagRepository.findTagEntitiesByNameIn(request.tags()).stream().toList();

        var publishAt = request.publishRightAway() ? Instant.now() : null;

        var publication = PublicationEntity.builder()
                .title(request.title())
                .description(request.description())
                .postedAt(publishAt)
                .expiresAt(request.hiddenUntil())
                .salePerson(salePerson)
                .tags(tags)
                .build();

        var savedPublication = publicationRepository.save(publication);

        var product = ProductEntity.builder()
                .salePrice(new BigDecimal(request.product().price()))
                .inventory(new BigDecimal(request.product().quantity()))
                .saleType(request.product().typeSale())
                .allowsLayaway(request.product().allowsLayaway())
                .publication(savedPublication)
                .build();

        productRepository.save(product);

        List<PublicationMediaEntity> mediaContent = request.mediaContent().stream()
                .map((media) -> {
                    return PublicationMediaEntity.builder()
                            .mediaUrl(media.url())
                            .mediaType(media.type())
                            .displayOrder(media.position())
                            .publication(savedPublication)
                            .build();
                })
                .toList();

        publicationMediaRepository.saveAll(mediaContent);

        var tagNames = tags.stream().map(TagEntity::getName).toList();

        return new PostResponse(
                savedPublication.getId(),
                savedPublication.getTitle(),
                savedPublication.getDescription(),
                tagNames,
                request.mediaContent(),
                request.product()
        );
    }
}
