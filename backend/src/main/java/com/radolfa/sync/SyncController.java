package com.radolfa.sync;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Incremental sync endpoint, intended to be called by an ERPNext webhook or a
 * scheduled poller on the ERP side.
 * <p>
 * Access is restricted to the {@code SYSTEM} role via Spring Security
 * ({@link com.radolfa.config.SecurityConfig}).  The
 * {@link com.radolfa.security.JwtAuthenticationFilter} grants that role either
 * through a valid JWT or through the {@code X-Sync-Api-Key} header.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/sync")
@Slf4j
public class SyncController {

    private final SyncService syncService;

    public SyncController(SyncService syncService) {
        this.syncService = syncService;
    }

    /**
     * Accepts a JSON array of ERP product payloads and upserts each one.
     * Matches by {@code erpId}; overwrites {@code name/price/stock};
     * preserves {@code webDescription/isTopSelling/images}.
     */
    @PostMapping("/products")
    public ResponseEntity<Map<String, Object>> syncProducts(
            @RequestBody List<ErpProductDto> products) {

        products.forEach(syncService::upsertProduct);

        log.info("SyncController: synced {} products.", products.size());
        return ResponseEntity.ok(Map.of("status", "success", "synced", products.size()));
    }
}
