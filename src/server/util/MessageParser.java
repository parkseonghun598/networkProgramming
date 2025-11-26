package server.util;

public class MessageParser {

    public static PlayerUpdateData parsePlayerUpdate(String message) {
        try {
            String payload = message.substring(message.indexOf("payload\":{") + 9);
            payload = payload.substring(1, payload.length() - 2);

            int x = 0;
            int y = 0;
            String state = "";
            String direction = "";

            String[] pairs = payload.split(",");
            for (String pair : pairs) {
                String[] keyValue = pair.split(":");
                String key = keyValue[0].replace("\"", "").trim();
                String value = keyValue[1].replace("\"", "").trim();

                switch (key) {
                    case "x":
                        x = Integer.parseInt(value);
                        break;
                    case "y":
                        y = Integer.parseInt(value);
                        break;
                    case "state":
                        state = value;
                        break;
                    case "direction":
                        direction = value;
                        break;
                }
            }

            return new PlayerUpdateData(x, y, state, direction);
        } catch (Exception e) {
            System.err.println("Failed to parse PLAYER_UPDATE: " + message);
            return null;
        }
    }

    public static SkillUseData parseSkillUse(String message) {
        try {
            String skillType = message.split("\"skillType\":\"")[1].split("\"")[0];
            String direction = "right"; // default
            if (message.contains("\"direction\":")) {
                direction = message.split("\"direction\":\"")[1].split("\"")[0];
            }
            return new SkillUseData(skillType, direction);
        } catch (Exception e) {
            System.err.println("Failed to parse SKILL_USE: " + message);
            return null;
        }
    }

    public static class PlayerUpdateData {
        public final int x;
        public final int y;
        public final String state;
        public final String direction;

        public PlayerUpdateData(int x, int y, String state, String direction) {
            this.x = x;
            this.y = y;
            this.state = state;
            this.direction = direction;
        }
    }

    public static class SkillUseData {
        public final String skillType;
        public final String direction;

        public SkillUseData(String skillType, String direction) {
            this.skillType = skillType;
            this.direction = direction;
        }
    }
}
