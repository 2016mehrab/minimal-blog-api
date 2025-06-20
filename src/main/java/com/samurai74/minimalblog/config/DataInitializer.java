package com.samurai74.minimalblog.config;

import com.samurai74.minimalblog.domain.Role;
import com.samurai74.minimalblog.domain.entities.Category;
import com.samurai74.minimalblog.domain.entities.Tag;
import com.samurai74.minimalblog.domain.entities.User;
import com.samurai74.minimalblog.repositories.CategoryRepository;
import com.samurai74.minimalblog.repositories.TagRepository;
import com.samurai74.minimalblog.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
    var tags = List.of("react" ,"java", "laravel", "php", "go", "javascript", "horror", "maven","spring boot","python","spring security","fastapi","astro","trash");
    var cats = List.of("uncategorized", "programming", "math", "science", "machine learning","trash");
    for (var tag : tags) {
        tagRepository.save(Tag.builder().name(tag).build());
    }
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
      for(var cat: cats) categoryRepository.save(Category.builder().name(cat).build());
    }
}
