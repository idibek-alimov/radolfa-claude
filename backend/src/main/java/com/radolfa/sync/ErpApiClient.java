package com.radolfa.sync;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * HTTP client for ERPNext's Item resource.
 * <p>
 * Authentication uses the ERPNext token scheme:
 * {@code Authorization: token <api_key>:<api_secret>}
 * </p>
 */
@Component
@Slf4j
public class ErpApiClient {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${erp.api.base-url}")
    private String baseUrl;

    @Value("${erp.api.api-key}")
    private String apiKey;

    @Value("${erp.api.api-secret}")
    private String apiSecret;

    @Value("${erp.api.page-size:100}")
    private int pageSize;

    /**
     * Fetches one page of Items starting at {@code offset}.
     * Returns an empty list when ERPNext has no more records at that offset.
     */
    public List<ErpProductDto> fetchProducts(int offset) {
        String url = String.format(
                "%s/api/resource/Item"
                        + "?fields=[\"name\",\"item_name\",\"standard_rate\",\"actual_qty\"]"
                        + "&limit_page_length=%d"
                        + "&limit_start=%d"
                        + "&order_by=name asc",
                baseUrl, pageSize, offset
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "token " + apiKey + ":" + apiSecret);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        ResponseEntity<ErpResourceResponse> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(headers), ErpResourceResponse.class
        );

        List<ErpProductDto> results = new ArrayList<>();
        if (response.getBody() == null || response.getBody().getData() == null) {
            return results;
        }

        for (Map<String, Object> row : response.getBody().getData()) {
            ErpProductDto dto = new ErpProductDto();
            dto.setErpId((String) row.get("name"));
            dto.setName((String) row.get("item_name"));
            dto.setPrice(row.get("standard_rate") != null
                    ? new BigDecimal(row.get("standard_rate").toString())
                    : BigDecimal.ZERO);
            dto.setStock(row.get("actual_qty") != null
                    ? ((Number) row.get("actual_qty")).intValue()
                    : 0);
            results.add(dto);
        }

        log.debug("ErpApiClient: fetched {} items at offset {}.", results.size(), offset);
        return results;
    }
}
