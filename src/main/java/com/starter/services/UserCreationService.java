package com.starter.services;

import com.starter.models.entities.User;
import com.starter.models.v1.V1CreateUserRequest;
import com.starter.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserCreationService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserCreationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void createUser(V1CreateUserRequest userRequest) {
        userRepository.save(User.builder()
            .email(userRequest.getEmail())
            .password(passwordEncoder.encode(userRequest.getPassword()))
            .build());
    }

}
