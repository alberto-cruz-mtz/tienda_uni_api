package tienda.uni.api.post.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tienda.uni.api.post.persistence.entity.ProductEntity;
import tienda.uni.api.post.persistence.entity.PublicationEntity;
import tienda.uni.api.post.persistence.entity.PublicationMediaEntity;
import tienda.uni.api.post.persistence.entity.SalePersonEntity;
import tienda.uni.api.post.persistence.entity.TagEntity;
import tienda.uni.api.post.persistence.repository.PostSpecification;
import tienda.uni.api.post.persistence.repository.ProductRepository;
import tienda.uni.api.post.persistence.repository.PublicationMediaRepository;
import tienda.uni.api.post.persistence.repository.PublicationRepository;
import tienda.uni.api.post.persistence.repository.SalePersonRepository;
import tienda.uni.api.post.persistence.repository.TagRepository;
import tienda.uni.api.post.presentation.dto.DataResponse;
import tienda.uni.api.post.presentation.dto.MediaContentRequest;
import tienda.uni.api.post.presentation.dto.PaginationMetadata;
import tienda.uni.api.post.presentation.dto.PostRequest;
import tienda.uni.api.post.presentation.dto.PostResponse;
import tienda.uni.api.post.presentation.dto.ProductResponse;
import tienda.uni.api.post.service.interfaces.PostService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
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

        var savedProduct = productRepository.save(product);

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

        var productResponse = new ProductResponse(
                savedProduct.getInventory().intValue(),
                savedProduct.getSalePrice().doubleValue(),
                savedProduct.getSaleType(),
                savedProduct.isAllowsLayaway(),
                savedProduct.getInventory().compareTo(BigDecimal.ZERO) == 0
        );

        return new PostResponse(
                savedPublication.getId(),
                savedPublication.getTitle(),
                savedPublication.getDescription(),
                tagNames,
                request.mediaContent(),
                productResponse,
                savedPublication.getPostedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponse<PostResponse> getAllPosts(Pageable pageable, UUID universityId, String search, boolean isOutOfStock) {
        var specification = Specification
                .where(PostSpecification.fetchRelations())
                .and(PostSpecification.getOnlyPublicationsByUniversity(universityId))
                // TODO: cambiar esos valores por los que vienen en el request, se deja asi para comprobar el correcto funcionamiento de la paginacion y el filtrado
                .and(PostSpecification.searchByTitle(search))
                .and(PostSpecification.filterByStock(isOutOfStock));

        var publications = publicationRepository.findAll(specification, pageable);

        var nextLink = publications.hasNext() ? "/posts?page=" + (publications.getNumber() + 1) : null;

        var pagination = new PaginationMetadata(
                publications.getNumber(),
                publications.getSize(),
                publications.getNumberOfElements(),
                nextLink
        );

        var posts = publications.stream()
                .map((publication) -> {
                    return new PostResponse(
                            publication.getId(),
                            publication.getTitle(),
                            publication.getDescription(),
                            publication.getTags().stream().map(TagEntity::getName).toList(),
                            publication.getMedia().stream()
                                    .map((media) -> new MediaContentRequest(
                                            media.getMediaUrl(),
                                            media.getMediaType(),
                                            media.getDisplayOrder()
                                    ))
                                    .toList(),
                            new ProductResponse(
                                    publication.getProduct().getInventory().intValue(),
                                    publication.getProduct().getSalePrice().doubleValue(),
                                    publication.getProduct().getSaleType(),
                                    publication.getProduct().isAllowsLayaway(),
                                    publication.getProduct().getInventory().compareTo(BigDecimal.ZERO) == 0
                            ),
                            publication.getPostedAt()
                    );
                })
                .toList();

        return new DataResponse<>(pagination, posts);
    }
}
