package tienda.uni.api.post.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tienda.uni.api.auth.persistence.model.AuthenticatedUser;
import tienda.uni.api.post.presentation.dto.BatchUploadRequest;
import tienda.uni.api.post.presentation.dto.BatchUploadResponse;
import tienda.uni.api.post.presentation.dto.DataResponse;
import tienda.uni.api.post.presentation.dto.PostRequest;
import tienda.uni.api.post.presentation.dto.PostResponse;
import tienda.uni.api.post.service.interfaces.PostService;
import tienda.uni.api.post.service.interfaces.StorageService;
import tienda.uni.api.post.service.interfaces.UploadTarget;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final StorageService storageService;

    @PostMapping
    public ResponseEntity<PostResponse> createPost(
            @RequestBody @Valid PostRequest postRequest,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        UUID userId = authenticatedUser.getUser().getId();
        PostResponse response = postService.createPost(postRequest, userId);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<DataResponse<PostResponse>> getPosts(
            @AuthenticationPrincipal AuthenticatedUser userDetails,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "isOutOfStock", required = false, defaultValue = "false") boolean isOutOfStock,
            @PageableDefault(sort = "postedAt", direction = Sort.Direction.DESC) Pageable pageable) {

        UUID universityId = userDetails.getUniversityId();

        var response = postService.getAllPosts(pageable, universityId, search, isOutOfStock);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/presigned-urls")
    public ResponseEntity<BatchUploadResponse> getPresignedUrlForMediaPublication(@RequestBody @Valid BatchUploadRequest request) {
        var response = storageService.presignBatchUpload(UploadTarget.PUBLICATION_MEDIA, request);
        return ResponseEntity.ok(response);
    }
}
