package com.chessgame.controller;

import com.chessgame.dto.MoveRequest;
import com.chessgame.entity.Game;
import com.chessgame.entity.User;
import com.chessgame.service.GameService;
import com.chessgame.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
public class GameController {

    private final GameService gameService;
    private final UserService userService;

    @Autowired
    public GameController(GameService gameService, UserService userService) {
        this.gameService = gameService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<Long> createGame(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName());
        Game game = gameService.createNewGame(user);
        return ResponseEntity.ok(game.getId());
    }

    @PostMapping("/{gameId}/move")
    public ResponseEntity<String> makeMove(
            @PathVariable Long gameId,
            @Valid @RequestBody MoveRequest moveRequest,
            Authentication authentication
    ) throws GameService.InvalidMoveException {
        User user = userService.findByUsername(authentication.getName());
        Game game = gameService.getGameByIdAndUser(gameId, user);

        String result = gameService.makeMove(game, moveRequest);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{gameId}/reset")
    public ResponseEntity<String> resetGame(
            @PathVariable Long gameId,
            Authentication authentication
    ) {
        User user = userService.findByUsername(authentication.getName());
        Game game = gameService.getGameByIdAndUser(gameId, user);

        String newFEN = gameService.resetGame(game);
        return ResponseEntity.ok(newFEN);
    }

    @PostMapping("/{gameId}/undo")
    public ResponseEntity<String> undoMove(
            @PathVariable Long gameId,
            Authentication authentication
    ) throws GameService.NoMovesToUndoException {
        User user = userService.findByUsername(authentication.getName());
        Game game = gameService.getGameByIdAndUser(gameId, user);

        String result = gameService.undoMove(game);
        return ResponseEntity.ok(result);
    }
}











