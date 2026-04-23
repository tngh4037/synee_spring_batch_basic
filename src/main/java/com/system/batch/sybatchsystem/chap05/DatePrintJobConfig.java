package com.system.batch.sybatchsystem.chap05;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DatePrintJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Step datePrintStep(Tasklet datePrintTasklet) {
        return new StepBuilder("datePrintStep", jobRepository)
                .tasklet(datePrintTasklet, transactionManager)
                .build();
    }

    @Bean
    public Job datePrintJob(Step datePrintStep) {
        return new JobBuilder("datePrintJob", jobRepository)
                .start(datePrintStep)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet datePrintTasklet(
            @Value("#{jobParameters['requestDate']}") String requestDate
    ) {
        return new DatePrintTasklet(requestDate);
    }
}
