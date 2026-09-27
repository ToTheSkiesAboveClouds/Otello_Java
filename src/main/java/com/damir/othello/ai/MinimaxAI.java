package com.damir.othello.ai;

import com.damir.othello.model.Board;
import com.damir.othello.model.Disc;
import com.damir.othello.model.Move;

import java.util.List;

public final class MinimaxAI implements OthelloAI {

    private static final int[][] WEIGHTS = {
            {120, -20, 20, 5, 5, 20, -20, 120},
            {-20, -40, -5, -5, -5, -5, -40, -20},
            {20, -5, 15, 3, 3, 15, -5, 20},
            {5, -5, 3, 3, 3, 3, -5, 5},
            {5, -5, 3, 3, 3, 3, -5, 5},
            {20, -5, 15, 3, 3, 15, -5, 20},
            {-20, -40, -5, -5, -5, -5, -40, -20},
            {120, -20, 20, 5, 5, 20, -20, 120},
    };

    private static final int MOBILITY_WEIGHT = 10;

    private final int maxDepth;

    public MinimaxAI(int maxDepth) {
        if (maxDepth < 1) {
            throw new IllegalArgumentException("maxDepth must be >= 1");
        }
        this.maxDepth = maxDepth;
    }

    @Override
    public Move chooseMove(Board board, Disc player) {
        List<Move> candidates = board.legalMoves(player);
        if (candidates.isEmpty()) {
            throw new IllegalStateException("no legal moves available for " + player);
        }

        Move best = candidates.get(0);
        int bestScore = Integer.MIN_VALUE;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;

        for (Move move : candidates) {
            Board next = board.applyMove(move, player);
            int score = search(next, player.opposite(), player, maxDepth - 1, alpha, beta);
            if (score > bestScore) {
                bestScore = score;
                best = move;
            }
            alpha = Math.max(alpha, bestScore);
        }
        return best;
    }

    private int search(Board board, Disc toMove, Disc maximizing, int depth, int alpha, int beta) {
        if (depth == 0 || board.isGameOver()) {
            return evaluate(board, maximizing);
        }

        List<Move> moves = board.legalMoves(toMove);
        if (moves.isEmpty()) {
            return search(board, toMove.opposite(), maximizing, depth, alpha, beta);
        }

        boolean isMaximizingTurn = toMove == maximizing;
        int best = isMaximizingTurn ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        for (Move move : moves) {
            Board next = board.applyMove(move, toMove);
            int score = search(next, toMove.opposite(), maximizing, depth - 1, alpha, beta);

            if (isMaximizingTurn) {
                best = Math.max(best, score);
                alpha = Math.max(alpha, best);
            } else {
                best = Math.min(best, score);
                beta = Math.min(beta, best);
            }
            if (alpha >= beta) {
                break;
            }
        }
        return best;
    }

    private int evaluate(Board board, Disc player) {
        Disc opponent = player.opposite();

        int positional = 0;
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                Disc d = board.at(r, c);
                if (d == player) {
                    positional += WEIGHTS[r][c];
                } else if (d == opponent) {
                    positional -= WEIGHTS[r][c];
                }
            }
        }

        int mobility = board.legalMoves(player).size() - board.legalMoves(opponent).size();
        return positional + MOBILITY_WEIGHT * mobility;
    }
}
