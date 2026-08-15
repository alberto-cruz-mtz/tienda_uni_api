package tienda.uni.api.service.interfaces;

import tienda.uni.api.presentation.dto.DataResponse;
import tienda.uni.api.presentation.dto.PostParams;
import tienda.uni.api.presentation.dto.PostRequest;
import tienda.uni.api.presentation.dto.PostResponse;

import java.util.UUID;

public interface PostService {

    PostResponse createPost(PostRequest postRequest, UUID userId);

    DataResponse<PostResponse> getAllPosts(PostParams params);

    PostResponse getPostById(UUID id, UUID universityId);
}
