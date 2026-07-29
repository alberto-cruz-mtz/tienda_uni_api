package tienda.uni.api.auth.persistence.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "email", nullable = false, unique = true, length = 120)
    private String email;

    @Column(name = "password", nullable = false, length = 300)
    private String password;

    @Column(name = "verified", nullable = false)
    private boolean verified;

    @JoinColumn(name = "university_id", nullable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, targetEntity = UniversityEntity.class)
    private UniversityEntity university;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, targetEntity = ProfileEntity.class, orphanRemoval = true)
    private ProfileEntity profile;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RoleEntity> roles;

    public static UserEntity create(String email, String password, Set<RoleEntity> roles, ProfileEntity profile, UniversityEntity university) {
        return UserEntity.builder()
                .email(email)
                .password(password)
                .roles(roles)
                .university(university)
                .profile(profile)
                .verified(false)
                .build();
    }
}