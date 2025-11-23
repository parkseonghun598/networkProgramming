package client.controller;

import client.view.GamePanel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NetworkHandler implements Runnable {

    private final GamePanel gamePanel;
    private final String host;
    private final int port;
    private PrintWriter out;

    public NetworkHandler(GamePanel gamePanel, String host, int port) {
        this.gamePanel = gamePanel;
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(host, port);
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            this.out = writer;
            System.out.println("Connected to the game server.");

            String serverMessage;
            while ((serverMessage = reader.readLine()) != null) {
                if (serverMessage.contains("\"type\":\"WELCOME\"")) {
                    // Manually parse WELCOME message to get player ID
                    String id = serverMessage.split("\"id\":\"")[1].split("\"}")[0];
                    gamePanel.setMyPlayerId(id);

                    // Send username to server
                    String username = gamePanel.getUsername();
                    if (username != null) {
                        String userInfoMsg = String.format("{\"type\":\"USER_INFO\",\"payload\":{\"username\":\"%s\"}}", username);
                        sendMessage(userInfoMsg);
                    }
                } else if (serverMessage.contains("\"type\":\"GAME_STATE\"")) {
                    gamePanel.updateGameState(serverMessage);
                    gamePanel.repaint();
                } else if (serverMessage.contains("\"type\":\"CHAT\"")) {
                    String message = serverMessage.split("\"message\":\"")[1].split("\"")[0];
                    gamePanel.addChatMessage(message);
                }
            }
        } catch (IOException e) {
            System.err.println("Connection to server lost: " + e.getMessage());
            gamePanel.showError("Connection to server lost.");
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
            // System.out.println("Sent to server: " + message); // Optional: for debugging
        }
    }
}
