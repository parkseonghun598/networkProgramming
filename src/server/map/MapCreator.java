package server.map;

import common.ImagePath;
import common.map.Portal;
import common.monster.GreenSlime;

import java.util.Collections;
import java.util.List;

public class MapCreator {
    public static GameMap Hennessis() {
        Portal toBossMap = new Portal(700, 475, 100, 100, "bossMap", 100, 475);
        return new GameMap(
                "hennesis",
                ImagePath.HENNESSIS_IMAGE_PATH,
                10,
                GreenSlime::new,
                List.of(toBossMap));
    }

    public static GameMap Robby() {
        // todo : 다른 맵
        return null;
    }

    public static GameMap BossMap() {
        Portal toHenesys = new Portal(50, 475, 100, 100, "hennesis", 650, 475);
        return new GameMap(
                "bossMap",
                ImagePath.BOSSBG_IMAGE_PATH,
                5,
                GreenSlime::new,
                List.of(toHenesys));
    }
}
