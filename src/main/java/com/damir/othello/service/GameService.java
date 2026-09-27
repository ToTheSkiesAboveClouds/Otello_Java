package com.damir.othello.service;

import com.damir.othello.ai.OthelloAI;
import com.damir.othello.model.Disc;
import com.damir.othello.model.Move;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GameService {

    private final Map<String, GameSession> games = new ConcurrentHashMap<>();
    private final OthelloAI ai;

    public GameService(OthelloAI ai) {
        this.ai = ai;
    }

    public GameSession.View createGame(Disc humanColor) {
        String id = UUID.randomUUID().toString();
        GameSession session = new GameSession(id, humanColor);
        games.put(id, session);
        return session.start(ai);
    }

    public GameSession.View getGame(String id) {
        return find(id).snapshot();
    }

    public GameSession.View submitMove(String id, Move move) {
        return find(id).submitMove(move, ai);
    }

    private GameSession find(String id) {
        GameSession session = games.get(id);
        if (session == null) {
            throw new GameNotFoundException(id);
        }
        return session;
    }
}
