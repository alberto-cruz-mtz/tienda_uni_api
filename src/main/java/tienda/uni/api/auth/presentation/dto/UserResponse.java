package tienda.uni.api.auth.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import tienda.uni.api.auth.persistence.entity.BuildingEntity;
import tienda.uni.api.auth.persistence.entity.ProfileEntity;

import java.util.Optional;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserResponse(
        String email,
        String name,
        String avatarUrl,
        String building
) {

    public static UserResponse forRegistration(String email, String name) {
        return new UserResponse(email, name, null, null);
    }

    public static UserResponse forAuthentication(ProfileEntity profile) {
        String fullName = profile.getFirstName() + " " + profile.getLastName();

        var buildingName = Optional.of(profile)
                .map(ProfileEntity::getBuilding)
                .map(BuildingEntity::getName)
                .orElse("Aun no ha sido asignado su edificio");

        return new UserResponse(null, fullName, profile.getPhotoUrl(), buildingName);
    }
}
