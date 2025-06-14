package com.samurai74.minimalblog.security;


import com.samurai74.minimalblog.domain.entities.RefreshToken;
import com.samurai74.minimalblog.domain.entities.User;

import java.util.UUID;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(UUID userId);
    RefreshToken rotateToken(RefreshToken refreshToken);
    RefreshToken getRefreshToken(String token);
    void revokeRefreshToken(String refreshToken);
    void cleanUpRevokedAndExpiredTokens();
}
