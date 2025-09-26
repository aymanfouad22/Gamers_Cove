package org.example.gamerscove.repositories;

import org.springframework.data.repository.CrudRepository;
import org.example.gamerscove.domain.entities.UserEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<UserEntity,Integer> {
}
