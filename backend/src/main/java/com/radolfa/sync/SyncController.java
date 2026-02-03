package com.radolfa.sync;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Incremental sync endpoint, intended to be called by an ERPNext webhook or a
 * scheduled poller on the ERP side.
 * <p>
 * A shared secret ({@code X-Sync-Api-Key} header) guards the endpoint.
 * Full role-based security (SYSTEM role + JWT) is layered on in Prompt 05.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/sync")
@Slf4j
public class SyncController {

    private final SyncService syncService;

    @Value("${sync.api-key}")
    private String syncApiKey;

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
            @RequestHeader(value = "X-Sync-Api-Key", required = false) String apiKey,
            @RequestBody List<ErpProductDto> products) {

        if (!syncApiKey.equals(apiKey)) {
            log.warn("SyncController: rejected request — invalid or missing API key.");
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid or missing API key"));
        }

        products.forEach(syncService::upsertProduct);

        log.info("SyncController: synced {} products.", products.size());
        return ResponseEntity.ok(Map.of("status", "success", "synced", products.size()));
    }
}
