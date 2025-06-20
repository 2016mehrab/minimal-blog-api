package com.samurai74.minimalblog.config;

import com.samurai74.minimalblog.domain.Role;
import com.samurai74.minimalblog.domain.entities.User;
import com.samurai74.minimalblog.repositories.UserRepository;
import com.samurai74.minimalblog.security.BlogUserDetailsService;
import com.samurai74.minimalblog.security.CookieLogFilter;
import com.samurai74.minimalblog.security.EmailPasswordAuthenticationProvider;
import com.samurai74.minimalblog.security.JwtAuthenticationFilter;
import com.samurai74.minimalblog.services.AuthenticationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity(securedEnabled = true,prePostEnabled = true)
public class SecurityConfig {
    @Bean
    public JwtAuthenticationFilter jwtAuthFilter(AuthenticationService authenticationService) {
        return new JwtAuthenticationFilter(authenticationService);
    }
    @Bean
    public UserDetailsService userDetailsService(UserRepository userRepository) {
       BlogUserDetailsService blogUserDetailsService = new BlogUserDetailsService(userRepository);
       String email= "admin@gmail.com";
       userRepository.findByEmail(email).orElseGet(()->{
           User newUser = User.builder()
                   .name("Admin")
                   .email(email)
                   .role(Role.ADMIN)
                   .password(passwordEncoder().encode("adminpass"))
                   .build();
           return userRepository.save(newUser);
       });

       return blogUserDetailsService;
    }

    @Bean
    UrlBasedCorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://localhost:5173");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", corsConfiguration);
        return src;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter, EmailPasswordAuthenticationProvider emailPasswordAuthenticationProvider, CookieLogFilter  cookieLogFilter) throws Exception {
        http.authenticationProvider(emailPasswordAuthenticationProvider);
        http.cors((crs)-> crs.configurationSource(corsConfigurationSource()));
        http.authorizeHttpRequests(auth->
                auth
                        // first match wins
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/posts/drafts").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.GET,"/api/v1/posts/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/api/v1/tags/**").permitAll()
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .csrf(csrf->csrf.disable())
                .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
//                .addFilterBefore(cookieLogFilter, JwtAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        ;
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
