package server.util;

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

        StringBuilder sb = new StringBuilder();
        sb.append("{\"type\":\"GAME_STATE\",\"payload\":{");
        appendMapInfo(sb, map);
        appendPlayers(sb, playersInMap);
        appendMonsters(sb, monstersInMap);
        appendSkills(sb, skills);
        appendPortals(sb, map.getPortals());
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
            sb.append(String.format("{\"id\":\"%s\",\"x\":%d,\"y\":%d,\"direction\":\"%s\",\"mapId\":\"%s\"}",
                    p.getId(), p.getX(), p.getY(), directionStr, p.getMapId()));
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
}