package com.system.batch.sybatchsystem.chap06;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// 알림을 위해 이메일을 보내주는 클래스라 가정
@Slf4j
@Component
public class EmailProvider {

    public void send(String to, String subject, String message) {
        // 실제로는 여기에서 smtp로 메일 전송
        log.info("[메일 발송 성공] 받는사람 : {}", to);
        log.info("제목 : {}", subject);
        log.info("내용 : {}", message);
    }
}
