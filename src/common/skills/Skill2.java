package common.skills;

import common.enums.Direction;

import javax.swing.ImageIcon;

/**
 * Skill2 구현 클래스 - W키
 * 빠른 속도, 짧은 사거리, 중간 데미지
 */
public class Skill2 extends Skill {
    private static final String RESOURCE_PATH = "../img/skill1_transparent.gif";
    private static final int SKILL_SPEED = 7;
    private static final int SKILL_RANGE = 300;
    private static final int SKILL_WIDTH = 70;
    private static final int SKILL_HEIGHT = 70;

    public Skill2(String id, String playerId, int x, int y, Direction direction) {
        super(id, playerId, x, y, direction);
        this.speed = SKILL_SPEED;
        this.range = SKILL_RANGE;
        this.width = SKILL_WIDTH;
        this.height = SKILL_HEIGHT;
        this.damage = 3;
        this.cooldown = 3000;
        loadResources();
    }

    @Override
    protected void loadResources() {
        try {
            this.sprite = new ImageIcon(RESOURCE_PATH).getImage();
        } catch (Exception e) {
            System.err.println("ERROR loading skill2 sprite: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public String getType() {
        return "skill2";
    }
}
