package com.damir.othello.model;

public enum Disc {
    BLACK,
    WHITE,
    EMPTY;

    public Disc opposite() {
        return switch (this) {
            case BLACK -> WHITE;
            case WHITE -> BLACK;
            case EMPTY -> throw new IllegalStateException("EMPTY has no opposite");
        };
    }
}
