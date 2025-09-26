package org.example.gamerscove.mappers.impl;

import org.example.gamerscove.domain.dto.UserDto;
import org.example.gamerscove.domain.entities.UserEntity;
import org.example.gamerscove.mappers.Mapper;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements Mapper<UserEntity, UserDto> {

    private ModelMapper modelMapper;

    public UserMapperImpl(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDto mapTo(UserEntity userEntity) {
        return UserDto.builder()
                .id(userEntity.getId())
                .firebaseUid(userEntity.getFirebaseUid())
                .email(userEntity.getEmail())
                .password(userEntity.getPassword())
                .username(userEntity.getUsername())
                .avatarUrl(userEntity.getAvatarUrl())
                .bio(userEntity.getBio())
                .preferredPlatforms(userEntity.getPreferredPlatforms())
                .favoriteGames(userEntity.getFavoriteGames())
                .gamertags(userEntity.getGamertags())
                .gamertagsVisibility(userEntity.getGamertagsVisibility() != null ?
                        UserEntity.GamertagsVisibility.valueOf(userEntity.getGamertagsVisibility().getValue()) : null)
                .build();
    }

    @Override
    public UserEntity mapFrom(UserDto userDto) {
        UserEntity userEntity = UserEntity.builder()
                .id(userDto.getId())
                .firebaseUid(userDto.getFirebaseUid())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .username(userDto.getUsername())
                .avatarUrl(userDto.getAvatarUrl())
                .bio(userDto.getBio())
                .build();

        // Set arrays using utility methods
        userEntity.setPreferredPlatforms(userDto.getPreferredPlatforms());
        userEntity.setFavoriteGames(userDto.getFavoriteGames());
        userEntity.setGamertags(userDto.getGamertags());

        // Convert string to enum
        if (userDto.getGamertagsVisibility() != null) {
            userEntity.setGamertagsVisibility(
                    UserEntity.GamertagsVisibility.valueOf(String.valueOf(userDto.getGamertagsVisibility()))
            );
        }

        return userEntity;
    }
}
