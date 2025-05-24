package com.samurai74.minimalblog.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailPasswordAuthenticationProvider implements AuthenticationProvider {
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();
        log.debug("Attempting to authenticate user with email: {}", email);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        if(passwordEncoder.matches(password, userDetails.getPassword())) {
            log.info("Authentication successful for user: {}", email);
            return new UsernamePasswordAuthenticationToken(email, null, userDetails.getAuthorities());
        }
        throw new BadCredentialsException("Bad credentials");
    }

    @Override
    public boolean supports(Class<?> authentication) {
        log.debug("Authentication class type: {}", authentication.getName());
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
