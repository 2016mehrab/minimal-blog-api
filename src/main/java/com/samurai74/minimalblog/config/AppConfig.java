package com.samurai74.minimalblog.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.SimpleMailMessage;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Configuration
public class AppConfig {
    @Bean
    public Queue<SimpleMailMessage> emailQueue() {
        return new ConcurrentLinkedQueue<>();
    }
}
