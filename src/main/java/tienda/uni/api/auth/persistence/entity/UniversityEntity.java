package tienda.uni.api.auth.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "universities")
public class UniversityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false, unique = true, length = 160)
    private String name;

    @Column(name = "acronym", nullable = false, unique = true, length = 10)
    private String acronym;

    @Column(name = "brand_color", nullable = false, length = 15)
    private String brandColor;

    @Column(name = "logo_url", nullable = false)
    private String logoUrl;

    @Column(name = "state", nullable = false, length = 50)
    private String state;

    @Column(name = "city", nullable = false, length = 50)
    private String city;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @ElementCollection
    @CollectionTable(name = "university_domains", joinColumns = @JoinColumn(name = "university_id"))
    @Column(name = "domain", unique = true, nullable = false, length = 75)
    private Set<String> emailDomains;
}