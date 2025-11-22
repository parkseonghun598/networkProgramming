package common.skills;

import common.enums.Direction;

import javax.swing.ImageIcon;

/**
 * Skill1 구현 클래스 (skill1.gif 사용)
 */
public class Skill1 extends Skill {
    private static final String RESOURCE_PATH = "../img/skill1_transparent.gif";
    private static final int SKILL_SPEED = 5;
    private static final int SKILL_RANGE = 400;
    private static final int SKILL_WIDTH = 70;
    private static final int SKILL_HEIGHT = 70;

    public Skill1(String id, String playerId, int x, int y, Direction direction) {
        super(id, playerId, x, y, direction);
        this.speed = SKILL_SPEED;
        this.range = SKILL_RANGE;
        this.width = SKILL_WIDTH;
        this.height = SKILL_HEIGHT;
        this.damage = 2;
        this.cooldown = 2000;
        loadResources();
    }

    @Override
    protected void loadResources() {
        try {
            this.sprite = new ImageIcon(RESOURCE_PATH).getImage();
            // System.out.println("SUCCESS: Skill1 GIF loaded from: " + RESOURCE_PATH);
        } catch (Exception e) {
            System.err.println("ERROR loading skill1.gif: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String getType() {
        return "skill1";
    }
}
