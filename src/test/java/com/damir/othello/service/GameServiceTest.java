package com.damir.othello.service;

import com.damir.othello.ai.GreedyAI;
import com.damir.othello.model.Disc;
import com.damir.othello.model.Move;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    private final GameService service = new GameService(new GreedyAI());

    @Test
    void createGame_returnsAFreshBoardForTheRequestedColor() {
        GameSession.View view = service.createGame(Disc.BLACK);

        assertNotNull(view.id());
        assertEquals(Disc.BLACK, view.humanColor());
        assertEquals(Disc.WHITE, view.aiColor());
    }

    @Test
    void getGame_returnsTheSameGameByItsId() {
        GameSession.View created = service.createGame(Disc.BLACK);
        GameSession.View fetched = service.getGame(created.id());

        assertEquals(created.id(), fetched.id());
    }

    @Test
    void getGame_unknownIdThrowsNotFound() {
        assertThrows(GameNotFoundException.class, () -> service.getGame("no-such-id"));
    }

    @Test
    void submitMove_worksThroughTheServiceToo() {
        GameSession.View created = service.createGame(Disc.BLACK);
        GameSession.View after = service.submitMove(created.id(), new Move(2, 3));

        assertTrue(after.blackDiscs() + after.whiteDiscs()
                > created.blackDiscs() + created.whiteDiscs());
    }
}
