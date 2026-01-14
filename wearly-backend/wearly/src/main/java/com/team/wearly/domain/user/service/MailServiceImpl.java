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

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

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