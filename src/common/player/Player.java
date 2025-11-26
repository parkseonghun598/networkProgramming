package common.player;

import common.enums.Direction;
import common.item.Item;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String id;
    private String username;
    private int x;
    private int y;
    private String state; // "idle", "move", "jump"
    private Direction direction;
    private String mapId = "hennesis";
    private String characterType = "defaultWarrior"; // 캐릭터 스킨
    private List<Item> inventory = new ArrayList<>(); // 인벤토리
    private int mesos = 0; // 메소 (게임 화폐)

    public String getCharacterType() {
        return characterType;
    }

    public void setCharacterType(String characterType) {
        this.characterType = characterType;
    }

    public String getMapId() {
        return mapId;
    }

    public void setMapId(String mapId) {
        this.mapId = mapId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void setDirection(String direction) {
        this.direction = Direction.fromString(direction);
    }

    public List<Item> getInventory() {
        return inventory;
    }

    public void setInventory(List<Item> inventory) {
        this.inventory = inventory;
    }

    public void addItemToInventory(Item item) {
        this.inventory.add(item);
    }

    public void removeItemFromInventory(String itemId) {
        this.inventory.removeIf(item -> item.getId().equals(itemId));
    }

    public int getMesos() {
        return mesos;
    }

    public void setMesos(int mesos) {
        this.mesos = mesos;
    }

    public void addMesos(int amount) {
        this.mesos += amount;
    }

    public boolean removeMesos(int amount) {
        if (this.mesos >= amount) {
            this.mesos -= amount;
            return true;
        }
        return false;
    }
}
