package com.samurai74.minimalblog.controllers;

import com.samurai74.minimalblog.constant.Constants;
import com.samurai74.minimalblog.domain.dtos.*;
import com.samurai74.minimalblog.domain.entities.RefreshToken;
import com.samurai74.minimalblog.security.BlogUserDetails;
import com.samurai74.minimalblog.security.PasswordResetService;
import com.samurai74.minimalblog.security.RefreshTokenService;
import com.samurai74.minimalblog.services.AuthenticationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication")
public class AuthController {
    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordResetService passwordResetService;

    @PostMapping(path = "/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
       UserDetails userDetails= authenticationService.authenticate(loginRequest.getEmail(),loginRequest.getPassword());
       log.warn("username/email "+userDetails.getUsername());

       String tokenValue= authenticationService.generateToken(userDetails);
       UUID userId=((BlogUserDetails) userDetails).getUserId();
       String refreshToken= refreshTokenService.createRefreshToken( userId).getToken();
       var authRes = AuthResponse.builder()
               .accessToken(tokenValue)
               .refreshToken(refreshToken)
                .expiresIn(Constants.EXPIRES_IN).build();
       return ResponseEntity.ok(authRes);
    }

    @PostMapping(path = "/refresh-token")
    public ResponseEntity<AuthResponse> getRefreshToken(@RequestBody RefreshRequest refreshRequest) {
        RefreshToken refreshToken = refreshTokenService.getRefreshToken(refreshRequest.getRefreshToken());

        var newRefreshToken =  authenticationService.getRefreshToken(refreshToken).getToken();
        var userDetails = new BlogUserDetails(refreshToken.getUser())  ;
        var accessToken = authenticationService.generateToken(userDetails);

        var authRes = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken)
                .expiresIn(Constants.EXPIRES_IN).build();
        return ResponseEntity.ok(authRes);
    }

    @PostMapping(path = "/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        UserDetails userDetails= authenticationService.register(registerRequest.getName(), registerRequest.getEmail(), registerRequest.getPassword());


        String tokenValue= authenticationService.generateToken(userDetails);
        UUID userId=((BlogUserDetails) userDetails).getUserId();
        String refreshToken= refreshTokenService.createRefreshToken( userId).getToken();
        var authRes = AuthResponse.builder()
                .accessToken(tokenValue)
                .refreshToken(refreshToken)
                .expiresIn(Constants.EXPIRES_IN).build();
        return ResponseEntity.status(HttpStatus.CREATED).body(authRes);
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
}
