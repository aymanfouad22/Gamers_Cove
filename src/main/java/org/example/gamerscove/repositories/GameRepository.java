package org.example.gamerscove.repositories;

import org.example.gamerscove.domain.entities.GameEntity;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<GameEntity, Long> {
}
