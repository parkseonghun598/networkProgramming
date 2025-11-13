package client.controller;

import common.player.Player;
import common.enums.Direction;
import java.awt.event.KeyEvent;

public class PlayerInputHandler {

    public static void handleKeyPress(KeyEvent e, Player myPlayer, PlayerMovementCallback callback) {
        if (myPlayer == null) return;

        int x = myPlayer.getX();
        int y = myPlayer.getY();
        String state = "idle";
        boolean shouldSkip = false;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                x -= 5;
                myPlayer.setDirection(Direction.LEFT);
                state = "move";
                break;
            case KeyEvent.VK_RIGHT:
                x += 5;
                myPlayer.setDirection(Direction.RIGHT);
                state = "move";
                break;
            case KeyEvent.VK_SPACE:
                if (callback.canJump()) {
                    callback.initiateJump();
                    state = "jump";
                }
                break;
            case KeyEvent.VK_Q:
                callback.useSkill("skill1");
                shouldSkip = true;
                break;
            default:
                return;
        }

        if (!shouldSkip) {
            myPlayer.setX(x);
            myPlayer.setY(y);
            //todo:myPlayer.setDirection(direction);
            myPlayer.setState(state);
            callback.sendUpdate();
        }
    }

    public static void handleKeyRelease(KeyEvent e, Player myPlayer, PlayerMovementCallback callback) {
        if (myPlayer == null) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                myPlayer.setState("idle");
                callback.sendUpdate();
                break;
        }
    }

    public interface PlayerMovementCallback {
        boolean canJump();
        void initiateJump();
        void useSkill(String skillType);
        void sendUpdate();
    }
}
