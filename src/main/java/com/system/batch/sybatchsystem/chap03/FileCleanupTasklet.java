package com.system.batch.sybatchsystem.chap03;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.StepContribution;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.infrastructure.repeat.RepeatStatus;

import java.io.File;
import java.time.LocalDate;

@Slf4j
@RequiredArgsConstructor
public class FileCleanupTasklet implements Tasklet {

    private final String rootPath;
    private final int retentionDays; // 보관일자

    @Override
    public @Nullable RepeatStatus execute(StepContribution contribution,
                                          ChunkContext chunkContext) throws Exception {
        LocalDate cutoffDate = LocalDate.now().minusDays(retentionDays);

        File folder = new File(rootPath);
        File[] files = folder.listFiles();
        if (files == null) {
            return RepeatStatus.FINISHED;
        }

        for (File file : files) {
            String fileName = file.getName();

            // 1. 로그 파일 형식인지 간단히 체크 (예: .log 포함 여부)
            if (fileName.endsWith(".log")) {
                try {
                    // 2. 파일명에서 날짜 부분만 필터링: "access_2026-01-31.log" => "2026-01-13"
                    String dateStr = fileName.substring(
                            fileName.lastIndexOf("_") + 1, fileName.lastIndexOf("."));
                    LocalDate fileDate = LocalDate.parse(dateStr);
                    if (fileDate.isBefore(cutoffDate)) {
                        file.delete();
                        log.info("삭제된 로그파일: {}", fileName);
                    }
                } catch (Exception e) {
                    // 날짜 형식이 아니거나 파싱 불가능한 파일(system_config.conf 등)은 자연스럽게 스킵
                    continue;
                }
            }
        }

        return RepeatStatus.FINISHED;
    }
}
