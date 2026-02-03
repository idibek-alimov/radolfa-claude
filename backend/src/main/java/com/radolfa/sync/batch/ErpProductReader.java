package com.radolfa.sync.batch;

import com.radolfa.sync.ErpApiClient;
import com.radolfa.sync.ErpProductDto;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Paginated {@link ItemReader} that pulls Items from ERPNext.
 * <p>
 * Internally buffers one page at a time.  The offset and buffer are reset
 * before every Step execution (via {@link #reset()}) so that re-running
 * the job always performs a full reconciliation from page zero.
 * </p>
 */
@Component
@Slf4j
public class ErpProductReader implements ItemReader<ErpProductDto> {

    private final ErpApiClient erpApiClient;

    @Value("${erp.api.page-size:100}")
    private int pageSize;

    private int offset = 0;
    private final Queue<ErpProductDto> buffer = new LinkedList<>();

    public ErpProductReader(ErpApiClient erpApiClient) {
        this.erpApiClient = erpApiClient;
    }

    /** Called by Spring Batch before the step begins — resets pagination state. */
    @BeforeStep
    public void reset() {
        offset = 0;
        buffer.clear();
        log.info("ErpProductReader: pagination state reset.");
    }

    @Override
    public ErpProductDto read() {
        if (buffer.isEmpty()) {
            List<ErpProductDto> page = erpApiClient.fetchProducts(offset);
            if (page.isEmpty()) {
                log.info("ErpProductReader: no more pages at offset {}.", offset);
                return null; // signals end of input to Spring Batch
            }
            buffer.addAll(page);
            offset += page.size();
            log.info("ErpProductReader: fetched {} items, offset now {}.", page.size(), offset);
        }
        return buffer.poll();
    }
}
