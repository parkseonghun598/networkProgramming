package server.map;

import common.map.Portal;
import common.monster.Monster;
import common.npc.NPC;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;

public class GameMap {
    private final String mapId;
    private final String backgroundImagePath;
    private final List<Monster> monsters;
    private final int maxMonsters;
    private final Supplier<Monster> monsterFactory;
    private final List<Portal> portals;
    private final List<NPC> npcs;

    public GameMap(String mapId, String backgroundImagePath, int maxMonsters, Supplier<Monster> monsterFactory, List<Portal> portals, List<NPC> npcs) {
        this.mapId = mapId;
        this.backgroundImagePath = backgroundImagePath;
        this.maxMonsters = maxMonsters;
        this.monsterFactory = monsterFactory;
        this.monsters = new CopyOnWriteArrayList<>();
        this.portals = portals;
        this.npcs = npcs != null ? npcs : List.of();
    }

    public void update() {
        // 맵에 속한 몬스터들의 움직임 등 업데이트
        for (Monster monster : monsters) {
            monster.move();
        }

        // 몬스터 개체 수 관리
        manageMonsters();
    }

    private void manageMonsters() {
        while (monsters.size() < maxMonsters) {
            monsters.add(monsterFactory.get());
            System.out.println(mapId + "에서 몬스터 생성. 현재: " + monsters.size() + "/" + maxMonsters);
        }
    }

    public List<Monster> getMonsters() {
        return monsters;
    }

    public String getMapId() {
        return mapId;
    }

    public String getBackgroundImagePath() {
        return backgroundImagePath;
    }

    public List<Portal> getPortals() {
        return portals;
    }

    public List<NPC> getNpcs() {
        return npcs;
    }
}
