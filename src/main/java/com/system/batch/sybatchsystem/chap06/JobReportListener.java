package com.system.batch.sybatchsystem.chap06;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.stereotype.Component;

// 애노테이션 방식으로 리스너 생성
@Slf4j
@Component
@RequiredArgsConstructor
public class JobReportListener {

    private final EmailProvider emailProvider;

    @BeforeJob
    public void before(JobExecution jobExecution) {
        log.info("배치를 시작합니다. id: {}", jobExecution.getJobInstanceId());
    }

    @AfterJob
    public void after(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.FAILED) {
            emailProvider.send(
                    "admin@admin.co.kr",
                    "배치 실패 알림",
                    "job id: " + jobExecution.getJobInstanceId() + "번이 실패했습니다. 조치 바람.");
        } else {
            log.info("배치가 성공적으로 끝났습니다.");
        }
    }
}
