package com.chessgame.service;

import chesspresso.Chess;
import chesspresso.move.Move;
import chesspresso.position.Position;
import chesspresso.move.IllegalMoveException;
import com.chessgame.dto.MoveRequest;
import com.chessgame.entity.Game;
import com.chessgame.entity.User;
import com.chessgame.exception.ResourceNotFoundException;
import com.chessgame.repository.GameRepository;
import com.chessgame.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;

    @Autowired
    public GameService(GameRepository gameRepository, UserRepository userRepository) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
    }

    // Create a new game
    public Game createNewGame(User user) {
        Game game = new Game();
        game.setUser(user);
        game.setFen(Position.createInitialPosition().getFEN());
        game.setCreatedAt(LocalDateTime.now());
        gameRepository.save(game);
        return game;
    }

    // Get a game by ID and User
    public Game getGameByIdAndUser(Long gameId, User user) {
        return gameRepository.findByIdAndUser(gameId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Game not found for this user."));
    }


    // Make a move in the game
    public String makeMove(Game game, MoveRequest moveRequest) throws InvalidMoveException {
        try {
            // Load the position from the game's FEN
            Position position = new Position(game.getFen());

            // Convert move from algebraic notation to square indices
            int from = Chess.strToSqi(moveRequest.getFrom()); // e.g., "e2" to square index
            int to = Chess.strToSqi(moveRequest.getTo());     // e.g., "e4" to square index

            // Determine if the move is a capture
            boolean isCapturing = !position.isSquareEmpty(to);

            // Get the promotion piece if applicable
            int promoPiece = getPromotionPiece(moveRequest.getPromotion());

            // Create the move
            short move;
            if (promoPiece != Chess.NO_PIECE) {
                // Pawn promotion
                move = Move.getPawnMove(from, to, isCapturing, promoPiece);
            } else if (position.getPiece(from) == Chess.PAWN && Math.abs(Chess.deltaRow(from, to)) == 2) {
                // Pawn double move
                move = Move.getPawnMove(from, to, isCapturing, Chess.NO_PIECE);
            } else if (Move.isCastle(move = Move.getRegularMove(from, to, isCapturing))) {
                // Castling move
                move = position.getToPlay() == Chess.WHITE ? (to == Chess.G1 ? Move.WHITE_SHORT_CASTLE : Move.WHITE_LONG_CASTLE)
                        : (to == Chess.G8 ? Move.BLACK_SHORT_CASTLE : Move.BLACK_LONG_CASTLE);
            } else {
                // Regular move
                move = Move.getRegularMove(from, to, isCapturing);
            }

            // Validate and make the move
            position.doMove(move);

            // Update the game's FEN
            game.setFen(position.getFEN());
            gameRepository.save(game);

            // Return the new FEN
            return position.getFEN();
        } catch (IllegalMoveException e) {
            throw new InvalidMoveException("Illegal move: " + e.getMessage());
        } catch (Exception e) {
            throw new InvalidMoveException("Unexpected error: " + e.getMessage());
        }
    }

    // Reset the game
    public String resetGame(Game game) {
        Position position = Position.createInitialPosition();
        game.setFen(position.getFEN());
        gameRepository.save(game);
        return position.getFEN();
    }

    // Helper method to get piece name from piece type
    private String getPieceName(int pieceType) {
        return switch (pieceType) {
            case Chess.PAWN -> "pawn";
            case Chess.KNIGHT -> "knight";
            case Chess.BISHOP -> "bishop";
            case Chess.ROOK -> "rook";
            case Chess.QUEEN -> "queen";
            case Chess.KING -> "king";
            default -> "unknown piece";
        };
    }

    private int getPromotionPiece(String promo) {
        if (promo == null || promo.isEmpty()) return Chess.NO_PIECE;
        return switch (promo.toLowerCase()) {
            case "q" -> Chess.QUEEN;
            case "r" -> Chess.ROOK;
            case "b" -> Chess.BISHOP;
            case "n" -> Chess.KNIGHT;
            default -> Chess.NO_PIECE;
        };
    }

    // Undo the last move
    public String undoMove(Game game) throws NoMovesToUndoException {
        try {
            Position position = new Position(game.getFen());

            if (position.canUndoMove()) {
                position.undoMove();
                game.setFen(position.getFEN());
                gameRepository.save(game);
                return position.getFEN();
            } else {
                throw new NoMovesToUndoException("No moves to undo.");
            }
        } catch (Exception e) {
            throw new NoMovesToUndoException("Unexpected error: " + e.getMessage());
        }
    }

    public static class InvalidMoveException extends Exception {
        public InvalidMoveException(String message) {
            super(message);
        }
    }

    public static class NoMovesToUndoException extends Exception {
        public NoMovesToUndoException(String message) {
            super(message);
        }
    }

}





