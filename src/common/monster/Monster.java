package common.monster;

import common.enums.Direction;

import java.util.Random;

public class Monster {
    private String id;
    private String name;
    private int hp;
    private String type;
    private int x;
    private int y;
    private String state;
    private Direction direction;

    private long nextMoveTime = 0;
    private static final int MAP_MIN_X = 50;
    private static final int MAP_MAX_X = 750;

    public void move() {
        long currentTime = System.currentTimeMillis();
        if (currentTime < nextMoveTime) {
            // Continue current action
            if ("move".equals(state)) {
                int currentX = getX();
                if (Direction.LEFT.equals(direction)) {
                    if (currentX > MAP_MIN_X) {
                        setX(currentX - 1);
                    } else {
                        // Hit wall, turn around immediately
                        setDirection(Direction.RIGHT);
                        setX(currentX + 1);
                    }
                } else { // RIGHT
                    if (currentX < MAP_MAX_X) {
                        setX(currentX + 1);
                    } else {
                        // Hit wall, turn around immediately
                        setDirection(Direction.LEFT);
                        setX(currentX - 1);
                    }
                }
            }
            return;
        }

        // Decide next action
        Random random = new Random();
        int action = random.nextInt(3); // 0: Idle, 1: Move Left, 2: Move Right
        int duration = 1000 + random.nextInt(2000); // 1-3 seconds

        if (action == 0) {
            setState("idle");
        } else if (action == 1) {
            setState("move");
            setDirection(Direction.LEFT);
        } else {
            setState("move");
            setDirection(Direction.RIGHT);
        }

        nextMoveTime = currentTime + duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
        if (this.maxHp == 0) {
            this.maxHp = hp;
        }
    }

    private int maxHp;

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public void takeDamage(int damage) {
        this.hp -= damage;
        if (this.hp < 0)
            this.hp = 0;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setDirection(String direction) {
        this.direction = Direction.fromString(direction);
    }
}
