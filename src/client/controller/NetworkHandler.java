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

                    // Send username and character type to server
                    String username = gamePanel.getUsername();
                    String characterType = gamePanel.getCharacterType();
                    if (username != null) {
                        String userInfoMsg = String.format("{\"type\":\"USER_INFO\",\"payload\":{\"username\":\"%s\",\"characterType\":\"%s\"}}", 
                            username, characterType != null ? characterType : "defaultWarrior");
                        sendMessage(userInfoMsg);
                    }
                } else if (serverMessage.contains("\"type\":\"GAME_STATE\"")) {
                    gamePanel.updateGameState(serverMessage);
                    gamePanel.repaint();
                } else if (serverMessage.contains("\"type\":\"CHAT\"")) {
                    String message = serverMessage.split("\"message\":\"")[1].split("\"")[0];
                    gamePanel.addChatMessage(message);
                } else if (serverMessage.contains("\"type\":\"ITEM_ADDED\"")) {
                    handleItemAdded(serverMessage);
                } else if (serverMessage.contains("\"type\":\"MESOS_UPDATE\"")) {
                    handleMesosUpdate(serverMessage);
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

    private void handleItemAdded(String message) {
        try {
            String id = message.split("\"id\":\"")[1].split("\"")[0];
            String type = message.split("\"type\":\"")[1].split("\"")[0];
            String name = message.split("\"name\":\"")[1].split("\"")[0];
            String spritePath = message.split("\"spritePath\":\"")[1].split("\"")[0];
            
            common.item.Item item = new common.item.Item(id, type, name, 0, 0, spritePath);
            gamePanel.addItemToInventory(item);
        } catch (Exception e) {
            System.err.println("Failed to parse ITEM_ADDED message: " + message);
            e.printStackTrace();
        }
    }
    
    private void handleMesosUpdate(String message) {
        try {
            String mesosStr = message.split("\"mesos\":")[1].split(",")[0];
            int mesos = Integer.parseInt(mesosStr.trim());
            gamePanel.updateMesos(mesos);
            
            // 획득한 메소 양과 위치 파싱
            if (message.contains("\"gained\":")) {
                String gainedStr = message.split("\"gained\":")[1].split(",")[0];
                int gained = Integer.parseInt(gainedStr.trim());
                
                // 위치 파싱 (있으면 사용, 없으면 플레이어 위치 사용)
                int x = 0;
                int y = 0;
                if (message.contains("\"x\":")) {
                    String xStr = message.split("\"x\":")[1].split(",")[0];
                    x = Integer.parseInt(xStr.trim());
                }
                if (message.contains("\"y\":")) {
                    String yStr = message.split("\"y\":")[1].split("}")[0];
                    y = Integer.parseInt(yStr.trim());
                }
                
                // 메소 획득 메시지 추가
                gamePanel.addMesosGainMessage(gained, x, y);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse MESOS_UPDATE message: " + message);
            e.printStackTrace();
        }
    }
}
