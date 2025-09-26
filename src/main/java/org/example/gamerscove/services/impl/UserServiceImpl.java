package org.example.gamerscove.services.impl;

import org.example.gamerscove.domain.entities.UserEntity;
import org.example.gamerscove.repositories.UserRepository;
import org.example.gamerscove.services.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserEntity createUser(UserEntity userEntity) {
        return userRepository.save(userEntity);
    }
}
