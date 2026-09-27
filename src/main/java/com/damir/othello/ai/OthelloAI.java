package com.damir.othello.ai;

import com.damir.othello.model.Board;
import com.damir.othello.model.Disc;
import com.damir.othello.model.Move;

public interface OthelloAI {
    Move chooseMove(Board board, Disc player);
}
