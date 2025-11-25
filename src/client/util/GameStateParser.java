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
        try {
            if (jsonState.contains("\"players\":[")) {
                parsePlayers(jsonState, players, myPlayerId);
            }

            if (jsonState.contains("\"monsters\":[")) {
                parseMonsters(jsonState, monsters);
            }

            if (jsonState.contains("\"skills\":[")) {
                parseSkills(jsonState, skills);
            }
            if (jsonState.contains("\"portals\":[")) {
                parsePortals(jsonState, portals);
            }
            if (jsonState.contains("\"npcs\":[")) {
                parseNpcs(jsonState, npcs);
            }
            if (jsonState.contains("\"items\":[")) {
                parseItems(jsonState, items);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse game state: " + jsonState);
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
        String playersJson = jsonState.split("\"players\":\\[")[1].split("\\]")[0];
        if (playersJson.isEmpty()) {
            players.clear();
            return;
        }

        for (String playerStr : playersJson.split("\\},\\{")) {
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
        String itemsJson = jsonState.split("\"items\":\\[")[1].split("\\]")[0];
        if (!itemsJson.isEmpty()) {
            for (String itemStr : itemsJson.split("\\},\\{")) {
                try {
                    String id = itemStr.split("\"id\":\"")[1].split("\"")[0];
                    String type = itemStr.split("\"type\":\"")[1].split("\"")[0];
                    String name = itemStr.split("\"name\":\"")[1].split("\"")[0];
                    int x = Integer.parseInt(itemStr.split("\"x\":")[1].split(",")[0]);
                    int y = Integer.parseInt(itemStr.split("\"y\":")[1].split(",")[0]);
                    String spritePath = itemStr.split("\"spritePath\":\"")[1].split("\"")[0];

                    Item item = new Item(id, type, name, x, y, spritePath);
                    items.add(item);
                } catch (Exception e) {
                    System.err.println("Failed to parse Item: " + itemStr);
                }
            }
        }
    }
}