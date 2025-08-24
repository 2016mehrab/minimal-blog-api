package com.samurai74.minimalblog.services.impl;

import com.samurai74.minimalblog.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Queue;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private final JavaMailSender javaMailSender;
    private final Queue<SimpleMailMessage> emailQueue;

    @Override
    public void queueEmail(SimpleMailMessage email) {
        emailQueue.add(email);
    }

    @Override
    @Scheduled(fixedRateString = "${email.queue.schedule-rate}")
    public void processEmailQueue() {
        log.info("Starting email queue processing job...");
        if(emailQueue.isEmpty()){
            log.info("Email queue is empty. Skipping job.");
            return;
        }
        int success =0;
        int failure =0;

        SimpleMailMessage email ;
        while((email = emailQueue.poll())!=null){
            try{
                javaMailSender.send(email);
                ++success;
                if(email.getTo()!=null){
                    log.info("Successfully sent email to {}.", email.getTo()[0]);
                }
                else log.info("Successfully sent email.");

            }catch(MailException e){
                ++failure;
                if(email.getTo()!=null){
                    log.error("Failed to send email to {}. Error: {}", email.getTo()[0], e.getMessage());
                }
                else  log.error("Failed to send email. Error: {}", e.getMessage());
               emailQueue.add(email);
            }
        }
        log.info("Finished email queue processing job. Processed {} emails ({} successful, {} failed).",
                success + failure, success, failure);
    }
}
