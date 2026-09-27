package com.damir.othello.model;

import java.util.ArrayList;
import java.util.List;

public final class Board {

    public static final int SIZE = 8;

    private static final int[][] DIRECTIONS = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1},
    };

    private final Disc[][] grid;

    private Board(Disc[][] grid) {
        this.grid = grid;
    }

    public static Board initial() {
        Disc[][] grid = new Disc[SIZE][SIZE];
        for (Disc[] row : grid) {
            java.util.Arrays.fill(row, Disc.EMPTY);
        }
        grid[3][3] = Disc.WHITE;
        grid[3][4] = Disc.BLACK;
        grid[4][3] = Disc.BLACK;
        grid[4][4] = Disc.WHITE;
        return new Board(grid);
    }

    public static Board fromGrid(Disc[][] grid) {
        if (grid.length != SIZE) {
            throw new IllegalArgumentException("grid must be " + SIZE + "x" + SIZE);
        }
        Disc[][] copy = new Disc[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            if (grid[r].length != SIZE) {
                throw new IllegalArgumentException("grid must be " + SIZE + "x" + SIZE);
            }
            copy[r] = grid[r].clone();
        }
        return new Board(copy);
    }

    public Disc at(int row, int col) {
        return grid[row][col];
    }

    public Disc[][] gridSnapshot() {
        Disc[][] copy = new Disc[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            copy[r] = grid[r].clone();
        }
        return copy;
    }

    private static boolean inBounds(int row, int col) {
        return row >= 0 && row < SIZE && col >= 0 && col < SIZE;
    }

    private List<Move> flipsFor(int row, int col, Disc player) {
        if (grid[row][col] != Disc.EMPTY) {
            return List.of();
        }
        Disc opponent = player.opposite();
        List<Move> flips = new ArrayList<>();

        for (int[] dir : DIRECTIONS) {
            List<Move> line = new ArrayList<>();
            int r = row + dir[0];
            int c = col + dir[1];
            while (inBounds(r, c) && grid[r][c] == opponent) {
                line.add(new Move(r, c));
                r += dir[0];
                c += dir[1];
            }
            if (!line.isEmpty() && inBounds(r, c) && grid[r][c] == player) {
                flips.addAll(line);
            }
        }
        return flips;
    }

    public List<Move> legalMoves(Disc player) {
        List<Move> moves = new ArrayList<>();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (!flipsFor(r, c, player).isEmpty()) {
                    moves.add(new Move(r, c));
                }
            }
        }
        return moves;
    }

    public boolean isLegalMove(Move move, Disc player) {
        return !flipsFor(move.row(), move.col(), player).isEmpty();
    }

    public Board applyMove(Move move, Disc player) {
        List<Move> flips = flipsFor(move.row(), move.col(), player);
        if (flips.isEmpty()) {
            throw new IllegalArgumentException(
                    "illegal move for " + player + " at " + move);
        }

        Disc[][] next = gridSnapshot();
        next[move.row()][move.col()] = player;
        for (Move f : flips) {
            next[f.row()][f.col()] = player;
        }
        return new Board(next);
    }

    public boolean isGameOver() {
        return legalMoves(Disc.BLACK).isEmpty() && legalMoves(Disc.WHITE).isEmpty();
    }

    public int countDiscs(Disc player) {
        int count = 0;
        for (Disc[] row : grid) {
            for (Disc d : row) {
                if (d == player) {
                    count++;
                }
            }
        }
        return count;
    }
}
