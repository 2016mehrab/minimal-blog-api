package com.samurai74.minimalblog.controllers;

import com.samurai74.minimalblog.constant.Constants;
import com.samurai74.minimalblog.domain.dtos.*;
import com.samurai74.minimalblog.domain.entities.RefreshToken;
import com.samurai74.minimalblog.security.BlogUserDetails;
import com.samurai74.minimalblog.security.PasswordResetService;
import com.samurai74.minimalblog.security.RefreshTokenService;
import com.samurai74.minimalblog.services.AuthenticationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication")
@SecurityRequirements(value={})
public class AuthController {
    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetService passwordResetService;

    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
       UserDetails userDetails= authenticationService.authenticate(loginRequest.getEmail(),loginRequest.getPassword());
       log.info("username/email "+userDetails.getUsername());

       String tokenValue= authenticationService.generateToken(userDetails);
       UUID userId=((BlogUserDetails) userDetails).getUserId();
       RefreshToken refreshToken= refreshTokenService.createRefreshToken( userId);

        ResponseCookie refreshTokenCookie= getRefreshTokenCookie(refreshToken);

       var authRes = AuthResponse.builder()
               .accessToken(tokenValue)
                .expiresIn(Constants.EXPIRES_IN).build();

       return ResponseEntity.ok()
               .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
               .body(authRes);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(
                                    @CookieValue(name = "refreshToken", required = false) String refreshToken) {
        log.info("logout with refresh-token: {}", refreshToken);
        if (refreshToken == null) {
            throw new BadCredentialsException("Missing refresh token");
        }
        // Clear cookie
        ResponseCookie clearedCookie = ResponseCookie.from("refreshToken", "")

                .httpOnly(true)
                // sent only over https
                .secure(false)
                .domain("localhost")
                .path("/")
//                .maxAge(Duration.ofDays(Constants.REFRESH_TOKEN_EXPIRES_IN).getSeconds())
                .maxAge(0)
                // for csrf protection, only sent from same origin
                .sameSite("Lax")
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, clearedCookie.toString())
                .build();
    }

    @PostMapping(path = "/refresh-token")
    public ResponseEntity<AuthResponse> getRefreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        if(refreshToken == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        RefreshToken rf = refreshTokenService.getRefreshToken(refreshToken);

        var newRefreshToken =  authenticationService.getRefreshToken(rf);
        var userDetails = new BlogUserDetails(rf.getUser())  ;
        var accessToken = authenticationService.generateToken(userDetails);

        var refreshTokenCookie= getRefreshTokenCookie(newRefreshToken);
        var authRes = AuthResponse.builder()
                .accessToken(accessToken)
                .expiresIn(Constants.EXPIRES_IN).build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(authRes);
    }

    @PostMapping(path = "/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        UserDetails userDetails= authenticationService.register(registerRequest.getName(), registerRequest.getEmail(), registerRequest.getPassword());


        String tokenValue= authenticationService.generateToken(userDetails);
        UUID userId=((BlogUserDetails) userDetails).getUserId();
        RefreshToken refreshToken= refreshTokenService.createRefreshToken( userId);

        var refreshTokenCookie= getRefreshTokenCookie(refreshToken);
        var authRes = AuthResponse.builder()
                .accessToken(tokenValue)
                .expiresIn(Constants.EXPIRES_IN).build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .body(authRes);
    }

    @PostMapping(path = "/forgot-password")
    public ResponseEntity<String> forgotPassword( @RequestParam(name = "email") String email ) {
        log.warn("Forgot password request for email "+email);
        passwordResetService.sendPasswordResetEmail(email);
        return ResponseEntity.accepted().build();
    }

    @PostMapping(path = "/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody PasswordResetConfirmRequest req ) {
        passwordResetService.resetPassword(req.getToken(), req.getNewPassword());
        return ResponseEntity.ok().build();
    }


    private ResponseCookie getRefreshTokenCookie(RefreshToken refreshToken) {
        Duration maxAgeDuration= Duration.between(Instant.now() , refreshToken.getExpiryDate());

        return ResponseCookie.from("refreshToken", refreshToken.getToken())
                .httpOnly(true)
                // sent only over https
                  .secure(false)
                .domain("localhost")
                .path("/")
                .maxAge(maxAgeDuration.toSeconds())
                // for csrf protection, only sent from same origin
                 .sameSite("Lax")
                .build();
    }
}
