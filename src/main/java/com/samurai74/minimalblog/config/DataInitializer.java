package com.samurai74.minimalblog.config;

import com.samurai74.minimalblog.domain.Role;
import com.samurai74.minimalblog.domain.entities.Category;
import com.samurai74.minimalblog.domain.entities.User;
import com.samurai74.minimalblog.repositories.CategoryRepository;
import com.samurai74.minimalblog.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
      String adminMail = "2016mehrab@gmail.com";
      userRepository.findByEmail(adminMail).orElseGet(()->{
                  User admin = User.builder()
                          .name("Eshan")
                          .email(adminMail)
                          .password(passwordEncoder.encode("adminpass"))
                          .role(Role.ADMIN)
                          .build();
                  return userRepository.save(admin);
              }
              );
      categoryRepository.findByNameIgnoreCase("uncategorized").orElseGet(()->{
          var category = Category.builder()
                  .name("Uncategorized")
                  .build();
          return categoryRepository.save(category);
      });
    }
}
