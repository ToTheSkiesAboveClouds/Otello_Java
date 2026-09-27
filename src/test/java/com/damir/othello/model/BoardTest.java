package com.damir.othello.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void initialBoard_blackHasFourLegalMoves() {
        Board board = Board.initial();
        List<Move> blackMoves = board.legalMoves(Disc.BLACK);

        assertEquals(4, blackMoves.size());
        assertTrue(blackMoves.contains(new Move(2, 3)));
        assertTrue(blackMoves.contains(new Move(3, 2)));
        assertTrue(blackMoves.contains(new Move(4, 5)));
        assertTrue(blackMoves.contains(new Move(5, 4)));
    }

    @Test
    void initialBoard_startsWithTwoDiscsEach() {
        Board board = Board.initial();
        assertEquals(2, board.countDiscs(Disc.BLACK));
        assertEquals(2, board.countDiscs(Disc.WHITE));
        assertFalse(board.isGameOver());
    }

    @Test
    void applyMove_flipsTheCapturedLine() {
        Board board = Board.initial();
        Board after = board.applyMove(new Move(2, 3), Disc.BLACK);

        assertEquals(Disc.BLACK, after.at(2, 3));
        assertEquals(Disc.BLACK, after.at(3, 3));
        assertEquals(4, after.countDiscs(Disc.BLACK));
        assertEquals(1, after.countDiscs(Disc.WHITE));
    }

    @Test
    void applyMove_rejectsAnIllegalMove() {
        Board board = Board.initial();
        assertThrows(IllegalArgumentException.class,
                () -> board.applyMove(new Move(0, 0), Disc.BLACK));
    }

    @Test
    void isLegalMove_agreesWithLegalMoves() {
        Board board = Board.initial();
        for (Move m : board.legalMoves(Disc.BLACK)) {
            assertTrue(board.isLegalMove(m, Disc.BLACK));
        }
        assertFalse(board.isLegalMove(new Move(0, 0), Disc.BLACK));
    }

    @Test
    void fromGrid_buildsAnArbitraryPosition() {
        Disc[][] grid = emptyGrid();
        grid[0][1] = Disc.WHITE;
        grid[0][2] = Disc.BLACK;

        Board board = Board.fromGrid(grid);
        List<Move> blackMoves = board.legalMoves(Disc.BLACK);

        assertEquals(1, blackMoves.size());
        assertTrue(blackMoves.contains(new Move(0, 0)));
    }

    @Test
    void gridSnapshot_isADefensiveCopy() {
        Board board = Board.initial();
        Disc[][] snapshot = board.gridSnapshot();
        snapshot[0][0] = Disc.BLACK;

        assertEquals(Disc.EMPTY, board.at(0, 0));
    }

    private static Disc[][] emptyGrid() {
        Disc[][] grid = new Disc[Board.SIZE][Board.SIZE];
        for (Disc[] row : grid) {
            java.util.Arrays.fill(row, Disc.EMPTY);
        }
        return grid;
    }
}
