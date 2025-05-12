package com.samurai74.minimalblog.services;

import com.samurai74.minimalblog.domain.entities.User;

import java.util.UUID;

public interface UserService {
    User getUserById(UUID userId);
}
