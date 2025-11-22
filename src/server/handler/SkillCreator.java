package server.handler;

import common.enums.Direction;
import common.player.Player;
import common.skills.Skill;
import common.skills.Skill1;

public class SkillCreator {
    public Skill createSkill(String skillType, String skillId, Player player, Direction direction) {
        switch (skillType) {
            case "skill1":
                // Center skill vertically: Player height 100, Skill height 70 -> offset +15
                return new Skill1(skillId, player.getId(), player.getX(), player.getY() + 15, direction);
            case "skill2":
                return null;
        }

        return null;
    }
}
