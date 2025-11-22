package server.core;

import common.monster.Monster;
import common.player.Player;
import common.skills.Skill;
import server.map.GameMap;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class GameState {

    private final Map<String, Player> players;
    private final List<Skill> skills;
    private final Map<String, GameMap> maps;

    public GameState(Map<String, GameMap> maps) {
        this.maps = maps;
        this.players = new ConcurrentHashMap<>();
        this.skills = new CopyOnWriteArrayList<>();
    }

    public void update() throws InterruptedException {
        for (GameMap map : maps.values()) {
            map.update();
        }
        updateSkills();
        checkCollisions();
    }

    private void checkCollisions() {
        for (Skill skill : skills) {
            if (!skill.isActive())
                continue;

            // Find which map the skill is in (based on player who cast it)
            Player player = players.get(skill.getPlayerId());
            if (player == null)
                continue;

            GameMap map = maps.get(player.getMapId());
            if (map == null)
                continue;

            for (Monster monster : map.getMonsters()) {
                if (isColliding(skill, monster)) {
                    monster.takeDamage(skill.getDamage());
                    skill.deactivate();
                    System.out.println("Monster " + monster.getId() + " took " + skill.getDamage() + " damage. HP: "
                            + monster.getHp());

                    if (monster.getHp() <= 0) {
                        map.getMonsters().remove(monster);
                        System.out.println("Monster " + monster.getId() + " died.");
                    }
                    break; // Skill hits one monster and disappears
                }
            }
        }
    }

    private boolean isColliding(Skill skill, Monster monster) {
        return skill.getX() < monster.getX() + 50 && // Monster width 50
                skill.getX() + skill.getWidth() > monster.getX() &&
                skill.getY() < monster.getY() + 50 && // Monster height 50
                skill.getY() + skill.getHeight() > monster.getY();
    }

    public void addPlayer(Player player) {
        players.put(player.getId(), player);
    }

    public void removePlayer(String playerId) {
        players.remove(playerId);
    }

    public Player getPlayer(String playerId) {
        return players.get(playerId);
    }

    public void updatePlayer(String playerId, common.dto.PlayerUpdateDTO dto) {
        Player player = players.get(playerId);
        if (player != null) {
            player.setX(dto.getX());
            player.setY(dto.getY());
            player.setState(dto.getState());
            player.setDirection(dto.getDirection());
        }
    }

    public List<Player> getAllPlayers() {
        return new CopyOnWriteArrayList<>(players.values());
    }

    public List<Player> getPlayersInMap(String mapId) {
        return players.values().stream()
                .filter(p -> mapId.equals(p.getMapId()))
                .collect(Collectors.toList());
    }

    public List<Monster> getMonstersInMap(String mapId) {
        GameMap map = maps.get(mapId);
        return (map != null) ? map.getMonsters() : new CopyOnWriteArrayList<>();
    }

    public void addSkill(Skill skill) {
        skills.add(skill);
    }

    public void removeSkill(String skillId) {
        skills.removeIf(s -> s.getId().equals(skillId));
    }

    public List<Skill> getAllSkills() {
        return this.skills;
    }

    public void updateSkills() throws InterruptedException {
        for (Skill skill : skills) {
            skill.update();
        }
        skills.removeIf(s -> !s.isActive());
    }

    public GameMap getMap(String mapId) {
        return maps.get(mapId);
    }

    public Map<String, GameMap> getMaps() {
        return maps;
    }
}
