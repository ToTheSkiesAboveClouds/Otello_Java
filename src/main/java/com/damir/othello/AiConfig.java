package com.damir.othello;

import com.damir.othello.ai.MinimaxAI;
import com.damir.othello.ai.OthelloAI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    private static final int SEARCH_DEPTH = 6;

    @Bean
    public OthelloAI othelloAI() {
        return new MinimaxAI(SEARCH_DEPTH);
    }
}
