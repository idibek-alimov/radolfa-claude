package com.radolfa.sync.batch;

import com.radolfa.entity.Product;
import com.radolfa.sync.ErpProductDto;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Wires the {@code erpInitialImportJob} Spring Batch job.
 * <p>
 * Chunk size is 100.  The step is fault-tolerant: each chunk retries up to 3 times
 * on any exception before the step is marked as failed.
 * </p>
 */
@Configuration
public class ErpSyncJobConfig {

    @Bean
    public Job erpInitialImportJob(JobRepository jobRepository, Step erpSyncStep) {
        return new JobBuilder("erpInitialImportJob", jobRepository)
                .start(erpSyncStep)
                .build();
    }

    @Bean
    public Step erpSyncStep(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            ErpProductReader reader,
                            ErpProductProcessor processor,
                            ErpProductWriter writer) {
        return new StepBuilder("erpSyncStep", jobRepository)
                .<ErpProductDto, Product>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .retry(Exception.class)
                .retryLimit(3)
                .build();
    }
}
