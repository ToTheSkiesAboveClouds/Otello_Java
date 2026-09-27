package com.damir.othello.cli;

import com.damir.othello.model.Disc;
import com.damir.othello.service.GameSession;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Scanner;

public class OthelloCliClient {

    private static final String BASE_URL = "http://localhost:8080";
    private static final HttpClient http = HttpClient.newHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Othello vs AI ===");
        System.out.print("Play as (B)lack or (W)hite? Black moves first. [B]: ");
        String colorInput = scanner.nextLine().trim().toUpperCase();
        Disc humanColor = colorInput.startsWith("W") ? Disc.WHITE : Disc.BLACK;

        GameSession.View game;
        try {
            game = createGame(humanColor);
        } catch (IOException | InterruptedException e) {
            System.out.println("Couldn't reach the server at " + BASE_URL
                    + " — is OthelloApplication actually running? (" + e.getMessage() + ")");
            return;
        }
        printBoard(game);

        while (!game.gameOver()) {
            System.out.print("Your turn (" + game.humanColor()
                    + "). Enter move as 'row col' (0-7), e.g. '2 3': ");
            String line = scanner.nextLine().trim();
            String[] parts = line.split("\\s+");
            if (parts.length != 2) {
                System.out.println("Please enter two numbers separated by a space, like '2 3'.");
                continue;
            }

            int row, col;
            try {
                row = Integer.parseInt(parts[0]);
                col = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                System.out.println("Both parts need to be numbers 0-7.");
                continue;
            }

            try {
                game = submitMove(game.id(), row, col);
            } catch (MoveRejectedException e) {
                System.out.println("Move rejected: " + e.getMessage());
                continue;
            } catch (IOException | InterruptedException e) {
                System.out.println("Error talking to server: " + e.getMessage());
                continue;
            }
            printBoard(game);
        }

        System.out.println("=== Game over ===");
        if (game.winner() == null) {
            System.out.println("It's a draw: " + game.blackDiscs() + "-" + game.whiteDiscs());
        } else if (game.winner() == game.humanColor()) {
            System.out.println("You win, " + game.blackDiscs() + "-" + game.whiteDiscs() + "!");
        } else {
            System.out.println("The AI wins, " + game.blackDiscs() + "-" + game.whiteDiscs() + ".");
        }
    }

    private static GameSession.View createGame(Disc humanColor) throws IOException, InterruptedException {
        String body = "{\"humanColor\":\"" + humanColor + "\"}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/games"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readValue(response.body(), GameSession.View.class);
    }

    private static GameSession.View submitMove(String gameId, int row, int col)
            throws IOException, InterruptedException {
        String body = "{\"row\":" + row + ",\"col\":" + col + "}";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/games/" + gameId + "/moves"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new MoveRejectedException(extractError(response.body()));
        }
        return mapper.readValue(response.body(), GameSession.View.class);
    }

    private static String extractError(String json) {
        try {
            JsonNode node = mapper.readTree(json);
            return node.has("error") ? node.get("error").asText() : json;
        } catch (IOException e) {
            return json;
        }
    }

    private static void printBoard(GameSession.View game) {
        Disc[][] grid = game.grid();
        System.out.println();
        System.out.print("   ");
        for (int c = 0; c < 8; c++) {
            System.out.print(c + " ");
        }
        System.out.println();
        for (int r = 0; r < 8; r++) {
            System.out.print(r + "  ");
            for (int c = 0; c < 8; c++) {
                System.out.print(symbol(grid[r][c]) + " ");
            }
            System.out.println();
        }
        System.out.println("Black: " + game.blackDiscs() + "   White: " + game.whiteDiscs()
                + "   (" + game.turn() + " to move)");
        System.out.println();
    }

    private static String symbol(Disc d) {
        return switch (d) {
            case BLACK -> "B";
            case WHITE -> "W";
            case EMPTY -> ".";
        };
    }

    private static final class MoveRejectedException extends RuntimeException {
        MoveRejectedException(String message) {
            super(message);
        }
    }
}
