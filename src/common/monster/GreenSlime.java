package common.monster;

import java.util.UUID;

public class GreenSlime extends Monster {
    public GreenSlime() {
        super.setId(UUID.randomUUID().toString());
        super.setName("그린 슬라임");
        super.setHp(10);
        super.setType("green");
        super.setX((int) Math.round(Math.random() * 100));
        super.setY(525);
        super.setDirection(common.enums.Direction.RIGHT);
    }
}
