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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication")
@SecurityRequirements(value={})
@CrossOrigin
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
       String refreshToken= refreshTokenService.createRefreshToken( userId).getToken();

        ResponseCookie refreshTokenCookie= getRefreshTokenCookie(refreshToken);

       var authRes = AuthResponse.builder()
               .accessToken(tokenValue)
                .expiresIn(Constants.EXPIRES_IN).build();

       return ResponseEntity.ok()
               .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
               .body(authRes);
    }

    @PostMapping(path = "/refresh-token")
    public ResponseEntity<AuthResponse> getRefreshToken(@RequestBody RefreshRequest refreshRequest) {
        RefreshToken refreshToken = refreshTokenService.getRefreshToken(refreshRequest.getRefreshToken());

        var newRefreshToken =  authenticationService.getRefreshToken(refreshToken).getToken();
        var userDetails = new BlogUserDetails(refreshToken.getUser())  ;
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
        String refreshToken= refreshTokenService.createRefreshToken( userId).getToken();

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

    private ResponseCookie getRefreshTokenCookie(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                // sent only over https
                //  .secure(true)
                .path("/api/v1/auth/refresh-token")
                .maxAge(Duration.ofDays(Constants.REFRESH_TOKEN_EXPIRES_IN).getSeconds())
                // for csrf protection, only sent from same origin
                // .sameSite("Strict")
                .build();
    }
}
