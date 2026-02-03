package com.radolfa.sync.batch;

import com.radolfa.entity.Product;
import com.radolfa.repository.ProductRepository;
import com.radolfa.sync.ErpProductDto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

/**
 * Maps an ERPNext DTO onto a JPA {@link Product} entity using the Enrichment Pattern.
 * <ul>
 *   <li><b>LOCKED</b> fields ({@code name}, {@code price}, {@code stock}) are always overwritten.</li>
 *   <li><b>EDITABLE</b> fields ({@code webDescription}, {@code isTopSelling}, {@code images})
 *       are never touched — they retain whatever value the application last set.
 *       For brand-new products they stay at their defaults (null / false / empty list).</li>
 * </ul>
 */
@Component
@Slf4j
public class ErpProductProcessor implements ItemProcessor<ErpProductDto, Product> {

    private final ProductRepository productRepository;

    public ErpProductProcessor(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product process(ErpProductDto dto) {
        Product product = productRepository
                .findByErpId(dto.getErpId())
                .orElseGet(Product::new);

        boolean isNew = (product.getId() == null);

        // Sync key — required for both new and existing entities
        product.setErpId(dto.getErpId());

        // Overwrite LOCKED fields
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());

        // EDITABLE fields intentionally left untouched

        log.debug("ErpProductProcessor: {} product erpId={}.", isNew ? "NEW" : "UPDATE", dto.getErpId());
        return product;
    }
}
