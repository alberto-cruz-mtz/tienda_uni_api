package tienda.uni.api.post.service.interfaces;

import tienda.uni.api.post.presentation.dto.UploadRequest;

public interface StorageService {

	String presignUpload(UploadTarget target, UploadRequest request);
}