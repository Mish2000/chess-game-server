package com.chessgame.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "moves")
public class Move {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    @Column(nullable = false)
    private int moveNumber;

    @Column(nullable = false)
    private String fromSquare;

    @Column(nullable = false)
    private String toSquare;

    private String promotionPiece;

}