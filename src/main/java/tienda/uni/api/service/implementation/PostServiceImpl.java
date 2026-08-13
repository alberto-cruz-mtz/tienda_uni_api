package tienda.uni.api.service.implementation;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tienda.uni.api.persistence.entity.PublicationEntity;
import tienda.uni.api.persistence.entity.PublicationMediaEntity;
import tienda.uni.api.persistence.entity.SalePersonEntity;
import tienda.uni.api.persistence.entity.TagEntity;
import tienda.uni.api.persistence.model.TagName;
import tienda.uni.api.persistence.repository.PostSpecification;
import tienda.uni.api.persistence.repository.ProductRepository;
import tienda.uni.api.persistence.repository.PublicationMediaRepository;
import tienda.uni.api.persistence.repository.PublicationRepository;
import tienda.uni.api.persistence.repository.SalePersonRepository;
import tienda.uni.api.persistence.repository.TagRepository;
import tienda.uni.api.presentation.dto.DataResponse;
import tienda.uni.api.presentation.dto.PaginationMetadata;
import tienda.uni.api.presentation.dto.PostParams;
import tienda.uni.api.presentation.dto.PostRequest;
import tienda.uni.api.presentation.dto.PostResponse;
import tienda.uni.api.service.interfaces.PostService;
import tienda.uni.api.util.ProductMapper;
import tienda.uni.api.util.PublicationMapper;
import tienda.uni.api.util.PublicationMediaMapper;

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

    private final PublicationMapper publicationMapper;
    private final ProductMapper productMapper;
    private final PublicationMediaMapper publicationMediaMapper;

    @Override
    public PostResponse createPost(PostRequest request, UUID userId) {
        SalePersonEntity salePerson = this.findSalePersonById(userId);
        var tags = this.findTagsByNames(request.tags());

        var publication = publicationMapper.toEntity(request, tags, salePerson);
        var savedPublication = publicationRepository.save(publication);

        var product = productMapper.toEntity(request.product(), savedPublication);
        var savedProduct = productRepository.save(product);

        List<PublicationMediaEntity> mediaContent = publicationMediaMapper.fromDto(request.mediaContent());
        publicationMediaRepository.saveAll(mediaContent);

        var tagNames = this.converterTagEntitiesToTagNames(tags);
        var productResponse = productMapper.fromEntity(savedProduct);

        return publicationMapper.toDto(savedPublication, tagNames, productResponse, request.mediaContent());
    }

    @Override
    @Transactional(readOnly = true)
    public DataResponse<PostResponse> getAllPosts(PostParams params) {
        var publications = this.findPublicationsBySearchCriteria(params);

        var pagination = this.converterFromPageToPaginationMetadata(publications);
        var posts = publicationMapper.toPostResponse(publications.toList());

        return new DataResponse<>(pagination, posts);
    }

    @Override
    @Transactional(readOnly = true)
    public PostResponse getPostById(UUID id, UUID universityId) {
        return publicationRepository.findByIdAndUniversityId(id, universityId)
                .map(publicationMapper::toPostResponse)
                .orElseThrow(() -> new RuntimeException("publication not found"));
    }

    private PaginationMetadata converterFromPageToPaginationMetadata(Page<PublicationEntity> page) {
        var nextLink = page.hasNext() ? "/posts?page=" + (page.getNumber() + 1) : null;

        return new PaginationMetadata(
                page.getNumber(),
                page.getSize(),
                page.getNumberOfElements(),
                nextLink
        );
    }

    private Page<PublicationEntity> findPublicationsBySearchCriteria(PostParams params) {
        var specification = Specification
                .where(PostSpecification.fetchRelations())
                .and(PostSpecification.getOnlyPublicationsByUniversity(params.universityId()))
                .and(PostSpecification.searchByTitle(params.search()))
                .and(PostSpecification.filterByStock(params.isOutOfStock()));

        return publicationRepository.findAll(specification, params.pageable());
    }

    private List<TagName> converterTagEntitiesToTagNames(List<TagEntity> tagEntities) {
        return tagEntities.stream().map(TagEntity::getName).toList();
    }

    private SalePersonEntity findSalePersonById(UUID id) {
        String message = "Aun no estas registrado como vendedor, por favor registrate para poder crear publicaciones";
        return salePersonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(message));
    }

    private List<TagEntity> findTagsByNames(List<TagName> names) {
        return tagRepository.findTagEntitiesByNameIn(names).stream().toList();
    }
}
