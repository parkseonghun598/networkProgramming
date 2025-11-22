package server.core;

import server.handler.ClientHandler;
import server.util.GameStateSerializer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameLoop implements Runnable {

    private final GameState gameState;
    private final List<ClientHandler> clients;
    private volatile boolean running = true;

    public GameLoop(GameState gameState) {
        this.gameState = gameState;
        this.clients = new CopyOnWriteArrayList<>();
    }

    public void addClient(ClientHandler clientHandler) {
        this.clients.add(clientHandler);
    }

    public void removeClient(ClientHandler clientHandler) {
        this.clients.remove(clientHandler);
    }

    @Override
    public void run() {
        while (running) {
            try {
                updateGame();
                broadcastState();
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                running = false;
                Thread.currentThread().interrupt();
                System.err.println("Game loop was interrupted.");
            }
        }
    }

    private void updateGame() throws InterruptedException {
        gameState.update();
    }

    private void broadcastState() {
        clients.removeIf(ClientHandler::isClosed);
        for (ClientHandler client : clients) {
            String playerId = client.getPlayerId();
            if (playerId == null)
                continue;

            String gameStateJson = GameStateSerializer.toJson(gameState, playerId);
            client.sendMessage(gameStateJson);
        }
    }

    public void broadcastMessage(String message) {
        clients.removeIf(ClientHandler::isClosed);
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public void stop() {
        running = false;
    }
}
