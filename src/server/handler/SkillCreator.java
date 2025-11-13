package server.handler;

import common.player.Player;
import common.skills.Skill;
import common.skills.Skill1;

public class SkillCreator {
    public Skill createSkill(String skillType, String skillId, Player player) {
        switch(skillType) {
            case "skill1":
                // System.out.println("Player " + playerId + " used " + data.skillType + " in direction: " + data.direction);
                return new Skill1(skillId, player.getId(), player.getX(), player.getY(),player.getDirection());
            case "skill2":
                return null;
        }

        return null;
    }
}
