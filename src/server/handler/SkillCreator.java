package server.handler;

import common.enums.Direction;
import common.player.Player;
import common.skills.Skill;
import common.skills.Skill1;
import common.skills.Skill2;
import common.skills.Skill3;
import common.skills.Skill4;

public class SkillCreator {
    public Skill createSkill(String skillType, String skillId, Player player, Direction direction) {
        switch (skillType) {
            case "skill1":
                // Center skill vertically: Player height 100, Skill height 70 -> offset +15
                return new Skill1(skillId, player.getId(), player.getX(), player.getY() + 15, direction);
            case "skill2":
                return new Skill2(skillId, player.getId(), player.getX(), player.getY() + 15, direction);
            case "skill3":
                return new Skill3(skillId, player.getId(), player.getX(), player.getY() + 15, direction);
            case "skill4":
                return new Skill4(skillId, player.getId(), player.getX(), player.getY() + 15, direction);
        }

        return null;
    }
}
