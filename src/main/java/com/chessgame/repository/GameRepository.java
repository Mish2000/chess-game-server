package com.chessgame.repository;

import com.chessgame.entity.Game;
import com.chessgame.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findByIdAndUser(Long id, User user);
}
