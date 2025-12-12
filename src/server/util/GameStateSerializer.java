package server.util;

import common.item.Item;
import common.monster.Monster;
import common.player.Player;
import common.skills.Skill;
import server.core.GameState;
import server.map.GameMap;

import java.util.List;

public class GameStateSerializer {

    public static String toJson(GameState gameState, String forPlayerId) {
        Player player = gameState.getPlayer(forPlayerId);
        if (player == null) {
            return "{}"; // Or some error/empty state
        }

        String mapId = player.getMapId();
        GameMap map = gameState.getMap(mapId);
        if (map == null) {
            return "{}"; // Should not happen if state is consistent
        }

        List<Player> playersInMap = gameState.getPlayersInMap(mapId);
        List<Monster> monstersInMap = gameState.getMonstersInMap(mapId);
        // Skills are currently global, could be filtered by mapId if needed
        List<Skill> skills = gameState.getAllSkills();
        List<Item> itemsInMap = gameState.getItemsInMap(mapId);

        StringBuilder sb = new StringBuilder();
        sb.append("{\"type\":\"GAME_STATE\",\"payload\":{");
        appendMapInfo(sb, map);
        appendPlayers(sb, playersInMap);
        appendMonsters(sb, monstersInMap);
        appendSkills(sb, skills);
        appendPortals(sb, map.getPortals());
        appendNpcs(sb, map.getNpcs());
        appendItems(sb, itemsInMap);
        sb.append("}}");
        return sb.toString();
    }

    private static void appendPortals(StringBuilder sb, List<common.map.Portal> portals) {
        sb.append(",\"portals\":[");
        for (common.map.Portal p : portals) {
            java.awt.Rectangle bounds = p.getBounds();
            sb.append(String.format("{\"x\":%d,\"y\":%d,\"width\":%d,\"height\":%d}",
                    (int) bounds.getX(), (int) bounds.getY(), (int) bounds.getWidth(), (int) bounds.getHeight()));
            sb.append(",");
        }
        if (!portals.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]");
    }

    private static void appendMapInfo(StringBuilder sb, GameMap map) {
        sb.append("\"map\":{");
        sb.append(String.format("\"backgroundImagePath\":\"%s\"", map.getBackgroundImagePath().replace("\\", "/")));
        sb.append("}");
    }

    private static void appendPlayers(StringBuilder sb, List<Player> players) {
        sb.append(",\"players\":[");
        for (Player p : players) {
            String directionStr = p.getDirection() != null ? p.getDirection().getValue() : "right";
            String username = p.getUsername() != null ? p.getUsername() : p.getId();
            String characterType = p.getCharacterType() != null ? p.getCharacterType() : "defaultWarrior";
            String state = p.getState() != null ? p.getState() : "idle";
            String equippedWeapon = p.getEquippedWeapon() != null ? p.getEquippedWeapon() : "none";
            String equippedHat = p.getEquippedHat() != null ? p.getEquippedHat() : "none";
            String equippedTop = p.getEquippedTop() != null ? p.getEquippedTop() : "none";
            String equippedBottom = p.getEquippedBottom() != null ? p.getEquippedBottom() : "none";
            String equippedGloves = p.getEquippedGloves() != null ? p.getEquippedGloves() : "none";
            String equippedShoes = p.getEquippedShoes() != null ? p.getEquippedShoes() : "none";
            
            sb.append(String.format("{\"id\":\"%s\",\"username\":\"%s\",\"x\":%d,\"y\":%d,\"direction\":\"%s\",\"mapId\":\"%s\",\"characterType\":\"%s\",\"state\":\"%s\",\"mesos\":%d,\"equippedWeapon\":\"%s\",\"equippedHat\":\"%s\",\"equippedTop\":\"%s\",\"equippedBottom\":\"%s\",\"equippedGloves\":\"%s\",\"equippedShoes\":\"%s\"",
                    p.getId(), username, p.getX(), p.getY(), directionStr, p.getMapId(), characterType, state, p.getMesos(), equippedWeapon, equippedHat, equippedTop, equippedBottom, equippedGloves, equippedShoes));
            
            // Append inventory
            sb.append(",\"inventory\":[");
            for (Item item : p.getInventory()) {
                sb.append(String.format("{\"id\":\"%s\",\"type\":\"%s\",\"name\":\"%s\",\"spritePath\":\"%s\"}",
                        item.getId(), item.getType(), item.getName(), item.getSpritePath().replace("\\", "/")));
                sb.append(",");
            }
            if (!p.getInventory().isEmpty()) {
                sb.deleteCharAt(sb.length() - 1);
            }
            sb.append("]}");
            
            sb.append(",");
        }
        if (!players.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]");
    }

    private static void appendMonsters(StringBuilder sb, List<Monster> monsters) {
        sb.append(",\"monsters\":[");
        for (Monster m : monsters) {
            sb.append(String.format(
                    "{\"id\":\"%s\",\"name\":\"%s\",\"type\":\"%s\",\"x\":%d,\"y\":%d,\"hp\":%d,\"maxHp\":%d}",
                    m.getId(), m.getName(), m.getType(), m.getX(), m.getY(), m.getHp(), m.getMaxHp()));
            sb.append(",");
        }
        if (!monsters.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]");
    }

    private static void appendSkills(StringBuilder sb, List<Skill> skills) {
        sb.append(",\"skills\":[");
        for (Skill s : skills) {
            String directionStr = s.getDirection().getValue();
            sb.append(String.format(
                    "{\"id\":\"%s\",\"playerId\":\"%s\",\"type\":\"%s\",\"x\":%d,\"y\":%d,\"direction\":\"%s\",\"active\":%b}",
                    s.getId(), s.getPlayerId(), s.getType(), s.getX(), s.getY(), directionStr, s.isActive()));
            sb.append(",");
        }
        if (!skills.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]");
    }

    private static void appendNpcs(StringBuilder sb, List<common.npc.NPC> npcs) {
        sb.append(",\"npcs\":[");
        for (common.npc.NPC npc : npcs) {
            sb.append(String.format(
                    "{\"id\":\"%s\",\"name\":\"%s\",\"x\":%d,\"y\":%d,\"spritePath\":\"%s\"}",
                    npc.getId(), npc.getName(), npc.getX(), npc.getY(), npc.getSpritePath().replace("\\", "/")));
            sb.append(",");
        }
        if (!npcs.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]");
    }

    private static void appendItems(StringBuilder sb, List<Item> items) {
        sb.append(",\"items\":[");
        for (Item item : items) {
            sb.append(String.format(
                    "{\"id\":\"%s\",\"type\":\"%s\",\"name\":\"%s\",\"x\":%d,\"y\":%d,\"spritePath\":\"%s\",\"value\":%d}",
                    item.getId(), item.getType(), item.getName(), item.getX(), item.getY(), item.getSpritePath().replace("\\", "/"), item.getValue()));
            sb.append(",");
        }
        if (!items.isEmpty()) {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append("]");
    }
}