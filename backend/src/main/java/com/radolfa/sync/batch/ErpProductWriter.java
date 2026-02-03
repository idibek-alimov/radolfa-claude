package com.radolfa.sync.batch;

import com.radolfa.entity.Product;
import com.radolfa.repository.ProductRepository;
import com.radolfa.repository.ProductSearchRepository;
import com.radolfa.search.ProductDocument;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

/**
 * Persists each product to PostgreSQL and re-indexes it into Elasticsearch in a single pass.
 * Using {@code erpId} as the ES document ID guarantees that repeated runs are idempotent.
 */
@Component
@Slf4j
public class ErpProductWriter implements ItemWriter<Product> {

    private final ProductRepository productRepository;
    private final ProductSearchRepository searchRepository;

    public ErpProductWriter(ProductRepository productRepository,
                            ProductSearchRepository searchRepository) {
        this.productRepository = productRepository;
        this.searchRepository = searchRepository;
    }

    @Override
    public void write(Chunk<? extends Product> products) throws Exception {
        for (Product product : products) {
            Product saved = productRepository.save(product);
            searchRepository.save(ProductDocument.from(saved));
        }
        log.info("ErpProductWriter: persisted and indexed {} products.", products.size());
    }
}
