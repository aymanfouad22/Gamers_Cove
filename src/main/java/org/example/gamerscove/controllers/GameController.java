package org.example.gamerscove.controllers;

import org.example.gamerscove.domain.dto.GameDto;
import org.example.gamerscove.domain.entities.GameEntity;
import org.example.gamerscove.mappers.Mapper;
import org.example.gamerscove.services.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    private final GameService gameService;
    private final Mapper<GameEntity, GameDto> gameMapper;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    // IGDB API Configuration
    @Value("${igdb.client.id:your-client-id}")
    private String igdbClientId;

    @Value("${igdb.access.token:your-access-token}")
    private String igdbAccessToken;

    private static final String IGDB_BASE_URL = "https://api.igdb.com/v4";
    private static final String IGDB_GAMES_ENDPOINT = IGDB_BASE_URL + "/games";

    public GameController(GameService gameService, Mapper<GameEntity, GameDto> gameMapper) {
        this.gameService = gameService;
        this.gameMapper = gameMapper;
        this.restTemplate = new RestTemplate();
        this.objectMapper = new ObjectMapper();
    }

    // DTO for IGDB API Response
    public static class IGDBGameResponse {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("name")
        private String name;

        @JsonProperty("summary")
        private String summary;

        @JsonProperty("cover")
        private IGDBCover cover;

        @JsonProperty("first_release_date")
        private Long firstReleaseDate;

        @JsonProperty("platforms")
        private List<IGDBPlatform> platforms;

        @JsonProperty("genres")
        private List<IGDBGenre> genres;

        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getSummary() { return summary; }
        public void setSummary(String summary) { this.summary = summary; }

        public IGDBCover getCover() { return cover; }
        public void setCover(IGDBCover cover) { this.cover = cover; }

        public Long getFirstReleaseDate() { return firstReleaseDate; }
        public void setFirstReleaseDate(Long firstReleaseDate) { this.firstReleaseDate = firstReleaseDate; }

        public List<IGDBPlatform> getPlatforms() { return platforms; }
        public void setPlatforms(List<IGDBPlatform> platforms) { this.platforms = platforms; }

        public List<IGDBGenre> getGenres() { return genres; }
        public void setGenres(List<IGDBGenre> genres) { this.genres = genres; }
    }

    public static class IGDBCover {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("url")
        private String url;

        @JsonProperty("image_id")
        private String imageId;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }

        public String getImageId() { return imageId; }
        public void setImageId(String imageId) { this.imageId = imageId; }
    }

    public static class IGDBPlatform {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("name")
        private String name;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    public static class IGDBGenre {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("name")
        private String name;

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    // CRUD Endpoints

    @PostMapping(path = "/games")
    public ResponseEntity<GameDto> createGame(@RequestBody GameDto gameDto) {
        logger.info("=== POST /api/games ENDPOINT CALLED ===");
        logger.info("Received GameDto: {}", gameDto);
        logger.info("======================================");

        try {
            GameEntity gameEntity = gameMapper.mapFrom(gameDto);
            GameEntity savedGame = gameService.createGameEntity(gameEntity);
            GameDto savedGameDto = gameMapper.mapTo(savedGame);

            logger.info("Game created successfully: {}", savedGameDto.getTitle());
            return ResponseEntity.ok(savedGameDto);
        } catch (Exception e) {
            logger.error("Error creating game: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(path = "/games/{gameId}")
    public ResponseEntity<GameDto> getGameById(@PathVariable("gameId") Long gameId) {
        logger.info("=== GET /api/games/{} ENDPOINT CALLED ===", gameId);
        logger.info("Fetching game with ID: {}", gameId);
        logger.info("========================================");

        Optional<GameEntity> game = gameService.findById(gameId);

        if (game.isPresent()) {
            GameDto gameDto = gameMapper.mapTo(game.get());
            logger.info("Found game: {}", gameDto.getTitle());
            return ResponseEntity.ok(gameDto);
        } else {
            logger.warn("Game not found with ID: {}", gameId);
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(path = "/games/title/{title}")
    public ResponseEntity<GameDto> getGameByTitle(@PathVariable("title") String title) {
        logger.info("=== GET /api/games/title/{} ENDPOINT CALLED ===", title);
        logger.info("Fetching game with title: {}", title);
        logger.info("===============================================");

        Optional<GameEntity> game = gameService.findTitle(title);

        if (game.isPresent()) {
            GameDto gameDto = gameMapper.mapTo(game.get());
            logger.info("Found game: {}", gameDto.getTitle());
            return ResponseEntity.ok(gameDto);
        } else {
            logger.warn("Game not found with title: {}", title);
            return ResponseEntity.notFound().build();
        }
    }

    // IGDB API Integration

    @PostMapping(path = "/games/populate-from-igdb")
    public ResponseEntity<String> populateGamesFromIGDB() {
        logger.info("=== POST /api/games/populate-from-igdb ENDPOINT CALLED ===");
        logger.info("Starting to populate games from IGDB API (first 5 games)");
        logger.info("========================================================");

        try {
            List<IGDBGameResponse> igdbGames = fetchGamesFromIGDB();

            int savedCount = 0;
            for (IGDBGameResponse igdbGame : igdbGames) {
                try {
                    GameEntity gameEntity = convertIGDBToGameEntity(igdbGame);
                    gameService.createGameEntity(gameEntity);
                    savedCount++;
                    logger.info("Successfully saved game: {}", gameEntity.getTitle());
                } catch (Exception e) {
                    logger.warn("Failed to save game {}: {}", igdbGame.getName(), e.getMessage());
                }
            }

            String message = String.format("Successfully populated %d games from IGDB API", savedCount);
            logger.info(message);
            return ResponseEntity.ok(message);

        } catch (Exception e) {
            logger.error("Error populating games from IGDB: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body("Failed to populate games from IGDB: " + e.getMessage());
        }
    }

    @GetMapping(path = "/games/search-igdb/{query}")
    public ResponseEntity<List<GameDto>> searchGamesInIGDB(@PathVariable("query") String query) {
        logger.info("=== GET /api/games/search-igdb/{} ENDPOINT CALLED ===", query);
        logger.info("Searching for games in IGDB with query: {}", query);
        logger.info("===================================================");

        try {
            List<IGDBGameResponse> igdbGames = searchGamesFromIGDB(query);

            List<GameDto> gameDtos = igdbGames.stream()
                    .map(this::convertIGDBToGameEntity)
                    .map(gameMapper::mapTo)
                    .collect(Collectors.toList());

            logger.info("Found {} games matching query: {}", gameDtos.size(), query);
            return ResponseEntity.ok(gameDtos);

        } catch (Exception e) {
            logger.error("Error searching games in IGDB: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }


    private List<IGDBGameResponse> fetchGamesFromIGDB() {
        logger.info("Fetching first 5 games from IGDB API...");

        HttpHeaders headers = createIGDBHeaders();
        String requestBody = "fields name,summary,cover.url,cover.image_id,first_release_date,platforms.name,genres.name; limit 5; sort first_release_date desc;";

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<IGDBGameResponse[]> response = restTemplate.exchange(
                    IGDB_GAMES_ENDPOINT,
                    HttpMethod.POST,
                    entity,
                    IGDBGameResponse[].class
            );

            IGDBGameResponse[] games = response.getBody();
            return games != null ? Arrays.asList(games) : List.of();

        } catch (Exception e) {
            logger.error("Failed to fetch games from IGDB: {}", e.getMessage());
            throw new RuntimeException("IGDB API request failed", e);
        }
    }

    private List<IGDBGameResponse> searchGamesFromIGDB(String query) {
        logger.info("Searching games in IGDB with query: {}", query);

        HttpHeaders headers = createIGDBHeaders();
        String requestBody = String.format(
                "search \"%s\"; fields name,summary,cover.url,cover.image_id,first_release_date,platforms.name,genres.name; limit 10;",
                query.replace("\"", "\\\"")
        );

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<IGDBGameResponse[]> response = restTemplate.exchange(
                    IGDB_GAMES_ENDPOINT,
                    HttpMethod.POST,
                    entity,
                    IGDBGameResponse[].class
            );

            IGDBGameResponse[] games = response.getBody();
            return games != null ? Arrays.asList(games) : List.of();

        } catch (Exception e) {
            logger.error("Failed to search games in IGDB: {}", e.getMessage());
            throw new RuntimeException("IGDB API search failed", e);
        }
    }

    private HttpHeaders createIGDBHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Client-ID", igdbClientId);
        headers.set("Authorization", "Bearer " + igdbAccessToken);
        headers.set("Accept", "application/json");
        return headers;
    }

    private GameEntity convertIGDBToGameEntity(IGDBGameResponse igdbGame) {
        GameEntity gameEntity = new GameEntity();

        gameEntity.setExternalApiId("igdb_" + igdbGame.getId());
        gameEntity.setTitle(igdbGame.getName());
        gameEntity.setDescription(igdbGame.getSummary() != null ? igdbGame.getSummary() : "");

        if (igdbGame.getCover() != null && igdbGame.getCover().getImageId() != null) {
            String coverUrl = "https://images.igdb.com/igdb/image/upload/t_cover_big/" + igdbGame.getCover().getImageId() + ".jpg";
            gameEntity.setCoverImageUrl(coverUrl);
        }

        if (igdbGame.getFirstReleaseDate() != null) {
            LocalDate releaseDate = Instant.ofEpochSecond(igdbGame.getFirstReleaseDate())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
            gameEntity.setReleaseDate(releaseDate);
        }

        if (igdbGame.getPlatforms() != null && !igdbGame.getPlatforms().isEmpty()) {
            String[] platforms = igdbGame.getPlatforms().stream()
                    .map(IGDBPlatform::getName)
                    .toArray(String[]::new);
            gameEntity.setPlatforms(platforms);
        }

        if (igdbGame.getGenres() != null && !igdbGame.getGenres().isEmpty()) {
            String[] genres = igdbGame.getGenres().stream()
                    .map(IGDBGenre::getName)
                    .toArray(String[]::new);
            gameEntity.setGenres(genres);
        }

        return gameEntity;
    }
}