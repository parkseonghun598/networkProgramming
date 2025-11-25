package server.handler;

import common.enums.Direction;
import common.item.Item;
import common.player.Player;
import common.skills.Skill;
import server.core.GameState;
import server.util.MessageParser;
import common.ImagePath;

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
            newPlayer.setMapId("warriorRoom"); // 첫 접속은 워리어 룸
            newPlayer.setX(400); // 워리어 룸 중앙
            newPlayer.setY(450);
            gameState.addPlayer(newPlayer);
            System.out.println("Player " + this.playerId + " connected to warriorRoom.");

            // Send a welcome message to the client with their new ID
            String welcomeMessage = String.format("{\"type\":\"WELCOME\",\"payload\":{\"id\":\"%s\"}}", this.playerId);
            sendMessage(welcomeMessage);

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (inputLine.contains("\"type\":\"USER_INFO\"")) {
                    handleUserInfo(inputLine);
                } else if (inputLine.contains("\"type\":\"PLAYER_UPDATE\"")) {
                    handlePlayerUpdate(inputLine);
                } else if (inputLine.contains("\"type\":\"SKILL_USE\"")) {
                    handleSkillUse(inputLine);
                    System.out.println(playerId + ": " + inputLine);
                } else if (inputLine.contains("\"type\":\"USE_PORTAL\"")) {
                    handlePortalUse();
                } else if (inputLine.contains("\"type\":\"CHAT\"")) {
                    handleChat(inputLine);
                } else if (inputLine.contains("\"type\":\"REQUEST_ITEM_DROP\"")) {
                    handleItemDropRequest(inputLine);
                } else if (inputLine.contains("\"type\":\"PICKUP_ITEM\"")) {
                    handleItemPickup(inputLine);
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
            // Player dimensions: 60x60
            if (portal.isPlayerInside(player.getX(), player.getY(), 60, 60)) {
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

    private void handleUserInfo(String message) {
        try {
            String username = message.split("\"username\":\"")[1].split("\"")[0];
            String characterType = "defaultWarrior"; // default
            if (message.contains("\"characterType\":\"")) {
                characterType = message.split("\"characterType\":\"")[1].split("\"")[0];
            }
            Player player = gameState.getPlayer(playerId);
            if (player != null) {
                player.setUsername(username);
                player.setCharacterType(characterType);
                System.out.println("Player " + playerId + " set username: " + username + ", character: " + characterType);
            }
        } catch (Exception e) {
            System.err.println("Failed to handle user info: " + message);
        }
    }

    private void handleChat(String message) {
        try {
            String content = message.split("\"message\":\"")[1].split("\"")[0];
            String time = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));

            Player player = gameState.getPlayer(playerId);
            String username = player != null && player.getUsername() != null ? player.getUsername() : playerId;

            String formattedMessage = String.format("[%s]%s:%s", time, username, content);

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

    private void handleItemDropRequest(String message) {
        try {
            String itemType = message.split("\"itemType\":\"")[1].split("\"")[0];
            int x = Integer.parseInt(message.split("\"x\":")[1].split(",")[0]);
            int y = Integer.parseInt(message.split("\"y\":")[1].split("}")[0]);

            Player player = gameState.getPlayer(playerId);
            if (player == null) return;

            String mapId = player.getMapId();
            String itemId = "item_" + UUID.randomUUID().toString();

            String spritePath = "";
            String itemName = "";
            if ("defaultWeapon".equals(itemType)) {
                spritePath = ImagePath.DEFAULT_WEAPON_IMAGE_PATH;
                itemName = "초보자 무기";
            }

            Item item = new Item(itemId, itemType, itemName, x, y, spritePath);
            gameState.addItem(mapId, item);

            System.out.println("Player " + playerId + " dropped item " + itemType + " at (" + x + ", " + y + ")");
        } catch (Exception e) {
            System.err.println("Failed to handle item drop request: " + message);
            e.printStackTrace();
        }
    }

    private void handleItemPickup(String message) {
        try {
            String itemId = message.split("\"itemId\":\"")[1].split("\"")[0];

            Player player = gameState.getPlayer(playerId);
            if (player == null) return;

            String mapId = player.getMapId();
            
            // 맵에서 아이템 찾기
            Item item = null;
            for (Item mapItem : gameState.getItemsInMap(mapId)) {
                if (mapItem.getId().equals(itemId)) {
                    item = mapItem;
                    break;
                }
            }

            if (item != null) {
                // 플레이어 인벤토리에 추가
                player.addItemToInventory(item);
                
                // 맵에서 제거
                gameState.removeItem(mapId, itemId);
                
                // 클라이언트에게 아이템 추가 알림
                String itemAddedMsg = String.format(
                    "{\"type\":\"ITEM_ADDED\",\"payload\":{\"id\":\"%s\",\"type\":\"%s\",\"name\":\"%s\",\"spritePath\":\"%s\"}}",
                    item.getId(), item.getType(), item.getName(), item.getSpritePath().replace("\\", "/")
                );
                sendMessage(itemAddedMsg);
                
                System.out.println("Player " + playerId + " picked up item " + item.getType());
            }
        } catch (Exception e) {
            System.err.println("Failed to handle item pickup: " + message);
            e.printStackTrace();
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
