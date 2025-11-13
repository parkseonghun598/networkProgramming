package server.util;

import common.monster.Monster;
import common.player.Player;
import common.skills.Skill;
import java.util.List;

public class GameStateSerializer {

    public static String toJson(List<Player> players, List<Monster> monsters, List<Skill> skills) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"type\":\"GAME_STATE\",\"payload\":{");
        appendPlayers(sb, players);
        appendMonsters(sb, monsters);
        appendSkills(sb, skills);
        sb.append("}}");
        return sb.toString();
    }

    private static void appendPlayers(StringBuilder sb, List<Player> players) {
        sb.append("\"players\":[");
        for (Player p : players) {
            sb.append(String.format("{\"id\":\"%s\",\"x\":%d,\"y\":%d}",
                p.getId(), p.getX(), p.getY()));
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
            sb.append(String.format("{\"id\":\"%s\",\"name\":\"%s\",\"type\":\"%s\",\"x\":%d,\"y\":%d}",
                m.getId(), m.getName(), m.getType(), m.getX(), m.getY()));
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
            String directionStr = s.getDirection() != null ? s.getDirection().getValue() : "right";
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