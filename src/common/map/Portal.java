package common.map;

import java.awt.Rectangle;

public class Portal {
    private final Rectangle bounds;
    private final String targetMapId;
    private final int targetX;
    private final int targetY;

    public Portal(int x, int y, int width, int height, String targetMapId, int targetX, int targetY) {
        this.bounds = new Rectangle(x, y, width, height);
        this.targetMapId = targetMapId;
        this.targetX = targetX;
        this.targetY = targetY;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public String getTargetMapId() {
        return targetMapId;
    }

    public boolean isPlayerInside(int playerX, int playerY, int playerWidth, int playerHeight) {
        return bounds.intersects(new Rectangle(playerX, playerY, playerWidth, playerHeight));
    }

    public int getTargetX() {
        return targetX;
    }

    public int getTargetY() {
        return targetY;
    }
}
