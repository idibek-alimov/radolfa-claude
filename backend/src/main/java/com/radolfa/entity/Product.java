package com.radolfa.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Product entity following the Enrichment Pattern.
 * <p>
 * Fields marked LOCKED are managed exclusively by ERPNext via the sync job.
 * Fields marked EDITABLE are maintained by the Radolfa application itself.
 * ERPNext is the single source of truth for locked fields; never overwrite them locally.
 * </p>
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** LOCKED – Unique identifier from ERPNext. Used as the sync key. */
    @Column(name = "erp_id", unique = true, nullable = false)
    private String erpId;

    /** LOCKED – Product name, owned by ERPNext. */
    @Column(name = "name", nullable = false, length = 500)
    private String name;

    /** LOCKED – Unit price, owned by ERPNext. */
    @Column(name = "price", nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    /** LOCKED – Current stock quantity, owned by ERPNext. */
    @Column(name = "stock", nullable = false)
    private Integer stock;

    /** EDITABLE – Marketing description written for the web storefront. */
    @Column(name = "web_description", columnDefinition = "TEXT")
    private String webDescription;

    /** EDITABLE – Flag to surface this product in featured / top-selling sections. */
    @Column(name = "is_top_selling", nullable = false)
    private Boolean isTopSelling = false;

    /** EDITABLE – S3 URLs of product images, managed via the image upload pipeline. */
    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url", length = 1024)
    private List<String> images = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
