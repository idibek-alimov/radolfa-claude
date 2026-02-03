package com.radolfa.search;

import com.radolfa.entity.Product;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "products")
@Getter
@Setter
@NoArgsConstructor
public class ProductDocument {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String erpId;

    @Field(type = FieldType.Text, analyzer = "standard")
    private String name;

    @Field(type = FieldType.Double)
    private BigDecimal price;

    @Field(type = FieldType.Integer)
    private Integer stock;

    @Field(type = FieldType.Text)
    private String webDescription;

    @Field(type = FieldType.Boolean)
    private boolean topSelling;

    /**
     * Maps a JPA Product entity onto an Elasticsearch document.
     * The ES document ID is set to {@code erpId} so that re-indexing is idempotent.
     */
    public static ProductDocument from(Product product) {
        ProductDocument doc = new ProductDocument();
        doc.setId(product.getErpId());
        doc.setErpId(product.getErpId());
        doc.setName(product.getName());
        doc.setPrice(product.getPrice());
        doc.setStock(product.getStock());
        doc.setWebDescription(product.getWebDescription());
        doc.setTopSelling(Boolean.TRUE.equals(product.getIsTopSelling()));
        return doc;
    }
}
