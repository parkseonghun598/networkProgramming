package common.skills;

import common.enums.Direction;

import javax.swing.ImageIcon;

/**
 * Skill4 구현 클래스 - E키
 * 중간 속도, 중간 사거리, 중간 데미지
 */
public class Skill4 extends Skill {
    private static final String RESOURCE_PATH = "../img/skill1_transparent.gif";
    private static final int SKILL_SPEED = 6;
    private static final int SKILL_RANGE = 500;
    private static final int SKILL_WIDTH = 70;
    private static final int SKILL_HEIGHT = 70;

    public Skill4(String id, String playerId, int x, int y, Direction direction) {
        super(id, playerId, x, y, direction);
        this.speed = SKILL_SPEED;
        this.range = SKILL_RANGE;
        this.width = SKILL_WIDTH;
        this.height = SKILL_HEIGHT;
        this.damage = 4;
        this.cooldown = 4000;
        loadResources();
    }

    @Override
    protected void loadResources() {
        try {
            this.sprite = new ImageIcon(RESOURCE_PATH).getImage();
        } catch (Exception e) {
            System.err.println("ERROR loading skill4 sprite: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String getType() {
        return "skill4";
    }
}
