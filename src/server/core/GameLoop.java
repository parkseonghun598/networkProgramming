package server.core;

import common.monster.Monster;
import server.handler.ClientHandler;
import server.util.GameStateSerializer;

import java.util.Random;

public class GameLoop implements Runnable {

    private final GameState gameState;
    private final java.util.List<ClientHandler> clients;
    private volatile boolean running = true;

    public GameLoop(GameState gameState, java.util.List<ClientHandler> clients) {
        this.gameState = gameState;
        this.clients = clients;
    }

    @Override
    public void run() {


        while (running) {
            try {
                updateGame();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            // 클라이언트에게 메세지 전송
            broadcastState();

            try {
                Thread.sleep(16); // ~60 FPS
            } catch (InterruptedException e) {
                running = false;
                Thread.currentThread().interrupt();
                System.err.println("Game loop was interrupted.");
            }
        }
    }

    private void updateGame() throws InterruptedException {
        // 몬스터 자동 관리 (부족하면 생성)
        gameState.manageMonsters();

        // Simple monster movement logic
        for (Monster monster : gameState.getAllMonsters()) {
            int currentX = monster.getX();
            if (common.enums.Direction.RIGHT.equals(monster.getDirection())) {
                if (currentX > 300) {
                    monster.setDirection(common.enums.Direction.LEFT);
                    int dist = new Random().nextInt(10) + 1;
                    monster.setX(currentX - dist);
                } else {
                    monster.setX(currentX + 1);
                }
            } else { // LEFT
                if (currentX < 50) {
                    monster.setDirection(common.enums.Direction.RIGHT);
                    monster.setX(currentX + 1);
                } else {
                    monster.setX(currentX - 1);
                }
            }
        }

        // Update skills
        gameState.updateSkills();
    }

    private void broadcastState() {
        String gameStateJson = GameStateSerializer.toJson(
            gameState.getAllPlayers(),
            gameState.getAllMonsters(),
            gameState.getAllSkills()
        );
        for (ClientHandler client : clients) {
            client.sendMessage(gameStateJson);
        }
    }

    public void stop() {
        running = false;
    }
}
