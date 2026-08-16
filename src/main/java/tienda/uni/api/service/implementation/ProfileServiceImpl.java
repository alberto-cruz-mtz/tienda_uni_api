package tienda.uni.api.service.implementation;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tienda.uni.api.persistence.repository.BuildingRepository;
import tienda.uni.api.persistence.repository.ProfileRepository;
import tienda.uni.api.presentation.dto.UpdateProfileRequest;
import tienda.uni.api.service.interfaces.ProfileService;

import java.util.UUID;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final BuildingRepository buildingRepository;

    @Override
    @Transactional
    public void updatePhotoAndBuildingOfProfile(UUID userId, UpdateProfileRequest request) {
        var profile = profileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.photoUrl() != null) {
            profile.setPhotoUrl(request.photoUrl());
        }

        if (request.buildingId() != null) {
            UUID buildingId = UUID.fromString(request.buildingId());

            var building = buildingRepository.findById(buildingId)
                    .orElseThrow(() -> new RuntimeException("Building not found"));

            profile.setBuilding(building);
        }

        profileRepository.save(profile);
    }
}
