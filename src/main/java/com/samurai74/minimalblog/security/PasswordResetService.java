package com.samurai74.minimalblog.security;

import com.samurai74.minimalblog.domain.entities.User;
import com.samurai74.minimalblog.exceptions.TokenExpiredException;
import com.samurai74.minimalblog.repositories.UserRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {
    private final UserRepository userRepository;
    @Transactional
    public void sendPasswordResetEmail(String email) {
        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(10);
        userOpt.get().setResetToken(token);
        userOpt.get().setResetTokenExpiresAt(expiresAt);
        userRepository.save(userOpt.get());
        // implement email send logic
    }
    @Transactional
    public void resetPassword(String token,String newPassword) {
        var userOpt= userRepository.findByResetToken(token);
        if (userOpt.isEmpty()) {
            throw new EntityNotFoundException("User not found");
        }
        // check if expired
        if(!userOpt.get().getResetTokenExpiresAt().isBefore(LocalDateTime.now())){
            throw new TokenExpiredException("Token is expired");
        }
        userOpt.get().setPassword(newPassword);
        userOpt.get().setResetToken(null);
        userOpt.get().setResetTokenExpiresAt(null);
        userRepository.save(userOpt.get());
    }
}
