package com.radolfa.sync;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Envelope returned by ERPNext's {@code /api/resource/*} endpoints.
 * <pre>
 * {
 *   "data": [ { "name": "...", "item_name": "...", ... }, ... ]
 * }
 * </pre>
 */
@Getter
@Setter
@NoArgsConstructor
public class ErpResourceResponse {

    private List<Map<String, Object>> data;
}
