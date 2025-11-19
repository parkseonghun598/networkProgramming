package server.handler;

import common.enums.Direction;
import common.player.Player;
import common.skills.Skill;
import common.skills.Skill1;

public class SkillCreator {
    public Skill createSkill(String skillType, String skillId, Player player, Direction direction) {
        switch(skillType) {
            case "skill1":
                return new Skill1(skillId, player.getId(), player.getX(), player.getY(), direction);
            case "skill2":
                return null;
        }

        return null;
    }
}
