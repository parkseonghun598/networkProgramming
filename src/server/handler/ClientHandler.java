package server.handler;

import common.enums.Direction;
import common.player.Player;
import common.skills.Skill;
import server.core.GameState;
import server.util.MessageParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.UUID;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final GameState gameState;
    private final server.core.GameLoop gameLoop;
    private PrintWriter out;
    private BufferedReader in;
    private final String playerId;
    private final SkillCreator skillCreator;

    public ClientHandler(Socket socket, GameState gameState, server.core.GameLoop gameLoop) {
        this.clientSocket = socket;
        this.gameState = gameState;
        this.gameLoop = gameLoop;
        this.playerId = "player_" + Integer.toHexString(hashCode());
        this.skillCreator = new SkillCreator();
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            Player newPlayer = new Player();
            newPlayer.setId(this.playerId);
            newPlayer.setX(50);
            newPlayer.setY(475);
            gameState.addPlayer(newPlayer);
            System.out.println("Player " + this.playerId + " connected.");

            // Send a welcome message to the client with their new ID
            String welcomeMessage = String.format("{\"type\":\"WELCOME\",\"payload\":{\"id\":\"%s\"}}", this.playerId);
            sendMessage(welcomeMessage);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.contains("\"type\":\"PLAYER_UPDATE\"")) {
                    handlePlayerUpdate(inputLine);
                } else if (inputLine.contains("\"type\":\"SKILL_USE\"")) {
                    handleSkillUse(inputLine);
                    System.out.println(playerId + ": " + inputLine);
                } else if (inputLine.contains("\"type\":\"USE_PORTAL\"")) {
                    handlePortalUse();
                } else if (inputLine.contains("\"type\":\"CHAT\"")) {
                    handleChat(inputLine);
                }
            }

        } catch (IOException e) {
            System.out.println("Player " + playerId + " disconnected.");
        } finally {
            gameState.removePlayer(this.playerId);
            closeResources();
        }
    }

    public String getPlayerId() {
        return playerId;
    }

    public boolean isClosed() {
        return clientSocket.isClosed();
    }

    private void handlePortalUse() {
        Player player = gameState.getPlayer(playerId);
        if (player == null)
            return;

        server.map.GameMap currentMap = gameState.getMap(player.getMapId());
        if (currentMap == null)
            return;

        for (common.map.Portal portal : currentMap.getPortals()) {
            // Player dimensions are hardcoded as 100x100 in the renderer
            if (portal.isPlayerInside(player.getX(), player.getY(), 100, 100)) {
                player.setMapId(portal.getTargetMapId());
                player.setX(portal.getTargetX());
                player.setY(portal.getTargetY());
                System.out.println("Player " + playerId + " used portal to " + portal.getTargetMapId());
                break; // Assume one portal at a time
            }
        }
    }

    private void handlePlayerUpdate(String message) {
        MessageParser.PlayerUpdateData data = MessageParser.parsePlayerUpdate(message);
        if (data != null) {
            common.dto.PlayerUpdateDTO dto = new common.dto.PlayerUpdateDTO();
            dto.setX(data.x);
            dto.setY(data.y);
            dto.setState(data.state);
            dto.setDirection(data.direction);
            gameState.updatePlayer(playerId, dto);
        }
    }

    private void handleSkillUse(String message) {
        MessageParser.SkillUseData data = MessageParser.parseSkillUse(message);
        if (data == null)
            return;

        // Cooldown check is primarily client-side for UI, but good to have server-side
        // too if needed.
        // For now, we trust the client's request but we could add a map of lastUsedTime
        // here.

        Player player = gameState.getPlayer(playerId);
        if (player != null) {
            String skillId = "skill_" + UUID.randomUUID().toString();
            Direction direction = Direction.fromString(data.direction);
            Skill skill = skillCreator.createSkill(data.skillType, skillId, player, direction);

            if (skill != null) {
                gameState.addSkill(skill);
            }
        }
    }

    private void handleChat(String message) {
        try {
            String content = message.split("\"message\":\"")[1].split("\"")[0];
            String time = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
            String formattedMessage = String.format("[%s] %s", time, content);

            String broadcastJson = String.format("{\"type\":\"CHAT\",\"payload\":{\"message\":\"%s\"}}",
                    formattedMessage);
            gameLoop.broadcastMessage(broadcastJson);
        } catch (Exception e) {
            System.err.println("Failed to handle chat: " + message);
        }
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    private void closeResources() {
        try {
            if (in != null)
                in.close();
            if (out != null)
                out.close();
            if (clientSocket != null)
                clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
