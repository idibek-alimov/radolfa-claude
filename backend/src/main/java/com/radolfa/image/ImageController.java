package com.radolfa.image;

import com.radolfa.entity.Product;
import com.radolfa.repository.ProductRepository;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/**
 * Handles product-image uploads.
 * <p>
 * Restricted to the MANAGER role.  Until Prompt 05 wires JWT-based role
 * extraction, both a valid API key ({@code X-Sync-Api-Key}) and an
 * {@code X-Role: MANAGER} header are required as a temporary guard.
 * </p>
 */
@RestController
@RequestMapping("/api/v1/products")
@Slf4j
public class ImageController {

    private final ImageService      imageService;
    private final ProductRepository productRepository;

    @Value("${sync.api-key}")
    private String apiKey;

    public ImageController(ImageService imageService, ProductRepository productRepository) {
        this.imageService      = imageService;
        this.productRepository = productRepository;
    }

    /**
     * Upload an image for a product.
     * The image is validated, resized to WebP, stored in S3, and the resulting
     * URL is appended to the product's {@code images} list.
     */
    @Transactional
    @PostMapping("/{id}/images")
    public ResponseEntity<Map<String, String>> uploadImage(
            @PathVariable                                          Long         id,
            @RequestParam("file")                                  MultipartFile file,
            @RequestHeader(value = "X-Sync-Api-Key", required = false) String providedKey,
            @RequestHeader(value = "X-Role",         required = false) String role) {

        // Temporary MANAGER guard – replaced by JWT claims in Prompt 05
        if (!apiKey.equals(providedKey) || !"MANAGER".equals(role)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Requires a valid API key and MANAGER role.");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Product not found: " + id));

        String url = imageService.uploadImage(file);
        product.getImages().add(url);
        productRepository.save(product);

        log.info("ImageController: added image {} to product id={}.", url, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("url", url));
    }
}
