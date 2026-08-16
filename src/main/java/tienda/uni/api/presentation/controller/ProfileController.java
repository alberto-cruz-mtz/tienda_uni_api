package tienda.uni.api.presentation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tienda.uni.api.persistence.model.AuthenticatedUser;
import tienda.uni.api.presentation.dto.UpdateProfileRequest;
import tienda.uni.api.presentation.dto.UploadRequest;
import tienda.uni.api.presentation.dto.UploadResponse;
import tienda.uni.api.service.interfaces.ProfileService;
import tienda.uni.api.service.interfaces.StorageService;
import tienda.uni.api.service.interfaces.UploadTarget;

import java.util.UUID;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final StorageService storageService;

    @PatchMapping
    public ResponseEntity<Void> updateProfilePhoto(
            @AuthenticationPrincipal AuthenticatedUser userDetails,
            @RequestBody @Valid UpdateProfileRequest request) {
        UUID userId = userDetails.getUserId();
        profileService.updatePhotoAndBuildingOfProfile(userId, request);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/presigned-url")
    public ResponseEntity<UploadResponse> generatePresignedUrlForProfilePhoto(@RequestBody @Valid UploadRequest request) {
        var presignedUrl = storageService.presignUpload(UploadTarget.PROFILE_PICTURE, request);
        var response = new UploadResponse(presignedUrl);
        return ResponseEntity.ok(response);
    }

}
