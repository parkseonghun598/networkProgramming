package client.controller;

import common.player.Player;
import common.enums.Direction;
import java.awt.event.KeyEvent;

public class PlayerInputHandler {

    public static void handleKeyPress(KeyEvent e, Player myPlayer, PlayerMovementCallback callback) {
        if (myPlayer == null) return;

        int x = myPlayer.getX();
        int y = myPlayer.getY();

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                myPlayer.setX(x-5);
                myPlayer.setDirection(Direction.LEFT);
                myPlayer.setState("move");
                break;
            case KeyEvent.VK_RIGHT:
                myPlayer.setX(x+5);
                myPlayer.setDirection(Direction.RIGHT);
                myPlayer.setState("move");
                break;
            case KeyEvent.VK_UP:
                callback.usePortal();
                return; // USE_PORTAL 메시지는 바로 전송되므로 sendUpdate()를 호출하지 않음
            case KeyEvent.VK_SPACE:
                if (callback.canJump()) {
                    callback.initiateJump();
                    myPlayer.setState("jump");
                }
                break;
            case KeyEvent.VK_Q:
                callback.useSkill("skill1");
                break;
            case KeyEvent.VK_W:
                callback.useSkill("skill2");
                break;
            case KeyEvent.VK_E:
                callback.useSkill("skill4");
                break;
            case KeyEvent.VK_R:
                callback.useSkill("skill3");
                break;
            default:
                return;
        }
        callback.sendUpdate();
    }

    public static void handleKeyRelease(KeyEvent e, Player myPlayer, PlayerMovementCallback callback) {
        if (myPlayer == null) return;

        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                callback.sendUpdate();
                break;
        }
    }

    public interface PlayerMovementCallback {
        boolean canJump();
        void initiateJump();
        void useSkill(String skillType);
        void usePortal();
        void sendUpdate();
    }
}
