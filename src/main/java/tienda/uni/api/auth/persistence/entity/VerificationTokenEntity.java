package tienda.uni.api.auth.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "verification_tokens")
public class VerificationTokenEntity extends AuditableEntity {

    @Id
    @Column(name = "user_id", nullable = false)
    private UUID id;

    @MapsId
    @OneToOne(optional = false, orphanRemoval = true, fetch = FetchType.LAZY, targetEntity = UserEntity.class)
    @JoinColumn(nullable = false, name = "user_id")
    private UserEntity user;

    @Column(name = "token", nullable = false)
    private UUID token;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    public static VerificationTokenEntity create(UserEntity user) {
        return VerificationTokenEntity.builder()
                .user(user)
                .token(UUID.randomUUID())
                .expiresAt(Instant.now().plusSeconds(900)) // 15 minutes
                .build();
    }

    public boolean isExpired() {
        return Instant.now().isAfter(this.expiresAt);
    }

    public boolean hasAtLeastFiveMinutesOfLife() {
        return Duration.between(Instant.now(), this.expiresAt).compareTo(Duration.ofMinutes(5)) >= 0;
    }

    public void resetExpiresAt() {
        this.expiresAt = Instant.now().plusSeconds(900);
    }
}
