package server;

import server.core.GameLoop;
import server.core.GameState;
import server.handler.ClientHandler;
import server.map.GameMap;
import server.map.MapCreator;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameServer {
    private static final int PORT = 12345;
    private static final ExecutorService clientPool = Executors.newCachedThreadPool();
    private static GameState gameState;

    public static void main(String[] args) {
        // Initialize maps
        Map<String, GameMap> maps = new HashMap<>();
        maps.put("hennesis", MapCreator.Hennessis());
        maps.put("bossMap", MapCreator.BossMap());

        // Initialize game state with all maps
        gameState = new GameState(maps);

        // Start the game loop in a separate thread
        GameLoop gameLoop = new GameLoop(gameState);
        new Thread(gameLoop).start();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Game server is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket, gameState, gameLoop);
                gameLoop.addClient(clientHandler);
                clientPool.execute(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
