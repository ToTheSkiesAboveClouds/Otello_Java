package com.damir.othello.ai;

import com.damir.othello.model.Board;
import com.damir.othello.model.Disc;
import com.damir.othello.model.Move;

import java.util.List;
import java.util.Random;

public final class RandomAI implements OthelloAI {

    private final Random random;

    public RandomAI() {
        this(new Random());
    }

    public RandomAI(Random random) {
        this.random = random;
    }

    @Override
    public Move chooseMove(Board board, Disc player) {
        List<Move> candidates = board.legalMoves(player);
        if (candidates.isEmpty()) {
            throw new IllegalStateException("no legal moves available for " + player);
        }
        return candidates.get(random.nextInt(candidates.size()));
    }
}
