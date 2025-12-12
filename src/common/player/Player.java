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
     * - 기본 장비(defaultTop, defaultBottom, glove, shoes)는 경로에서 제외
     * - 커스텀 장비만 경로에 포함
     * 예: "defaultWarrior_bigWeapon_blackBottom_brownTop_blackHat"
     */
    public String getCharacterFolderPath() {
        // 커스텀 장비만 추출 (기본 장비는 제외)
        String weapon = (equippedWeapon != null && !equippedWeapon.equals("none") && !equippedWeapon.equals("defaultWeapon")) 
                        ? equippedWeapon : "none";
        String bottom = (equippedBottom != null && !equippedBottom.equals("none") && !equippedBottom.equals("defaultBottom")) 
                        ? equippedBottom : "none";
        String top = (equippedTop != null && !equippedTop.equals("none") && !equippedTop.equals("defaultTop")) 
                     ? equippedTop : "none";
        String hat = (equippedHat != null && !equippedHat.equals("none")) 
                     ? equippedHat : "none";
        
        // 모든 장비가 기본값이면 기본 경로 사용
        if (weapon.equals("none") && bottom.equals("none") && top.equals("none") && hat.equals("none")) {
            return characterType;
        }
        
        // 커스텀 장비만 경로에 포함
        return String.format("%s_%s_%s_%s_%s", characterType, weapon, bottom, top, hat);
    }
}
