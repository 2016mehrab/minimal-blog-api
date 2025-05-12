package com.samurai74.minimalblog.services.impl;

import com.samurai74.minimalblog.domain.entities.User;
import com.samurai74.minimalblog.repositories.UserRepository;
import com.samurai74.minimalblog.services.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    @Override
    public User getUserById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(()->new EntityNotFoundException("User not found with id:" + userId));
    }
}
