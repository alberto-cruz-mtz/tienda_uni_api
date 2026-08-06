package tienda.uni.api.post.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tienda.uni.api.auth.persistence.model.AuthenticatedUser;
import tienda.uni.api.post.presentation.dto.PostRequest;
import tienda.uni.api.post.presentation.dto.PostResponse;
import tienda.uni.api.post.service.interfaces.PostService;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

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
}
