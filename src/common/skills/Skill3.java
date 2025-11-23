package common.skills;

import common.enums.Direction;

import javax.swing.ImageIcon;

/**
 * Skill3 구현 클래스 - R키
 * 느린 속도, 긴 사거리, 강한 데미지
 */
public class Skill3 extends Skill {
    private static final String RESOURCE_PATH = "../img/skill1_transparent.gif";
    private static final int SKILL_SPEED = 3;
    private static final int SKILL_RANGE = 600;
    private static final int SKILL_WIDTH = 70;
    private static final int SKILL_HEIGHT = 70;

    public Skill3(String id, String playerId, int x, int y, Direction direction) {
        super(id, playerId, x, y, direction);
        this.speed = SKILL_SPEED;
        this.range = SKILL_RANGE;
        this.width = SKILL_WIDTH;
        this.height = SKILL_HEIGHT;
        this.damage = 5;
        this.cooldown = 5000;
        loadResources();
    }

    @Override
    protected void loadResources() {
        try {
            this.sprite = new ImageIcon(RESOURCE_PATH).getImage();
        } catch (Exception e) {
            System.err.println("ERROR loading skill3 sprite: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String getType() {
        return "skill3";
    }
}
