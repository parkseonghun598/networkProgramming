package common.enums;

public enum Direction {
    LEFT("left"),
    RIGHT("right"),
    UP("up"),
    DOWN("down");

    private final String value;

    Direction(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Direction fromString(String text) {
        for (Direction d : Direction.values()) {
            if (d.value.equalsIgnoreCase(text)) {
                return d;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return value;
    }
}
