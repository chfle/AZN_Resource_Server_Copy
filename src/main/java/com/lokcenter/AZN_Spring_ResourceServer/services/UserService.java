package com.lokcenter.AZN_Spring_ResourceServer.services;

import com.lokcenter.AZN_Spring_ResourceServer.database.repository.UserRepository;
import com.lokcenter.AZN_Spring_ResourceServer.database.tables.Users;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@AllArgsConstructor
public class UserService {
    @Autowired
    private final UserRepository userRepository;

    @Async
    public CompletableFuture<Optional<Users>> findById(Long userId) {
        return CompletableFuture.completedFuture(userRepository.findById(userId));
    }

    @Async
    public CompletableFuture<Optional<Users>> findByName(String name) {
        return CompletableFuture.completedFuture(userRepository.findByUsername(name));
    }
}
