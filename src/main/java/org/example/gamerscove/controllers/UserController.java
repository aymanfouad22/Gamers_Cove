package org.example.gamerscove.controllers;

import org.example.gamerscove.domain.dto.UserDto;
import org.example.gamerscove.domain.entities.UserEntity;
import org.example.gamerscove.mappers.Mapper;
import org.example.gamerscove.services.UserService;
import org.slf4j.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private UserService userService;
    private Mapper<UserEntity, UserDto> userMapper;

    public UserController(UserService userService, Mapper<UserEntity, UserDto> userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @PostMapping(path = "/users")
    public UserDto createUser(@RequestBody UserDto user) {
        logger.info("=== POST /api/users ENDPOINT CALLED ===");
        logger.info("Received UserDto: {}", user);
        logger.info("======================================");

        UserEntity userEntity = userMapper.mapFrom(user);
        UserEntity savedUserEntity = userService.createUser(userEntity);
        return userMapper.mapTo(savedUserEntity);
    }

    @PostMapping(path = "/users/me")
    public ResponseEntity<UserDto> getUserInfo(@RequestBody UserDto user) {
        logger.info("=== POST /api/users/me ENDPOINT CALLED ===");
        logger.info("Received request to get user info for Firebase UID: {}", user.getFirebaseUid());
        logger.info("=========================================");

        Optional<UserEntity> userEntity = userService.findByFirebaseUid(user.getFirebaseUid());
        if (userEntity.isPresent()) {
            UserDto userDto = userMapper.mapTo(userEntity.get());
            logger.info("Returning user info for: {}", userDto.getUsername());
            return ResponseEntity.ok(userDto);
        } else {
            logger.warn("User not found for Firebase UID: {}", user.getFirebaseUid());
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping(path = "/users/me")
    public ResponseEntity<UserDto> updateUserInfo(@RequestBody UserDto user) {
        logger.info("=== PUT /api/users/me ENDPOINT CALLED ===");
        logger.info("Received request to update user info: {}", user);
        logger.info("========================================");

        Optional<UserEntity> existingUser = userService.findByFirebaseUid(user.getFirebaseUid());
        if (existingUser.isPresent()) {
            UserEntity userEntity = userMapper.mapFrom(user);
            userEntity.setId(existingUser.get().getId());

            UserEntity updatedUser = userService.updateUser(userEntity);
            UserDto updatedUserDto = userMapper.mapTo(updatedUser);

            logger.info("User updated successfully: {}", updatedUserDto.getUsername());
            return ResponseEntity.ok(updatedUserDto);
        } else {
            logger.warn("User not found for update with Firebase UID: {}", user.getFirebaseUid());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "/users/{userId}/favorite_games")
    public ResponseEntity<String[]> getUserFavoriteGames(@PathVariable("userId") Long userId) {
        logger.info("=== GET /api/users/{}/favorite_games ENDPOINT CALLED ===", userId);
        logger.info("Fetching favorite games for user ID: {}", userId);
        logger.info("======================================================");

        Optional<UserEntity> user = userService.findById(userId);
        if (user.isPresent()) {
            String[] favoriteGames = user.get().getFavoriteGames();
            logger.info("Found {} favorite games for user {}",
                    favoriteGames != null ? favoriteGames.length : 0,
                    user.get().getUsername());
            if (favoriteGames != null) {
                logger.info("Favorite games: {}", String.join(", ", favoriteGames));
            }
            return ResponseEntity.ok(favoriteGames);
        } else {
            logger.warn("User not found with ID: {}", userId);
            return ResponseEntity.notFound().build();
        }
    }

    // Additional endpoint to get user by username for testing
    @GetMapping(path = "/users/username/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable("username") String username) {
        logger.info("=== GET /api/users/username/{} ENDPOINT CALLED ===", username);
        logger.info("Fetching user by username: {}", username);
        logger.info("=================================================");

        Optional<UserEntity> user = userService.findByUsername(username);
        if (user.isPresent()) {
            UserDto userDto = userMapper.mapTo(user.get());
            logger.info("Found user: {}", userDto.getUsername());
            return ResponseEntity.ok(userDto);
        } else {
            logger.warn("User not found with username: {}", username);
            return ResponseEntity.notFound().build();
        }
    }
}
