package Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;
import io.hypersistence.utils.hibernate.type.array.StringArrayType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

@Entity
@Table(name = "games")
@Getter @Setter
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "external_api_id", nullable = false, unique = true, length = 100)
    private String externalApiId;

    @NotNull
    @Size(max = 200)
    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "cover_image_url", columnDefinition = "TEXT")
    private String coverImageUrl;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Type(StringArrayType.class)
    @Column(name = "platforms", columnDefinition = "text[]")
    private String[] platforms;

    @Type(StringArrayType.class)
    @Column(name = "genres", columnDefinition = "text[]")
    private String[] genres;

    // Default constructor
    public Game() {}

    // Constructor with required fields
    public Game(String externalApiId, String title) {
        this.externalApiId = externalApiId;
        this.title = title;
    }

    // Full constructor
    public Game(String externalApiId, String title, String description,
                String coverImageUrl, LocalDate releaseDate,
                String[] platforms, String[] genres) {
        this.externalApiId = externalApiId;
        this.title = title;
        this.description = description;
        this.coverImageUrl = coverImageUrl;
        this.releaseDate = releaseDate;
        this.platforms = platforms;
        this.genres = genres;
    }
    // Convenience methods for array operations
    public boolean hasPlatform(String platform) {
        if (platforms == null) return false;
        for (String p : platforms) {
            if (platform.equalsIgnoreCase(p)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasGenre(String genre) {
        if (genres == null) return false;
        for (String g : genres) {
            if (genre.equalsIgnoreCase(g)) {
                return true;
            }
        }
        return false;
    }

    public void addPlatform(String platform) {
        if (platforms == null) {
            platforms = new String[]{platform};
        } else {
            // Check if platform already exists
            if (hasPlatform(platform)) {
                return;
            }
            // Add new platform
            String[] newPlatforms = new String[platforms.length + 1];
            System.arraycopy(platforms, 0, newPlatforms, 0, platforms.length);
            newPlatforms[platforms.length] = platform;
            platforms = newPlatforms;
        }
    }

    public void addGenre(String genre) {
        if (genres == null) {
            genres = new String[]{genre};
        } else {
            // Check if genre already exists
            if (hasGenre(genre)) {
                return;
            }
            // Add new genre
            String[] newGenres = new String[genres.length + 1];
            System.arraycopy(genres, 0, newGenres, 0, genres.length);
            newGenres[genres.length] = genre;
            genres = newGenres;
        }
    }

    public void removePlatform(String platform) {
        if (platforms == null) return;

        java.util.List<String> platformList = new java.util.ArrayList<>();
        for (String p : platforms) {
            if (!platform.equalsIgnoreCase(p)) {
                platformList.add(p);
            }
        }
        platforms = platformList.toArray(new String[0]);
    }

    public void removeGenre(String genre) {
        if (genres == null) return;

        java.util.List<String> genreList = new java.util.ArrayList<>();
        for (String g : genres) {
            if (!genre.equalsIgnoreCase(g)) {
                genreList.add(g);
            }
        }
        genres = genreList.toArray(new String[0]);
    }

    // Get platforms and genres as comma-separated strings for display
    public String getPlatformsAsString() {
        if (platforms == null || platforms.length == 0) {
            return "";
        }
        return String.join(", ", platforms);
    }

    public String getGenresAsString() {
        if (genres == null || genres.length == 0) {
            return "";
        }
        return String.join(", ", genres);
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", externalApiId='" + externalApiId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", coverImageUrl='" + coverImageUrl + '\'' +
                ", releaseDate=" + releaseDate +
                ", platforms=" + getPlatformsAsString() +
                ", genres=" + getGenresAsString() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return externalApiId != null ? externalApiId.equals(game.externalApiId) : game.externalApiId == null;
    }

    @Override
    public int hashCode() {
        return externalApiId != null ? externalApiId.hashCode() : 0;
    }
}