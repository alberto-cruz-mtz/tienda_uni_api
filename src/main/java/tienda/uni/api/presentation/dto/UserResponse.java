package tienda.uni.api.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import tienda.uni.api.persistence.entity.BuildingEntity;
import tienda.uni.api.persistence.entity.ProfileEntity;

import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(
        String email,
        String name,
        String avatarUrl,
        String building
) {

    public static UserResponse createResponseForRegistration(String email, String name) {
        return new UserResponse(email, name, null, null);
    }

    public static UserResponse createResponseForAuthentication(ProfileEntity profile, String fileManagerUrl) {
        String fullName = profile.getFirstName() + " " + profile.getLastName();
        String avatarUrl = Optional.ofNullable(profile.getPhotoUrl())
                .map(photoUrl -> fileManagerUrl + "/" + photoUrl)
                .orElse(null);

        var buildingName = Optional.of(profile)
                .map(ProfileEntity::getBuilding)
                .map(BuildingEntity::getName)
                .orElse("Aun no ha sido asignado su edificio");

        return new UserResponse(null, fullName, avatarUrl, buildingName);
    }
}
