package com.damir.othello.service;

import com.damir.othello.ai.GreedyAI;
import com.damir.othello.ai.OthelloAI;
import com.damir.othello.model.Disc;
import com.damir.othello.model.Move;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameSessionTest {

    private final OthelloAI ai = new GreedyAI();

    @Test
    void startAsBlack_isImmediatelyTheHumansTurn() {
        GameSession session = new GameSession("g1", Disc.BLACK);
        GameSession.View view = session.start(ai);

        assertEquals(Disc.BLACK, view.turn());
        assertEquals(Disc.BLACK, view.humanColor());
        assertFalse(view.gameOver());
        assertEquals(2, view.blackDiscs());
        assertEquals(2, view.whiteDiscs());
    }

    @Test
    void startAsWhite_aiPlaysBlacksOpeningMoveFirst() {
        GameSession session = new GameSession("g2", Disc.WHITE);
        GameSession.View view = session.start(ai);

        assertEquals(Disc.WHITE, view.turn());
        assertFalse(view.gameOver());
        assertTrue(view.blackDiscs() > 2);
    }

    @Test
    void submitMove_appliesTheHumanMoveThenTheAiReplies() {
        GameSession session = new GameSession("g3", Disc.BLACK);
        GameSession.View before = session.start(ai);
        int totalBefore = before.blackDiscs() + before.whiteDiscs();

        GameSession.View after = session.submitMove(new Move(2, 3), ai);
        int totalAfter = after.blackDiscs() + after.whiteDiscs();

        assertTrue(totalAfter >= totalBefore + 2);
    }

    @Test
    void submitMove_rejectsAnIllegalMove() {
        GameSession session = new GameSession("g4", Disc.BLACK);
        session.start(ai);

        assertThrows(IllegalArgumentException.class,
                () -> session.submitMove(new Move(0, 0), ai));
    }

    @Test
    void submitMove_rejectsAMoveWhenItIsNotYourTurn() {
        GameSession session = new GameSession("g5", Disc.WHITE);

        assertThrows(IllegalStateException.class,
                () -> session.submitMove(new Move(2, 3), ai));
    }
}
