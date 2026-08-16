package tienda.uni.api.service.interfaces;

import tienda.uni.api.presentation.dto.UpdateProfileRequest;

import java.util.UUID;

public interface ProfileService {

    void updatePhotoAndBuildingOfProfile(UUID userId, UpdateProfileRequest request);
}
