package com.team.wearly.domain.user.service;

import com.team.wearly.global.exception.CustomException;
import com.team.wearly.global.exception.ErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 시스템 내 각종 알림 및 인증 메일 발송을 담당하는 메일 서비스 구현체
 *
 * @author 허준형
 * @DateOfCreated 2026-01-14
 * @DateOfEdit 2026-01-14
 */
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;


    /**
     * 비밀번호를 분실한 사용자에게 재설정 페이지 링크가 포함된 안내 메일을 발송함
     * SMTP 서버와의 통신 지연이 사용자 경험(UX)에 영향을 주지 않도록 비동기(@Async)로 처리함
     *
     * @param email     메일을 수신할 사용자의 이메일 주소
     * @param resetLink 유효 토큰이 포함된 비밀번호 재설정 URL
     * @author 허준형
     * @DateOfCreated 2026-01-14
     * @DateOfEdit 2026-01-14
     */
    @Async
    @Override
    public void sendPasswordResetMail(String email, String resetLink) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setSubject("[Wearly] 비밀번호 재설정 안내");
        message.setText(
                "비밀번호 재설정을 요청하셨습니다.\n\n" +
                        "아래 링크를 클릭하여 비밀번호를 재설정해주세요.\n\n" +
                        resetLink + "\n\n" +
                        "해당 링크는 30분간 유효합니다."
        );

        mailSender.send(message);
    }
}