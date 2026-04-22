package com.system.batch.sybatchsystem.chap03;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

// 테스트 파일을 생성하기 위한 코드
public class LogGenerator {

    private static final String ROOT_PATH = "./test-logs";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static void main(String[] args) throws IOException {
        File dir = new File(ROOT_PATH);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 파일 이름에 날짜를 포함하여 생성 (예: access_2025-01-01.log)
        createLogFile(dir, "access", 2);    // 2일 전
        createLogFile(dir, "access", 0);    // 오늘
        createLogFile(dir, "service", 50);  // 50일 전
        createLogFile(dir, "service", 100); // 100일 전

        // 날짜 형식이 없는 예외 파일도 하나 추가 (삭제되면 안됨)
        createLogFile(dir, "system_config.conf", -1);

        System.out.println("테스트용 로그 파일 생성 완료! 경로 확인: " + ROOT_PATH);
    }

    private static void createLogFile(File dir, String prefix, int daysAgo) throws IOException {
        String filename;

        if (daysAgo == -1) {
            // 날짜 패턴이 없는 일반 파일
            filename = prefix;
        } else {
            // 날짜 패턴 적용: prefix_yyyy-MM-dd.log
            LocalDate targetDate = LocalDate.now().minusDays(daysAgo);
            String dateStr = targetDate.format(DATE_FORMATTER);
            filename = prefix + "_" + dateStr + ".log";
        }

        File file = new File(dir, filename);

        if (file.createNewFile()) {
            System.out.println("파일 생성됨: " + filename);
        } else {
            System.out.println("이미 존재함: " + filename);
        }
    }

}
