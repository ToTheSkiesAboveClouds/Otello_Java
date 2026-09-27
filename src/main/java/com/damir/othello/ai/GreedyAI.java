package com.damir.othello.ai;

import com.damir.othello.model.Board;
import com.damir.othello.model.Disc;
import com.damir.othello.model.Move;

import java.util.List;

public final class GreedyAI implements OthelloAI {

    @Override
    public Move chooseMove(Board board, Disc player) {
        List<Move> candidates = board.legalMoves(player);
        if (candidates.isEmpty()) {
            throw new IllegalStateException("no legal moves available for " + player);
        }

        Move best = candidates.get(0);
        int bestFlips = -1;
        int before = board.countDiscs(player);

        for (Move move : candidates) {
            Board next = board.applyMove(move, player);
            int flips = next.countDiscs(player) - before - 1;
            if (flips > bestFlips) {
                bestFlips = flips;
                best = move;
            }
        }
        return best;
    }
}
