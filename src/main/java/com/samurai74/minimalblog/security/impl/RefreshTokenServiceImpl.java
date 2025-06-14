package com.samurai74.minimalblog.security.impl;

import com.samurai74.minimalblog.constant.Constants;
import com.samurai74.minimalblog.domain.entities.RefreshToken;
import com.samurai74.minimalblog.repositories.RefreshTokenRepository;
import com.samurai74.minimalblog.repositories.UserRepository;
import com.samurai74.minimalblog.security.RefreshTokenService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public RefreshToken createRefreshToken(UUID userId) {
        var user = userRepository.findById(userId).orElseThrow( ()->new EntityNotFoundException("User does not exist."));
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plus(Constants.REFRESH_TOKEN_EXPIRES_IN, ChronoUnit.DAYS))
                .revoked(false)
                .build();

        var rt= refreshTokenRepository.save(refreshToken);
        return rt;
    }

    @Override
    @Transactional
    public RefreshToken rotateToken(RefreshToken refreshToken) {
        // delete the old one-no need
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
        // get the user
        var user = refreshToken.getUser();
        // create a new one with the user
        return createRefreshToken(user.getId());
    }

    @Override
    public RefreshToken getRefreshToken(String token) {
        var rt= refreshTokenRepository.findByToken(token).orElseThrow(()->new EntityNotFoundException("Token does not exist."));
        return rt;
    }

    @Override
    @Transactional
    public void revokeRefreshToken(String refreshToken) {
       var rt = refreshTokenRepository.findByToken(refreshToken).orElseThrow(()->new EntityNotFoundException("Token does not exist."));
       rt.setRevoked(true);
       refreshTokenRepository.save(rt);

    }

}
