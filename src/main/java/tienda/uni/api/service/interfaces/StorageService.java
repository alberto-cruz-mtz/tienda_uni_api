package tienda.uni.api.service.interfaces;

import tienda.uni.api.presentation.dto.BatchUploadRequest;
import tienda.uni.api.presentation.dto.BatchUploadResponse;
import tienda.uni.api.presentation.dto.UploadRequest;

public interface StorageService {

    String presignUpload(UploadTarget target, UploadRequest request);

    BatchUploadResponse presignBatchUpload(UploadTarget target, BatchUploadRequest request);
}