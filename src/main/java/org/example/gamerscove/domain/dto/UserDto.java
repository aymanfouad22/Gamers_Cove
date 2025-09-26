package org.example.gamerscove.domain.dto;

import lombok.*;
import org.example.gamerscove.domain.entities.UserEntity;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Long id;

    private String firebaseUid;

    private String email;

    private String password;

    private String username;

    private String avatarUrl;

    private String bio;

    private String[] preferredPlatforms;

    private String[] favoriteGames;

    private Map<String, String> gamertags;

    private UserEntity.GamertagsVisibility gamertagsVisibility = UserEntity.GamertagsVisibility.FRIENDS;
}
