package com.samurai74.minimalblog.services.impl;

import com.samurai74.minimalblog.domain.Role;
import com.samurai74.minimalblog.domain.entities.RefreshToken;
import com.samurai74.minimalblog.domain.entities.User;
import com.samurai74.minimalblog.repositories.UserRepository;
import com.samurai74.minimalblog.security.RefreshTokenService;
import com.samurai74.minimalblog.services.AuthenticationService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;
    private  final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String secretkey;
    private final Long jwtExpiryMs = 120_000L;

    @Override
    public UserDetails authenticate(String email, String password) throws UsernameNotFoundException , BadCredentialsException {

        Authentication authenticatedObj= authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        return userDetailsService.loadUserByUsername(authenticatedObj.getName());
    }

    @Override
    @Transactional
    public UserDetails register(String name, String email, String password) throws  IllegalArgumentException {
        // if already a user
        if(userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }
        var transientUser =  User.builder()
                .name(name)
                .password(passwordEncoder.encode(password))
                .email(email)
                .role(Role.USER)
                .build();
        userRepository.save(transientUser);
        return userDetailsService.loadUserByUsername(transientUser.getEmail());
    }

    @Override
    @Transactional
    public RefreshToken rotateRefreshToken(RefreshToken refreshToken) {
        return refreshTokenService.rotateToken(refreshToken);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("sub", userDetails.getUsername());
        claims.put("role", userDetails.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList())
                   );
        var currTimeMillis = System.currentTimeMillis();

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(currTimeMillis))
                .setExpiration(new Date(currTimeMillis+jwtExpiryMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public UserDetails validateToken(String token){
        if(!isTokenValid(token)) {
            throw new BadCredentialsException("Invalid token");
        }
        String username = extractUsername(token);
        return userDetailsService.loadUserByUsername(username);
    }

    public boolean isTokenValid(String token) {
        var jwtParser =Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build();
        jwtParser.parseClaimsJws(token);
        return true;
    }


    private String extractUsername(String token) {
      Claims claims = Jwts.parserBuilder()
               .setSigningKey(getSigningKey())
               .build()
               .parseClaimsJws(token)
               .getBody();
      return claims.getSubject();
    }

    private Key getSigningKey(){
        byte[] keyBytes = secretkey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
