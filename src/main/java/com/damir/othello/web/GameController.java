package com.damir.othello.web;

import com.damir.othello.model.Disc;
import com.damir.othello.model.Move;
import com.damir.othello.service.GameService;
import com.damir.othello.service.GameSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    public record CreateGameRequest(Disc humanColor) {
    }

    public record MoveRequest(int row, int col) {
    }

    @PostMapping
    public ResponseEntity<GameSession.View> createGame(
            @RequestBody(required = false) CreateGameRequest request) {
        Disc humanColor = (request != null && request.humanColor() != null)
                ? request.humanColor()
                : Disc.BLACK;
        GameSession.View view = gameService.createGame(humanColor);
        return ResponseEntity.status(HttpStatus.CREATED).body(view);
    }

    @GetMapping("/{id}")
    public GameSession.View getGame(@PathVariable String id) {
        return gameService.getGame(id);
    }

    @PostMapping("/{id}/moves")
    public GameSession.View submitMove(@PathVariable String id, @RequestBody MoveRequest request) {
        return gameService.submitMove(id, new Move(request.row(), request.col()));
    }
}
