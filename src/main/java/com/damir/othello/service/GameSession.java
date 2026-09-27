package com.damir.othello.service;

import com.damir.othello.ai.OthelloAI;
import com.damir.othello.model.Board;
import com.damir.othello.model.Disc;
import com.damir.othello.model.Move;

public final class GameSession {

    public record View(
            String id,
            Disc[][] grid,
            Disc turn,
            Disc humanColor,
            Disc aiColor,
            boolean gameOver,
            int blackDiscs,
            int whiteDiscs,
            Disc winner
    ) {
    }

    private final String id;
    private final Disc humanColor;
    private final Disc aiColor;

    private Board board;
    private Disc turn;

    public GameSession(String id, Disc humanColor) {
        this.id = id;
        this.humanColor = humanColor;
        this.aiColor = humanColor.opposite();
        this.board = Board.initial();
        this.turn = Disc.BLACK;
    }

    public String id() {
        return id;
    }

    public synchronized View start(OthelloAI ai) {
        advanceAiAndPasses(ai);
        return snapshot();
    }

    public synchronized View submitMove(Move move, OthelloAI ai) {
        if (board.isGameOver()) {
            throw new IllegalStateException("the game is already over");
        }
        if (turn != humanColor) {
            throw new IllegalStateException("it is not your turn");
        }
        if (!board.legalMoves(humanColor).contains(move)) {
            throw new IllegalArgumentException("illegal move: " + move);
        }

        board = board.applyMove(move, humanColor);
        turn = turn.opposite();
        advanceAiAndPasses(ai);
        return snapshot();
    }

    private void advanceAiAndPasses(OthelloAI ai) {
        while (!board.isGameOver()) {
            if (board.legalMoves(turn).isEmpty()) {
                turn = turn.opposite();
                continue;
            }
            if (turn == humanColor) {
                return;
            }
            Move aiMove = ai.chooseMove(board, aiColor);
            board = board.applyMove(aiMove, aiColor);
            turn = turn.opposite();
        }
    }

    public synchronized View snapshot() {
        int black = board.countDiscs(Disc.BLACK);
        int white = board.countDiscs(Disc.WHITE);
        Disc winner = null;
        if (board.isGameOver()) {
            if (black > white) {
                winner = Disc.BLACK;
            } else if (white > black) {
                winner = Disc.WHITE;
            }
        }
        return new View(id, board.gridSnapshot(), turn, humanColor, aiColor,
                board.isGameOver(), black, white, winner);
    }
}
