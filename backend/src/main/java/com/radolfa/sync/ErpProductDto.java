package com.radolfa.sync;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Payload shape for a single product arriving from ERPNext.
 * Used by both the batch reader and the incremental sync controller.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErpProductDto {

    /** ERPNext "name" field — the unique document key inside ERPNext. */
    private String erpId;

    /** ERPNext "item_name" — human-readable product name. */
    private String name;

    /** Selling price sourced from ERPNext. */
    private BigDecimal price;

    /** Current in-stock quantity sourced from ERPNext. */
    private Integer stock;
}
