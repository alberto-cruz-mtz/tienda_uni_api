package tienda.uni.api.post.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.type.PostgreSQLEnumJdbcType;
import tienda.uni.api.post.persistence.model.SaleType;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "products")
public class ProductEntity {

    @Id
    @Column(name = "publication_id")
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false, targetEntity = PublicationEntity.class)
    @JoinColumn(name = "publication_id", nullable = false, unique = true)
    private PublicationEntity publication;

    @Column(name = "sale_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal salePrice;

    @Column(name = "inventory", nullable = false, precision = 10, scale = 2)
    private BigDecimal inventory;

    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "type_sale", nullable = false)
    private SaleType saleType;

    @Column(name = "allows_layaway", nullable = false)
    private boolean allowsLayaway;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private boolean active = true;
}
