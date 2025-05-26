package com.samurai74.minimalblog.security;

public interface PasswordResetService {
    void sendPasswordResetEmail(String email);
    void resetPassword(String token,String newPassword);
}
