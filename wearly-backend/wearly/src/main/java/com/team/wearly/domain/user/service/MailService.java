package com.team.wearly.domain.user.service;

public interface MailService {
    public void sendPasswordResetMail(String email, String resetLink);
}
