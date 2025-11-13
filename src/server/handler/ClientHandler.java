package server.handler;

import common.player.Player;
import common.skills.Skill;
import common.skills.Skill1;
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
    private PrintWriter out;
    private BufferedReader in;
    private final String playerId;
    private final SkillCreator skillCreator;

    public ClientHandler(Socket socket, GameState gameState) {
        this.clientSocket = socket;
        this.gameState = gameState;
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
            newPlayer.setY(500);
            gameState.addPlayer(newPlayer);
            System.out.println("Player " + this.playerId + " connected.");

            // Send a welcome message to the client with their new ID
            String welcomeMessage = String.format("{\"type\":\"WELCOME\",\"payload\":{\"id\":\"%s\"}}", this.playerId);
            sendMessage(welcomeMessage);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received from " + playerId + ": " + inputLine);
                if (inputLine.contains("\"type\":\"PLAYER_UPDATE\"")) {
                    handlePlayerUpdate(inputLine);
                } else if (inputLine.contains("\"type\":\"SKILL_USE\"")) {
                    handleSkillUse(inputLine);
                }
            }

        } catch (IOException e) {
            System.out.println("Player " + playerId + " disconnected.");
        } finally {
            gameState.removePlayer(this.playerId);
            closeResources();
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
        if (data == null) return;

        Player player = gameState.getPlayer(playerId);
        if (player != null) {
            String skillId = "skill_" + UUID.randomUUID().toString();
            Skill skill = skillCreator.createSkill(data.skillType, skillId, player);

            if (skill != null) {
                gameState.addSkill(skill);
            }
        }
    }


    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
        }
    }

    private void closeResources() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null) clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
