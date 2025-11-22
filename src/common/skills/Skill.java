package common.skills;

import common.enums.Direction;

import java.awt.Image;

import static java.lang.Thread.sleep;

public abstract class Skill {
    protected String id;
    protected String playerId;
    protected int x;
    protected int y;
    protected Direction direction;
    protected boolean active;
    protected int speed;
    protected int range;
    protected int width;
    protected int height;
    protected int traveledDistance;
    protected Image sprite;
    protected int damage;
    protected long cooldown; // in milliseconds

    public Skill(String id, String playerId, int x, int y, Direction direction) {
        this.id = id;
        this.playerId = playerId;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.active = true;
        this.traveledDistance = 0;
    }

    protected abstract void loadResources();

    public void update() throws InterruptedException {
        if (!active)
            return;
        move();
        checkRange();
    }

    private void move() throws InterruptedException {
        if (direction.equals(Direction.LEFT)) {
            x -= speed;
        } else if (direction.equals(Direction.RIGHT)) {
            x += speed;
        }
        traveledDistance += speed;
        sleep(10);
    }

    private void checkRange() {
        if (traveledDistance >= range) {
            active = false;
        }
    }

    public String getId() {
        return id;
    }

    public String getPlayerId() {
        return playerId;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Direction getDirection() {
        return direction;
    }

    public boolean isActive() {
        return active;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getRange() {
        return range;
    }

    public void deactivate() {
        this.active = false;
    }

    public Image getSprite() {
        return sprite;
    }

    public int getDamage() {
        return damage;
    }

    public long getCooldown() {
        return cooldown;
    }

    public abstract String getType();
}
