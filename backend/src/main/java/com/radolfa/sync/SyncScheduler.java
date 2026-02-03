package com.radolfa.sync;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * Fires the full-reconciliation batch job every hour.
 * This guarantees eventual consistency even if incremental webhook calls are missed.
 * A unique {@code timestamp} parameter is added each run so Spring Batch treats it
 * as a new job instance.
 */
@Component
@Slf4j
public class SyncScheduler {

    private final JobLauncher jobLauncher;
    private final Job erpInitialImportJob;

    public SyncScheduler(JobLauncher jobLauncher, Job erpInitialImportJob) {
        this.jobLauncher = jobLauncher;
        this.erpInitialImportJob = erpInitialImportJob;
    }

    @Scheduled(cron = "0 0 * * * *")
    public void runReconciliation() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(erpInitialImportJob, params);
            log.info("SyncScheduler: reconciliation job launched.");
        } catch (Exception e) {
            log.error("SyncScheduler: failed to launch reconciliation job.", e);
        }
    }
}
