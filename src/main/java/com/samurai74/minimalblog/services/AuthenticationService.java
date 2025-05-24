package com.samurai74.minimalblog.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface AuthenticationService {
    UserDetails authenticate(String email, String password) throws UsernameNotFoundException;
    String generateToken(UserDetails userDetails);
    UserDetails validateToken(String token);
    UserDetails register(String name, String email, String password );
}
