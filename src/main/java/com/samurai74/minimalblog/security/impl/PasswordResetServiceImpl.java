package com.samurai74.minimalblog.security.impl;

import com.samurai74.minimalblog.constant.Constants;
import com.samurai74.minimalblog.exceptions.TokenExpiredException;
import com.samurai74.minimalblog.repositories.UserRepository;
import com.samurai74.minimalblog.security.PasswordResetService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetServiceImpl implements PasswordResetService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Value("${email.service.from.email}")
    public String EMAIL_SENDER ;
    private final JavaMailSender mailSender;
    private final ExecutorService emailExecutor = Executors.newVirtualThreadPerTaskExecutor();


    @Transactional
    public void sendPasswordResetEmail(String email) {
        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(Constants.RESET_TOKEN_EXPIRES_IN);
        userOpt.get().setResetToken(token);
        userOpt.get().setResetTokenExpiresAt(expiresAt);
        var userEmail = userOpt.get().getEmail();
        userRepository.save(userOpt.get());
        // implement email send logic
        StringBuilder sb = new StringBuilder();
        sb.append("Your password ResetToken: ");
        sb.append(token);
        sb.append("\n");
        sb.append("Token will expire in: ");
        sb.append(Constants.RESET_TOKEN_EXPIRES_IN);
        sb.append("min");
        sb.append("\n");
        SimpleMailMessage sm = new SimpleMailMessage();
        sm.setTo(userEmail);
        sm.setFrom(EMAIL_SENDER);
        sm.setSubject("Password Reset token from Minimal Blog");
        sm.setText(sb.toString());
        emailExecutor.submit(()-> sendEmail(sm));
    }

    @Transactional
    public void resetPassword(String token,String newPassword) {
        var userOpt= userRepository.findByResetToken(token);
        if (userOpt.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        // check if expired
        if(userOpt.get().getResetTokenExpiresAt().isBefore(LocalDateTime.now())){
            throw new TokenExpiredException("Token is expired");
        }
        userOpt.get().setPassword(passwordEncoder.encode(newPassword));
        userOpt.get().setResetToken(null);
        userOpt.get().setResetTokenExpiresAt(null);
        userRepository.save(userOpt.get());
    }
    protected void sendEmail(SimpleMailMessage email) {
        log.info("Starting email sending process in a new thread...");
        try {
            mailSender.send(email);
            if (email.getTo() != null) {
                log.info("Successfully sent email to {}.", email.getTo()[0]);
            } else {
                log.info("Successfully sent email.");
            }
        } catch (MailException e) {
            if (email.getTo() != null) {
                log.error("Failed to send email to {}. Error: {}", email.getTo()[0], e.getMessage());
            } else {
                log.error("Failed to send email. Error: {}", e.getMessage());
            }
        }
    }
}
