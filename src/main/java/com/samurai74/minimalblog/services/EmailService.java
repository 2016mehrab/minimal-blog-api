package com.samurai74.minimalblog.services;

import org.springframework.mail.SimpleMailMessage;

public interface EmailService {
    public void queueEmail(SimpleMailMessage email);
    public void processEmailQueue();
}
