package org.example.gamerscove.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Map;
import java.util.HashMap;

@Data
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

    // Store as comma-separated string instead of PostgreSQL array
    @Column(name = "preferred_platforms", columnDefinition = "TEXT")
    private String preferredPlatformsString;

    // Store as comma-separated string instead of PostgreSQL array
    @Column(name = "favorite_games", columnDefinition = "TEXT")
    private String favoriteGamesString;

    // Store as simple JSON string instead of JSONB
    @Column(name = "gamertags", columnDefinition = "TEXT")
    private String gamertagsJson;

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

    // Utility methods for preferred platforms array
    public String[] getPreferredPlatforms() {
        if (preferredPlatformsString == null || preferredPlatformsString.trim().isEmpty()) {
            return new String[0];
        }
        return preferredPlatformsString.split(",");
    }

    public void setPreferredPlatforms(String[] platforms) {
        if (platforms == null || platforms.length == 0) {
            this.preferredPlatformsString = null;
        } else {
            this.preferredPlatformsString = String.join(",", platforms);
        }
    }

    // Utility methods for favorite games array
    public String[] getFavoriteGames() {
        if (favoriteGamesString == null || favoriteGamesString.trim().isEmpty()) {
            return new String[0];
        }
        return favoriteGamesString.split(",");
    }

    public void setFavoriteGames(String[] games) {
        if (games == null || games.length == 0) {
            this.favoriteGamesString = null;
        } else {
            this.favoriteGamesString = String.join(",", games);
        }
    }

    // Utility methods for gamertags map (simple JSON-like format)
    public Map<String, String> getGamertags() {
        Map<String, String> map = new HashMap<>();
        if (gamertagsJson == null || gamertagsJson.trim().isEmpty()) {
            return map;
        }

        // Parse simple format: "key1=value1,key2=value2"
        try {
            String[] pairs = gamertagsJson.split(",");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                if (keyValue.length == 2) {
                    map.put(keyValue[0].trim(), keyValue[1].trim());
                }
            }
        } catch (Exception e) {
            // If parsing fails, return empty map
            return new HashMap<>();
        }

        return map;
    }

    public void setGamertags(Map<String, String> gamertags) {
        if (gamertags == null || gamertags.isEmpty()) {
            this.gamertagsJson = null;
        } else {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> entry : gamertags.entrySet()) {
                if (!first) {
                    sb.append(",");
                }
                sb.append(entry.getKey()).append("=").append(entry.getValue());
                first = false;
            }
            this.gamertagsJson = sb.toString();
        }
    }

    // Helper method to add a single gamertag
    public void addGamertag(String platform, String gamertag) {
        Map<String, String> current = getGamertags();
        current.put(platform, gamertag);
        setGamertags(current);
    }

    // Helper method to remove a gamertag
    public void removeGamertag(String platform) {
        Map<String, String> current = getGamertags();
        current.remove(platform);
        setGamertags(current);
    }

    // Helper method to get a specific gamertag
    public String getGamertag(String platform) {
        return getGamertags().get(platform);
    }

    public UserEntity(String firebaseUid, String username, String email, String password) {
        this.firebaseUid = firebaseUid;
        this.username = username;
        this.email = email;
        this.password = password;
    }
}