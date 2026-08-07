package tienda.uni.api.post.service.interfaces;

import org.springframework.data.domain.Pageable;
import tienda.uni.api.post.presentation.dto.DataResponse;
import tienda.uni.api.post.presentation.dto.PostRequest;
import tienda.uni.api.post.presentation.dto.PostResponse;

import java.util.UUID;

public interface PostService {

    PostResponse createPost(PostRequest postRequest, UUID userId);

    DataResponse<PostResponse> getAllPosts(Pageable pageable, UUID universityId);
}
