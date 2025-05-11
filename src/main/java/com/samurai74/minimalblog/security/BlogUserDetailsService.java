package com.samurai74.minimalblog.security;

import com.samurai74.minimalblog.domain.entities.User;
import com.samurai74.minimalblog.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@RequiredArgsConstructor
public class BlogUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user =userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User not found with email: "+ email));
        return new BlogUserDetails(user);
    }
}
