package tienda.uni.api.post.persistence.entity;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tienda.uni.api.auth.persistence.entity.BuildingEntity;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "publications")
public class PublicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "title", nullable = false, length = 80)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Builder.Default
    @Column(name = "posted_at", nullable = false)
    private Instant postedAt = Instant.now();

    @Builder.Default
    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @ManyToOne(fetch = FetchType.LAZY, targetEntity = BuildingEntity.class)
    @JoinColumn(name = "building_id")
    private BuildingEntity building;

    @OneToMany(targetEntity = PublicationMediaEntity.class, fetch = FetchType.LAZY, mappedBy = "publication")
    private List<PublicationMediaEntity> media;

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = TagEntity.class)
    @JoinTable(name = "tag_publication", joinColumns = @JoinColumn(name = "publication_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "tag_id", nullable = false))
    private List<TagEntity> tags;
}