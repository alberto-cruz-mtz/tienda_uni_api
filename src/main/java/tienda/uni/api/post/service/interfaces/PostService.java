package tienda.uni.api.post.service.interfaces;

import tienda.uni.api.post.presentation.dto.PostRequest;
import tienda.uni.api.post.presentation.dto.PostResponse;

import java.util.UUID;

public interface PostService {

    PostResponse createPost(PostRequest postRequest, UUID userId);
}
