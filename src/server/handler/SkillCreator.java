package server.handler;

import common.enums.Direction;
import common.player.Player;
import common.skills.Skill;
import common.skills.Skill1;
import common.skills.Skill2;
import common.skills.Skill3;
import common.skills.Skill4;
import common.util.StatCalculator;

public class SkillCreator {
    public Skill createSkill(String skillType, String skillId, Player player, Direction direction) {
        Skill skill = null;
        int baseDamage = 0;
        
        // 플레이어 공격력 계산
        int playerAttack = StatCalculator.calculateAttack(player);
        
        switch (skillType) {
            case "skill1":
                // Center skill vertically: Player height 100, Skill height 70 -> offset +15
                skill = new Skill1(skillId, player.getId(), player.getX(), player.getY() + 15, direction);
                baseDamage = 2; // Skill1 기본 데미지
                break;
            case "skill2":
                skill = new Skill2(skillId, player.getId(), player.getX(), player.getY() + 15, direction);
                baseDamage = 3; // Skill2 기본 데미지
                break;
            case "skill3":
                skill = new Skill3(skillId, player.getId(), player.getX(), player.getY() + 15, direction);
                baseDamage = 5; // Skill3 기본 데미지
                break;
            case "skill4":
                skill = new Skill4(skillId, player.getId(), player.getX(), player.getY() + 15, direction);
                baseDamage = 4; // Skill4 기본 데미지
                break;
        }
        
        if (skill != null) {
            // 공격력을 반영한 데미지 설정
            skill.setDamageWithAttack(baseDamage, playerAttack);
        }

        return skill;
    }
}
