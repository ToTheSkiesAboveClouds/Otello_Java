package com.damir.othello.service;

public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(String id) {
        super("no such game: " + id);
    }
}
