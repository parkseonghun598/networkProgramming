package server.map;

import common.ImagePath;
import common.map.Portal;
import common.monster.Dragon;
import common.monster.GreenSlime;
import common.npc.NPC;

import java.util.List;

public class MapCreator {
    public static GameMap Hennessis() {
        Portal toBossMap = new Portal(700, 475, 100, 100, "bossMap", 100, 525);
        return new GameMap(
                "hennesis",
                ImagePath.HENNESSIS_IMAGE_PATH,
                10,
                GreenSlime::new,
                List.of(toBossMap),
                null); // NPC 없음
    }

    public static GameMap WarriorRoom() {
        // 워리어 룸: 첫 접속 맵, 헤네시스로 가는 포탈
        Portal toHennesys = new Portal(600, 400, 100, 100, "hennesis", 100, 525);
        
        // NPC 배치
        NPC warriorNpc = new NPC("npc_warrior", "주먹펴고 일어서", 200, 420, ImagePath.NPC_WARRIOR_IMAGE_PATH);
        
        return new GameMap(
                "warriorRoom",
                ImagePath.WARRIOR_ROOM_IMAGE_PATH,
                0, // 몬스터 없음
                null,
                List.of(toHennesys),
                List.of(warriorNpc));
    }

    public static GameMap BossMap() {
        Portal toHenesys = new Portal(50, 475, 100, 100, "hennesis", 650, 525);
        return new GameMap(
                "bossMap",
                ImagePath.BOSSBG_IMAGE_PATH,
                5,
                Dragon::new,
                List.of(toHenesys),
                null); // NPC 없음
    }
}
