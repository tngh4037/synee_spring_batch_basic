package com.system.batch.sybatchsystem.chap04;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.batch.infrastructure.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.List;

// 메뉴에 있는 문자를 전부 대문자로 바꾸는 배치 (chunksize: 2)
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SimpleChunkConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    // 1. itemReader
    @Bean
    public ItemReader<String> menuReader() {
        return new ListItemReader<>(
                List.of("ice americano", "latte", "mocha", "cappuccino", "espresso")
        );
    }

    // 2. ItemProcessor
    @Bean
    public ItemProcessor<String, String> menuProcessor() {
        return String::toUpperCase;
        /*
        return new ItemProcessor<String, String>() {

            @Override
            public @Nullable String process(String item) throws Exception {
                return item.toUpperCase();
            }
        };
        */
    }

    // 3. ItemWriter
    @Bean
    public ItemWriter<String> menuWriter() {

        return new ItemWriter<String>() {
            @Override
            public void write(Chunk<? extends String> chunk) throws Exception {
                log.info("=== 청크 쓰기 시작 ===");
                for (String item : chunk) {
                    log.info("결과: {}", item);
                }
                log.info("== 청크 쓰기 완료 ==");
            }
        };
    }

    @Bean
    public Step simpleStep() {
        return new StepBuilder("simpleStep", jobRepository)
                .<String, String>chunk(2) // 참고) chunk 처리 시, <Input Type, Output Type> 을 작성해주어야 한다.
                .reader(menuReader())
                .processor(menuProcessor())
                .writer(menuWriter())
                .build();
    }

    @Bean
    public Job simpleJob() {
        return new JobBuilder("simpleJob", jobRepository)
                .start(simpleStep())
                .build();
    }

}
