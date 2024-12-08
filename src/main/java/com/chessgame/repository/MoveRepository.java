package com.chessgame.repository;

import com.chessgame.entity.Game;
import com.chessgame.entity.Move;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MoveRepository extends JpaRepository<Move, Long> {
    List<Move> findByGameOrderByMoveNumberAsc(Game game);
}