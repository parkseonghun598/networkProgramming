package client.util;

import common.item.Item;
import common.map.Portal;
import common.monster.Monster;
import common.npc.NPC;
import common.player.Player;
import common.skills.Skill;
import common.skills.Skill1;
import common.skills.Skill2;
import common.skills.Skill3;
import common.skills.Skill4;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GameStateParser {

    public static void parseAndUpdate(String jsonState, List<Player> players,
            List<Monster> monsters, List<Skill> skills, List<Portal> portals, List<NPC> npcs, List<Item> items, String myPlayerId) {
        // 각 파싱 메서드를 독립적으로 실행하여 하나가 실패해도 다른 것들은 계속 진행
        try {
            if (jsonState.contains("\"players\":[")) {
                parsePlayers(jsonState, players, myPlayerId);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse players: " + e.getMessage());
        }

        try {
            if (jsonState.contains("\"monsters\":[")) {
                parseMonsters(jsonState, monsters);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse monsters: " + e.getMessage());
        }

        try {
            if (jsonState.contains("\"skills\":[")) {
                parseSkills(jsonState, skills);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse skills: " + e.getMessage());
        }
        
        try {
            if (jsonState.contains("\"portals\":[")) {
                parsePortals(jsonState, portals);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse portals: " + e.getMessage());
        }
        
        try {
            if (jsonState.contains("\"npcs\":[")) {
                parseNpcs(jsonState, npcs);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse npcs: " + e.getMessage());
        }
        
        try {
            if (jsonState.contains("\"items\":[")) {
                parseItems(jsonState, items);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse items: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void parsePortals(String jsonState, List<Portal> portals) {
        portals.clear();
        String portalsJson = jsonState.split("\"portals\":\\[")[1].split("\\]")[0];
        if (!portalsJson.isEmpty()) {
            for (String portalStr : portalsJson.split("\\},\\{")) {
                try {
                    int x = Integer.parseInt(portalStr.split("\"x\":")[1].split(",")[0]);
                    int y = Integer.parseInt(portalStr.split("\"y\":")[1].split(",")[0]);
                    int width = Integer.parseInt(portalStr.split("\"width\":")[1].split(",")[0]);
                    int height = Integer.parseInt(portalStr.split("\"height\":")[1].split("}")[0]);
                    // The client only needs to know where to draw the portal, so target info is not
                    // needed.
                    portals.add(new Portal(x, y, width, height, null, 0, 0));
                } catch (Exception e) {
                    System.err.println("Failed to parse portal: " + portalStr);
                }
            }
        }
    }

    public static String parseBackgroundImagePath(String jsonState) {
        try {
            if (jsonState.contains("\"map\":{")) {
                String mapJson = jsonState.split("\"map\":\\{")[1].split("\\}")[0];
                if (mapJson.contains("\"backgroundImagePath\":\"")) {
                    return mapJson.split("\"backgroundImagePath\":\"")[1].split("\"")[0];
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to parse background image path: " + e.getMessage());
        }
        return null;
    }

    private static void parsePlayers(String jsonState, List<Player> players, String myPlayerId) {
        List<Player> receivedPlayers = new ArrayList<>();
        String playersJson = jsonState.split("\"players\":\\[")[1];
        
        // Find the closing bracket for players array (handle nested arrays)
        int bracketCount = 0;
        int endIndex = 0;
        for (int i = 0; i < playersJson.length(); i++) {
            char c = playersJson.charAt(i);
            if (c == '[') bracketCount++;
            else if (c == ']') {
                bracketCount--;
                if (bracketCount == -1) {
                    endIndex = i;
                    break;
                }
            }
        }
        playersJson = playersJson.substring(0, endIndex);
        
        if (playersJson.isEmpty()) {
            players.clear();
            return;
        }

        // Split players manually by tracking brace depth
        List<String> playerStrings = splitByTopLevelBraces(playersJson);
        
        for (String playerStr : playerStrings) {
            String id = playerStr.split("\"id\":\"")[1].split("\"")[0];
            String username = id; // default to id
            if (playerStr.contains("\"username\":\"")) {
                username = playerStr.split("\"username\":\"")[1].split("\"")[0];
            }
            int x = Integer.parseInt(playerStr.split("\"x\":")[1].split(",")[0]);
            int y = Integer.parseInt(playerStr.split("\"y\":")[1].split(",")[0]);
            String directionStr = "right"; // default
            if (playerStr.contains("\"direction\":\"")) {
                directionStr = playerStr.split("\"direction\":\"")[1].split("\"")[0];
            }
            String mapId = "hennesis"; // default
            if (playerStr.contains("\"mapId\":\"")) {
                mapId = playerStr.split("\"mapId\":\"")[1].split("\"")[0];
            }
            String characterType = "defaultWarrior"; // default
            if (playerStr.contains("\"characterType\":\"")) {
                characterType = playerStr.split("\"characterType\":\"")[1].split("\"")[0];
            }
            String state = "idle"; // default
            if (playerStr.contains("\"state\":\"")) {
                state = playerStr.split("\"state\":\"")[1].split("\"")[0];
            }
            int mesos = 0; // default
            if (playerStr.contains("\"mesos\":")) {
                try {
                    mesos = Integer.parseInt(playerStr.split("\"mesos\":")[1].split(",")[0]);
                } catch (Exception e) {
                    // Keep default
                }
            }
            int level = 1; // default
            if (playerStr.contains("\"level\":")) {
                try {
                    level = Integer.parseInt(playerStr.split("\"level\":")[1].split(",")[0]);
                } catch (Exception e) {
                    // Keep default
                }
            }
            int xp = 0; // default
            if (playerStr.contains("\"xp\":")) {
                try {
                    xp = Integer.parseInt(playerStr.split("\"xp\":")[1].split(",")[0]);
                } catch (Exception e) {
                    // Keep default
                }
            }
            int maxXp = 100; // default
            if (playerStr.contains("\"maxXp\":")) {
                try {
                    maxXp = Integer.parseInt(playerStr.split("\"maxXp\":")[1].split(",")[0]);
                } catch (Exception e) {
                    // Keep default
                }
            }
            
            // Parse equipped items
            String equippedWeapon = "none"; // default
            if (playerStr.contains("\"equippedWeapon\":\"")) {
                equippedWeapon = playerStr.split("\"equippedWeapon\":\"")[1].split("\"")[0];
            }
            String equippedHat = "none"; // default
            if (playerStr.contains("\"equippedHat\":\"")) {
                equippedHat = playerStr.split("\"equippedHat\":\"")[1].split("\"")[0];
            }
            String equippedTop = "none"; // default
            if (playerStr.contains("\"equippedTop\":\"")) {
                equippedTop = playerStr.split("\"equippedTop\":\"")[1].split("\"")[0];
            }
            String equippedBottom = "none"; // default
            if (playerStr.contains("\"equippedBottom\":\"")) {
                equippedBottom = playerStr.split("\"equippedBottom\":\"")[1].split("\"")[0];
            }
            String equippedGloves = "none"; // default
            if (playerStr.contains("\"equippedGloves\":\"")) {
                equippedGloves = playerStr.split("\"equippedGloves\":\"")[1].split("\"")[0];
            }
            String equippedShoes = "none"; // default
            if (playerStr.contains("\"equippedShoes\":\"")) {
                equippedShoes = playerStr.split("\"equippedShoes\":\"")[1].split("\"")[0];
            }

            Optional<Player> existingPlayerOpt = players.stream().filter(p -> p.getId().equals(id)).findFirst();
            Player player;
            if (existingPlayerOpt.isPresent()) {
                player = existingPlayerOpt.get();
            } else {
                player = new Player();
                player.setId(id);
                players.add(player);
            }

            player.setUsername(username);
            player.setX(x);
            player.setY(y);
            player.setMapId(mapId);
            player.setCharacterType(characterType);
            player.setMesos(mesos);
            player.setLevel(level);
            player.setXp(xp);
            player.setMaxXp(maxXp);
            
            // Set equipped items
            player.setEquippedWeapon(equippedWeapon);
            player.setEquippedHat(equippedHat);
            player.setEquippedTop(equippedTop);
            player.setEquippedBottom(equippedBottom);
            player.setEquippedGloves(equippedGloves);
            player.setEquippedShoes(equippedShoes);

            // Parse inventory
            if (playerStr.contains("\"inventory\":[")) {
                try {
                    String inventoryJson = playerStr.split("\"inventory\":\\[")[1].split("\\]")[0];
                    player.getInventory().clear();
                    if (!inventoryJson.isEmpty() && !inventoryJson.equals("")) {
                        for (String itemStr : inventoryJson.split("\\},\\{")) {
                            try {
                                String itemId = itemStr.split("\"id\":\"")[1].split("\"")[0];
                                String itemType = itemStr.split("\"type\":\"")[1].split("\"")[0];
                                String itemName = itemStr.split("\"name\":\"")[1].split("\"")[0];
                                String itemSpritePath = itemStr.split("\"spritePath\":\"")[1].split("\"")[0];
                                
                                Item item = new Item(itemId, itemType, itemName, 0, 0, itemSpritePath);
                                player.addItemToInventory(item);
                            } catch (Exception e) {
                                // Skip malformed item
                            }
                        }
                    }
                } catch (Exception e) {
                    // No inventory or malformed
                }
            }

            // Only update direction and state for other players, not myPlayer
            if (!id.equals(myPlayerId)) {
                player.setDirection(common.enums.Direction.fromString(directionStr));
                player.setState(state);
            }
            receivedPlayers.add(player);
        }

        players.retainAll(receivedPlayers);
    }

    /**
     * Split JSON array elements by top-level commas (ignoring nested arrays/objects)
     */
    private static List<String> splitByTopLevelBraces(String json) {
        List<String> result = new ArrayList<>();
        int braceDepth = 0;
        int bracketDepth = 0;
        int startIndex = 0;
        
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{') braceDepth++;
            else if (c == '}') braceDepth--;
            else if (c == '[') bracketDepth++;
            else if (c == ']') bracketDepth--;
            else if (c == ',' && braceDepth == 0 && bracketDepth == 0) {
                String element = json.substring(startIndex, i).trim();
                if (element.startsWith("{")) element = element.substring(1);
                if (element.endsWith("}")) element = element.substring(0, element.length() - 1);
                result.add(element);
                startIndex = i + 1;
            }
        }
        
        // Add last element
        String lastElement = json.substring(startIndex).trim();
        if (lastElement.startsWith("{")) lastElement = lastElement.substring(1);
        if (lastElement.endsWith("}")) lastElement = lastElement.substring(0, lastElement.length() - 1);
        if (!lastElement.isEmpty()) {
            result.add(lastElement);
        }
        
        return result;
    }

    private static void parseMonsters(String jsonState, List<Monster> monsters) {
        monsters.clear();
        String monstersJson = jsonState.split("\"monsters\":\\[")[1].split("\\]")[0];
        if (!monstersJson.isEmpty()) {
            for (String monsterStr : monstersJson.split("\\},\\{")) {
                Monster m = new Monster();
                String id = monsterStr.split("\"id\":\"")[1].split("\"")[0];
                String name = monsterStr.split("\"name\":\"")[1].split("\"")[0];
                int x = Integer.parseInt(monsterStr.split("\"x\":")[1].split(",")[0]);
                int y = Integer.parseInt(monsterStr.split("\"y\":")[1].split(",")[0]);
                int hp = 0;
                int maxHp = 0;
                try {
                    if (monsterStr.contains("\"hp\":")) {
                        hp = Integer.parseInt(monsterStr.split("\"hp\":")[1].split(",")[0]);
                    }
                    if (monsterStr.contains("\"maxHp\":")) {
                        String maxHpStr = monsterStr.split("\"maxHp\":")[1];
                        if (maxHpStr.contains("}")) {
                            maxHpStr = maxHpStr.split("}")[0];
                        } else if (maxHpStr.contains(",")) {
                            maxHpStr = maxHpStr.split(",")[0];
                        }
                        maxHp = Integer.parseInt(maxHpStr);
                    }
                } catch (Exception e) {
                    System.err.println("Error parsing monster HP: " + e.getMessage());
                }

                m.setId(id);
                m.setName(name);
                m.setX(x);
                m.setY(y);
                m.setHp(hp);
                m.setMaxHp(maxHp);
                monsters.add(m);
            }
        }
    }

    private static void parseSkills(String jsonState, List<Skill> skills) {
        skills.clear();
        String skillsJson = jsonState.split("\"skills\":\\[")[1].split("\\]")[0];
        if (!skillsJson.isEmpty()) {
            for (String skillStr : skillsJson.split("\\},\\{")) {
                try {
                    String id = skillStr.split("\"id\":\"")[1].split("\"")[0];
                    String playerId = skillStr.split("\"playerId\":\"")[1].split("\"")[0];
                    String type = skillStr.split("\"type\":\"")[1].split("\"")[0];
                    int x = Integer.parseInt(skillStr.split("\"x\":")[1].split(",")[0]);
                    int y = Integer.parseInt(skillStr.split("\"y\":")[1].split(",")[0]);
                    String direction = skillStr.split("\"direction\":\"")[1].split("\"")[0];

                    Skill skill = null;
                    if ("skill1".equals(type)) {
                        skill = new Skill1(id, playerId, x, y,
                                common.enums.Direction.fromString(direction));
                    } else if ("skill2".equals(type)) {
                        skill = new Skill2(id, playerId, x, y,
                                common.enums.Direction.fromString(direction));
                    } else if ("skill3".equals(type)) {
                        skill = new Skill3(id, playerId, x, y,
                                common.enums.Direction.fromString(direction));
                    } else if ("skill4".equals(type)) {
                        skill = new Skill4(id, playerId, x, y,
                                common.enums.Direction.fromString(direction));
                    }

                    if (skill != null) {
                        skills.add(skill);
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    private static void parseNpcs(String jsonState, List<NPC> npcs) {
        npcs.clear();
        String npcsJson = jsonState.split("\"npcs\":\\[")[1].split("\\]")[0];
        if (!npcsJson.isEmpty()) {
            for (String npcStr : npcsJson.split("\\},\\{")) {
                try {
                    String id = npcStr.split("\"id\":\"")[1].split("\"")[0];
                    String name = npcStr.split("\"name\":\"")[1].split("\"")[0];
                    int x = Integer.parseInt(npcStr.split("\"x\":")[1].split(",")[0]);
                    int y = Integer.parseInt(npcStr.split("\"y\":")[1].split(",")[0]);
                    String spritePath = npcStr.split("\"spritePath\":\"")[1].split("\"")[0];

                    NPC npc = new NPC(id, name, x, y, spritePath);
                    npcs.add(npc);
                } catch (Exception e) {
                    System.err.println("Failed to parse NPC: " + npcStr);
                }
            }
        }
    }

    private static void parseItems(String jsonState, List<Item> items) {
        items.clear();
        try {
            if (!jsonState.contains("\"items\":[")) {
                return;
            }
            
            String itemsJson = jsonState.split("\"items\":\\[")[1].split("\\]")[0];
            if (itemsJson.isEmpty() || itemsJson.trim().isEmpty()) {
                return;
            }
            
            // 첫 번째 아이템 앞의 { 제거, 마지막 아이템 뒤의 } 제거
            itemsJson = itemsJson.trim();
            if (itemsJson.startsWith("{")) {
                itemsJson = itemsJson.substring(1);
            }
            if (itemsJson.endsWith("}")) {
                itemsJson = itemsJson.substring(0, itemsJson.length() - 1);
            }
            
            // 아이템들을 분리 (},{ 로 분리)
            String[] itemStrings = itemsJson.split("\\},\\{");
            
            for (String itemStr : itemStrings) {
                try {
                    // 각 아이템 앞뒤의 { } 제거
                    itemStr = itemStr.trim();
                    if (itemStr.startsWith("{")) {
                        itemStr = itemStr.substring(1);
                    }
                    if (itemStr.endsWith("}")) {
                        itemStr = itemStr.substring(0, itemStr.length() - 1);
                    }
                    
                    String id = extractField(itemStr, "id");
                    String type = extractField(itemStr, "type");
                    String name = extractField(itemStr, "name");
                    int x = extractIntField(itemStr, "x");
                    int y = extractIntField(itemStr, "y");
                    String spritePath = extractField(itemStr, "spritePath");
                    
                    // value 필드 파싱 (코인 아이템의 경우, 선택적)
                    int value = 0;
                    if (itemStr.contains("\"value\":")) {
                        value = extractIntField(itemStr, "value");
                    }

                    Item item = new Item(id, type, name, x, y, spritePath, value);
                    items.add(item);
                } catch (Exception e) {
                    System.err.println("Failed to parse Item: " + itemStr + " - " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to parse items array: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static String extractField(String jsonStr, String fieldName) {
        try {
            String pattern = "\"" + fieldName + "\":\"";
            if (jsonStr.contains(pattern)) {
                String afterPattern = jsonStr.split(pattern, 2)[1];
                return afterPattern.split("\"")[0];
            }
        } catch (Exception e) {
            System.err.println("Failed to extract field " + fieldName + " from: " + jsonStr);
        }
        return "";
    }
    
    private static int extractIntField(String jsonStr, String fieldName) {
        try {
            String pattern = "\"" + fieldName + "\":";
            if (jsonStr.contains(pattern)) {
                String afterPattern = jsonStr.split(pattern, 2)[1];
                String valueStr = afterPattern.split(",")[0].split("}")[0].trim();
                return Integer.parseInt(valueStr);
            }
        } catch (Exception e) {
            System.err.println("Failed to extract int field " + fieldName + " from: " + jsonStr);
        }
        return 0;
    }
}