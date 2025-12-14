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
    private int level = 1; // 레벨 (1~5)
    private int xp = 0; // 현재 경험치
    private int maxXp = 100; // 레벨업에 필요한 경험치
    
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

    public int getMesos() {
        return mesos;
    }

    public void setMesos(int mesos) {
        this.mesos = mesos;
    }
    
    // 레벨 관련 getter/setter
    public int getLevel() {
        return level;
    }
    
    public void setLevel(int level) {
        // 최대 레벨 5로 제한
        this.level = Math.min(Math.max(1, level), 5);
    }
    
    public int getXp() {
        return xp;
    }
    
    public void setXp(int xp) {
        this.xp = Math.max(0, xp);
    }
    
    public int getMaxXp() {
        return maxXp;
    }
    
    public void setMaxXp(int maxXp) {
        this.maxXp = maxXp;
    }
    
    /**
     * 경험치를 추가하고 레벨업 여부를 반환합니다
     * @param amount 추가할 경험치
     * @return 레벨업했으면 true
     */
    public boolean addXp(int amount) {
        this.xp += amount;
        
        // 레벨업 체크
        if (this.xp >= this.maxXp && this.level < 5) {
            this.xp -= this.maxXp;
            this.level++;
            // 다음 레벨업에 필요한 경험치 증가 (레벨마다 +50)
            this.maxXp = 100 + (this.level - 1) * 50;
            return true;
        }
        
        // 최대 레벨이면 경험치를 maxXp로 제한
        if (this.level >= 5) {
            this.xp = Math.min(this.xp, this.maxXp);
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
     * - 초기 상태(equippedWeapon = "none"): "defaultWarrior" 폴더 사용
     * - defaultWeapon 착용: "defaultWarrior_none_none_none_none" 폴더 사용
     * - 커스텀 장비 착용: "defaultWarrior_{weapon}_{bottom}_{top}_{hat}" 형식
     * 예: "defaultWarrior" (초기 상태, 아무 무기도 착용 안 함)
     * 예: "defaultWarrior_none_none_none_none" (defaultWeapon 착용)
     * 예: "defaultWarrior_bigWeapon_blackBottom_brownTop_blackHat" (커스텀 장비)
     */
    public String getCharacterFolderPath() {
        // 무기 처리: "none"이면 기본 경로, "defaultWeapon"이면 "none"으로 변환하여 경로 생성
        if (equippedWeapon == null || equippedWeapon.equals("none")) {
            // 초기 상태: 기본 경로 사용
            return characterType;
        }
        
        // 기본 장비는 "none"으로 변환, 커스텀 장비는 그대로 사용
        String weapon = equippedWeapon.equals("defaultWeapon") ? "none" : equippedWeapon;
        String bottom = (equippedBottom == null || equippedBottom.equals("none") || equippedBottom.equals("defaultBottom")) 
                        ? "none" : equippedBottom;
        String top = (equippedTop == null || equippedTop.equals("none") || equippedTop.equals("defaultTop")) 
                     ? "none" : equippedTop;
        String hat = (equippedHat == null || equippedHat.equals("none")) 
                     ? "none" : equippedHat;
        
        // 형식: characterType_weapon_bottom_top_hat
        return String.format("%s_%s_%s_%s_%s", characterType, weapon, bottom, top, hat);
    }
}
