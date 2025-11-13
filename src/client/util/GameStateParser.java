package client.util;

import common.monster.Monster;
import common.player.Player;
import common.skills.Skill;
import common.skills.Skill1;
import java.util.List;

public class GameStateParser {

    public static void parseAndUpdate(String jsonState, List<Player> players,
                                      List<Monster> monsters, List<Skill> skills) {
        try {
            if (jsonState.contains("\"players\":[")) {
                parsePlayers(jsonState, players);
            }

            if (jsonState.contains("\"monsters\":[")) {
                parseMonsters(jsonState, monsters);
            }

            if (jsonState.contains("\"skills\":[")) {
                parseSkills(jsonState, skills);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse game state: " + jsonState);
        }
    }

    private static void parsePlayers(String jsonState, List<Player> players) {
        players.clear();
        String playersJson = jsonState.split("\"players\":\\[")[1].split("\\]")[0];
        if (!playersJson.isEmpty()) {
            for (String playerStr : playersJson.split("\\},\\{")) {
                Player p = new Player();
                String id = playerStr.split("\"id\":\"")[1].split("\"")[0];
                int x = Integer.parseInt(playerStr.split("\"x\":")[1].split(",")[0]);
                int y = Integer.parseInt(playerStr.split("\"y\":")[1].split("}")[0]);
                p.setId(id);
                p.setX(x);
                p.setY(y);
                players.add(p);
            }
        }
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
                int y = Integer.parseInt(monsterStr.split("\"y\":")[1].split("}")[0]);
                m.setId(id);
                m.setName(name);
                m.setX(x);
                m.setY(y);
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
                    }

                    if (skill != null) {
                        skills.add(skill);
                    }
                } catch (Exception e) {
                    // Skip malformed skill
                }
            }
        }
    }
}