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
    
    // 착용 아이템 (캐릭터 외형에 영향)
    private String equippedWeapon = "none"; // 무기
    private String equippedHat = "none"; // 모자
    private String equippedTop = "defaultTop"; // 상의 (기본 착용)
    private String equippedBottom = "defaultBottom"; // 하의 (기본 착용)
    private String equippedGloves = "glove"; // 장갑 (기본 착용)
    private String equippedShoes = "shoes"; // 신발 (기본 착용)

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

    // 착용 아이템 getter/setter
    public String getEquippedWeapon() {
        return equippedWeapon;
    }

    public void setEquippedWeapon(String equippedWeapon) {
        this.equippedWeapon = equippedWeapon;
    }

    public String getEquippedHat() {
        return equippedHat;
    }

    public void setEquippedHat(String equippedHat) {
        this.equippedHat = equippedHat;
    }

    public String getEquippedTop() {
        return equippedTop;
    }

    public void setEquippedTop(String equippedTop) {
        this.equippedTop = equippedTop;
    }

    public String getEquippedBottom() {
        return equippedBottom;
    }

    public void setEquippedBottom(String equippedBottom) {
        this.equippedBottom = equippedBottom;
    }

    public String getEquippedGloves() {
        return equippedGloves;
    }

    public void setEquippedGloves(String equippedGloves) {
        this.equippedGloves = equippedGloves;
    }

    public String getEquippedShoes() {
        return equippedShoes;
    }

    public void setEquippedShoes(String equippedShoes) {
        this.equippedShoes = equippedShoes;
    }

    /**
     * 착용한 아이템 조합을 기반으로 캐릭터 폴더 경로를 생성합니다.
     * - 아무것도 착용 안했으면: "defaultWarrior"
     * - 하나라도 착용했으면: "defaultWarrior_무기_하의_상의_모자" (none 포함)
     * 예: "defaultWarrior_defaultWeapon_none_none_none"
     */
    public String getCharacterFolderPath() {
        // 아무것도 착용하지 않은 상태 (모두 none)
        boolean allNone = (equippedWeapon == null || equippedWeapon.equals("none")) &&
                          (equippedBottom == null || equippedBottom.equals("none")) &&
                          (equippedTop == null || equippedTop.equals("none")) &&
                          (equippedHat == null || equippedHat.equals("none"));
        
        if (allNone) {
            // 기본 상태: defaultWarrior 폴더 사용
            return characterType;
        } else {
            // 하나라도 착용했으면 모든 슬롯을 명시 (none 포함)
            String weapon = (equippedWeapon != null && !equippedWeapon.equals("none")) ? equippedWeapon : "none";
            String bottom = (equippedBottom != null && !equippedBottom.equals("none")) ? equippedBottom : "none";
            String top = (equippedTop != null && !equippedTop.equals("none")) ? equippedTop : "none";
            String hat = (equippedHat != null && !equippedHat.equals("none")) ? equippedHat : "none";
            
            return String.format("%s_%s_%s_%s_%s", characterType, weapon, bottom, top, hat);
        }
    }
}
