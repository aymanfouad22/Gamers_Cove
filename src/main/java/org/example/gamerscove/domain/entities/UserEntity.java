package org.example.gamerscove.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;
import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Map;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Table(name = "users") // Note: 'user' is a reserved keyword in PostgreSQL
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 128)
    @Column(name = "firebase_uid", nullable = false, unique = true, length = 128)
    private String firebaseUid;

    @Transient
    private String email;

    @Transient
    private String password;

    @NotNull
    @Size(max = 50)
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Type(StringArrayType.class)
    @Column(name = "preferred_platforms", columnDefinition = "text[]")
    private String[] preferredPlatforms;

    @Type(StringArrayType.class)
    @Column(name = "favorite_games", columnDefinition = "text[]")
    private String[] favoriteGames;

    @Type(JsonBinaryType.class)
    @Column(name = "gamertags", columnDefinition = "jsonb")
    private Map<String, String> gamertags;

    @Enumerated(EnumType.STRING)
    @Column(name = "gamertags_visibility", length = 10, columnDefinition = "varchar(10) default 'friends'")
    private GamertagsVisibility gamertagsVisibility = GamertagsVisibility.FRIENDS;

    // Enum for gamertags_visibility
    public enum GamertagsVisibility {
        PUBLIC("public"),
        FRIENDS("friends");

        private final String value;

        GamertagsVisibility(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public UserEntity(String firebaseUid, String username, String email, String password) {
        this.firebaseUid = firebaseUid;
        this.username = username;
        this.email = email;
        this.password = password;
    }
}
