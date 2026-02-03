package com.radolfa.sync;

import com.radolfa.entity.Product;
import com.radolfa.repository.ProductRepository;
import com.radolfa.repository.ProductSearchRepository;
import com.radolfa.search.ProductDocument;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Orchestrates a single-product upsert for the incremental sync API.
 * The same Enrichment-Pattern rules apply: locked fields are overwritten,
 * editable fields are left intact.
 */
@Service
@Slf4j
public class SyncService {

    private final ProductRepository productRepository;
    private final ProductSearchRepository searchRepository;

    public SyncService(ProductRepository productRepository,
                       ProductSearchRepository searchRepository) {
        this.productRepository = productRepository;
        this.searchRepository = searchRepository;
    }

    @Transactional
    public Product upsertProduct(ErpProductDto dto) {
        Product product = productRepository
                .findByErpId(dto.getErpId())
                .orElseGet(Product::new);

        product.setErpId(dto.getErpId());
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());

        Product saved = productRepository.save(product);
        searchRepository.save(ProductDocument.from(saved));

        log.info("SyncService: upserted product erpId={}.", dto.getErpId());
        return saved;
    }
}
