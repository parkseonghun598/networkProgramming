package common.monster;

import java.util.UUID;

public class Dragon extends Monster {
    public Dragon() {
        super.setId(UUID.randomUUID().toString());
        super.setName("드래곤");
        super.setHp(20);
        super.setType("red");
        super.setX((int) Math.round(Math.random() * 100));
        super.setY((int) Math.round(Math.random() * 100+400));
        super.setDirection(common.enums.Direction.RIGHT);
    }
}
