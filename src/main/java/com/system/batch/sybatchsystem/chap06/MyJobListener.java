package com.system.batch.sybatchsystem.chap06;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MyJobListener implements JobExecutionListener {

    @Override
    public void beforeJob(JobExecution jobExecution) {
        log.info("[job 시작] id: {}", jobExecution.getJobInstanceId()); // 어떤 job이 수행되는지 id 정보 출력
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.FAILED) { // job 이 실패한 경우
            log.warn("[job 실패] 비상! 관리자에게 연락하세요.");
        } else {
            log.info("[job 종료] 정상적으로 배치가 실행되었습니다.");
        }
    }
}
