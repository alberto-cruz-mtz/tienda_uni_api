package tienda.uni.api.auth.persistence.model;

public enum Role {
    UNVERIFIED,
    CUSTOMER,
    SELLER;

    private static final String SPRING_ROLE_PREFIX = "ROLE_";

    public String authority() {
        return SPRING_ROLE_PREFIX + name();
    }
}