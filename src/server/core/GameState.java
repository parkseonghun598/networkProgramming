package server.core;

import common.item.Item;
import common.monster.Monster;
import common.player.Player;
import common.skills.Skill;
import server.map.GameMap;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class GameState {

    private final Map<String, Player> players;
    private final List<Skill> skills;
    private final Map<String, GameMap> maps;
    private final Map<String, List<Item>> mapItems; // 맵별 아이템 관리

    public GameState(Map<String, GameMap> maps) {
        this.maps = maps;
        this.players = new ConcurrentHashMap<>();
        this.skills = new CopyOnWriteArrayList<>();
        this.mapItems = new ConcurrentHashMap<>();
        
        // 각 맵의 아이템 리스트 초기화
        for (String mapId : maps.keySet()) {
            mapItems.put(mapId, new CopyOnWriteArrayList<>());
        }
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
                        // 몬스터 처치 시 아이템 드롭
                        dropItemsOnMonsterDeath(player.getMapId(), monster.getX(), monster.getY());
                        
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

    public void addItem(String mapId, Item item) {
        List<Item> items = mapItems.get(mapId);
        if (items != null) {
            items.add(item);
            System.out.println("Added item " + item.getType() + " to map " + mapId);
        }
    }

    public void removeItem(String mapId, String itemId) {
        List<Item> items = mapItems.get(mapId);
        if (items != null) {
            items.removeIf(item -> item.getId().equals(itemId));
        }
    }

    public List<Item> getItemsInMap(String mapId) {
        List<Item> items = mapItems.get(mapId);
        return items != null ? items : new CopyOnWriteArrayList<>();
    }

    /**
     * 몬스터 처치 시 아이템 드롭
     * - 확정: 코인 (50~200 메소 랜덤)
     * - 1% 확률: 장비 아이템 (bigWeapon, blackBottom, blackHat, blueHat, brownTop, puppleTop 중 랜덤)
     */
    private void dropItemsOnMonsterDeath(String mapId, int monsterX, int monsterY) {
        Random random = new Random();
        
        // 1. 확정 코인 드롭 (50~200 메소 랜덤)
        int mesosAmount = 50 + random.nextInt(151); // 50~200
        String coinId = "coin_" + UUID.randomUUID().toString();
        Item coin = new Item(coinId, "coin", mesosAmount + " 메소", monsterX, monsterY, 
                           "../img/tabler_coin.png", mesosAmount);
        addItem(mapId, coin);
        System.out.println("Dropped coin with " + mesosAmount + " mesos at (" + monsterX + ", " + monsterY + ")");
        
        // 2. 1% 확률로 장비 아이템 드롭
        if (random.nextInt(100) < 1) { // 1% 확률
            String[] equipmentTypes = {
                "bigWeapon", "blackBottom", "blackHat", 
                "blueHat", "brownTop", "puppleTop"
            };
            String equipmentType = equipmentTypes[random.nextInt(equipmentTypes.length)];
            String equipmentId = "item_" + UUID.randomUUID().toString();
            String equipmentName = getEquipmentName(equipmentType);
            Item equipment = new Item(equipmentId, equipmentType, equipmentName, 
                                    monsterX + 30, monsterY, // 코인과 약간 떨어진 위치
                                    "../img/clothes/" + equipmentType + ".png");
            addItem(mapId, equipment);
            System.out.println("Dropped equipment: " + equipmentType + " at (" + (monsterX + 30) + ", " + monsterY + ")");
        }
    }

    private String getEquipmentName(String itemType) {
        switch (itemType) {
            case "bigWeapon": return "큰 무기";
            case "blackBottom": return "검은 하의";
            case "blackHat": return "검은 모자";
            case "blueHat": return "파란 모자";
            case "brownTop": return "갈색 상의";
            case "puppleTop": return "보라 상의";
            default: return itemType;
        }
    }
}
