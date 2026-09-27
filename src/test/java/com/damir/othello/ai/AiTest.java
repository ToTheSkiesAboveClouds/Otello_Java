package com.damir.othello.ai;

import com.damir.othello.model.Board;
import com.damir.othello.model.Disc;
import com.damir.othello.model.Move;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class AiTest {

    @Test
    void allThreeAisAlwaysReturnALegalMove() {
        OthelloAI[] ais = {new RandomAI(new Random(42)), new GreedyAI(), new MinimaxAI(4)};

        for (OthelloAI ai : ais) {
            Board board = Board.initial();
            Disc turn = Disc.BLACK;

            for (int i = 0; i < 20 && !board.isGameOver(); i++) {
                List<Move> legal = board.legalMoves(turn);
                if (legal.isEmpty()) {
                    turn = turn.opposite();
                    continue;
                }
                Move chosen = ai.chooseMove(board, turn);
                assertTrue(legal.contains(chosen),
                        ai.getClass().getSimpleName() + " picked an illegal move: " + chosen);
                board = board.applyMove(chosen, turn);
                turn = turn.opposite();
            }
        }
    }

    @Test
    void minimaxPrefersTheCornerOverABiggerImmediateFlip() {
        Disc[][] grid = new Disc[Board.SIZE][Board.SIZE];
        for (Disc[] row : grid) {
            java.util.Arrays.fill(row, Disc.EMPTY);
        }
        grid[0][1] = Disc.WHITE;
        grid[0][2] = Disc.BLACK;
        grid[3][3] = Disc.WHITE;
        grid[3][4] = Disc.WHITE;
        grid[3][5] = Disc.WHITE;
        grid[3][6] = Disc.BLACK;

        Board board = Board.fromGrid(grid);
        assertEquals(2, board.legalMoves(Disc.BLACK).size());

        assertEquals(new Move(3, 2), new GreedyAI().chooseMove(board, Disc.BLACK));
        assertEquals(new Move(0, 0), new MinimaxAI(1).chooseMove(board, Disc.BLACK));
    }

    @Test
    void aRandomVsRandomGameAlwaysReachesGameOver() {
        RandomAI ai = new RandomAI(new Random(7));
        Board board = Board.initial();
        Disc turn = Disc.BLACK;

        int plies = 0;
        while (!board.isGameOver() && plies < 200) {
            List<Move> legal = board.legalMoves(turn);
            if (legal.isEmpty()) {
                turn = turn.opposite();
                plies++;
                continue;
            }
            board = board.applyMove(ai.chooseMove(board, turn), turn);
            turn = turn.opposite();
            plies++;
        }

        assertTrue(board.isGameOver());
        int total = board.countDiscs(Disc.BLACK) + board.countDiscs(Disc.WHITE);
        assertTrue(total >= 4 && total <= 64);
    }
}
