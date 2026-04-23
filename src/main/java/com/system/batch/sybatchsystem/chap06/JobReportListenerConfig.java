package com.system.batch.sybatchsystem.chap06;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobReportListenerConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    private final JobReportListener jobReportListener;

    @Bean
    public Job emailJob() {
        return new JobBuilder("emailJob", jobRepository)
                .start(emailStep()) // RepeatStatus 없이 그냥 Step 이 끝나버리는 경우,
                .listener(jobReportListener) // listener 는 job이 정상적으로 종료되지 않았다고 판단하고 @Afterjob 호출
                .build();
    }

    @Bean
    public Step emailStep() {
        return new StepBuilder("emailStep", jobRepository)
                .tasklet(new Tasklet() {
                    @Override
                    public @Nullable RepeatStatus execute(StepContribution contribution,
                                                          ChunkContext chunkContext) throws Exception {
                        log.info("3초 뒤 에러를 발생시킵니다.");

                        Thread.sleep(3_000);

                        throw new RuntimeException("테스트를 위해 에러 발생!");
                    }
                }, transactionManager)
                .build();
    }

}
