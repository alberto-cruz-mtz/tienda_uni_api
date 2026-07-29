package tienda.uni.api.auth.presentation.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import tienda.uni.api.auth.persistence.entity.ProfileEntity;

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
        String buildingName = profile.getBuilding().getName();
        return new UserResponse(null, fullName, profile.getPhotoUrl(), buildingName);
    }
}
