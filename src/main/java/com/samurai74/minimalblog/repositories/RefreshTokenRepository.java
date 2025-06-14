package com.samurai74.minimalblog.repositories;

import com.samurai74.minimalblog.domain.entities.RefreshToken;
import com.samurai74.minimalblog.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM refresh_token WHERE revoked=true OR expiry_date<=:now", nativeQuery = true)
    int clearRevokedAndExpiredTokens(Instant now);

}
