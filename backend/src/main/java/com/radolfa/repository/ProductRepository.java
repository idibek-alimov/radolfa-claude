package com.radolfa.repository;

import com.radolfa.entity.Product;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByErpId(String erpId);
}
