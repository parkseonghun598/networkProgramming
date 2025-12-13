package common.util;

import common.player.Player;

/**
 * 플레이어 스텟 계산 유틸리티
 */
public class StatCalculator {
    
    /**
     * 플레이어의 공격력을 계산합니다 (기본 공격력 + 레벨 보너스 + 장비 보너스)
     */
    public static int calculateAttack(Player player) {
        if (player == null) {
            return 10; // 기본 공격력
        }
        
        int baseAttack = 10;
        int levelBonus = 0;
        int bonusAttack = 0;
        
        // 레벨에 따른 공격력 보너스 (레벨당 +3)
        levelBonus = (player.getLevel() - 1) * 3;
        
        // 장비별 공격력 보너스
        String weapon = player.getEquippedWeapon();
        if (weapon != null && !weapon.equals("none")) {
            switch (weapon) {
                case "defaultWeapon":
                    bonusAttack += 5;
                    break;
                case "bigWeapon":
                    bonusAttack += 15;
                    break;
            }
        }
        
        return baseAttack + levelBonus + bonusAttack;
    }
    
    /**
     * 플레이어의 전체 스텟을 계산합니다
     */
    public static PlayerStats calculateAllStats(Player player) {
        if (player == null) {
            return new PlayerStats(10, 10, 10, 10, 100, 50, 10, 5);
        }
        
        // 기본 스텟
        int baseStr = 10;
        int baseDex = 10;
        int baseInt = 10;
        int baseLuk = 10;
        int baseHp = 100;
        int baseMp = 50;
        int baseAttack = 10;
        int baseDefense = 5;
        
        // 레벨에 따른 공격력 보너스 (레벨당 +3)
        int levelBonus = 0;
        if (player.getLevel() > 1) {
            levelBonus = (player.getLevel() - 1) * 3;
        }
        
        // 장비 보너스 계산
        EquipmentBonus bonus = calculateEquipmentBonus(player);
        
        return new PlayerStats(
            baseStr + bonus.str,
            baseDex + bonus.dex,
            baseInt + bonus.intelligence,
            baseLuk + bonus.luk,
            baseHp + bonus.hp,
            baseMp + bonus.mp,
            baseAttack + levelBonus + bonus.attack,
            baseDefense + bonus.defense
        );
    }
    
    private static EquipmentBonus calculateEquipmentBonus(Player player) {
        EquipmentBonus bonus = new EquipmentBonus();
        
        addItemBonus(bonus, player.getEquippedWeapon());
        addItemBonus(bonus, player.getEquippedHat());
        addItemBonus(bonus, player.getEquippedTop());
        addItemBonus(bonus, player.getEquippedBottom());
        addItemBonus(bonus, player.getEquippedGloves());
        addItemBonus(bonus, player.getEquippedShoes());
        
        return bonus;
    }
    
    private static void addItemBonus(EquipmentBonus bonus, String itemType) {
        if (itemType == null || itemType.equals("none")) {
            return;
        }
        
        switch (itemType) {
            case "defaultWeapon":
                bonus.attack += 5;
                break;
            case "bigWeapon":
                bonus.attack += 15;
                bonus.str += 5;
                break;
            case "blackHat":
                bonus.defense += 3;
                bonus.hp += 20;
                break;
            case "blueHat":
                bonus.defense += 2;
                bonus.mp += 10;
                break;
            case "brownTop":
                bonus.defense += 5;
                bonus.hp += 30;
                break;
            case "puppleTop":
                bonus.defense += 8;
                bonus.hp += 50;
                bonus.intelligence += 3;
                break;
            case "blackBottom":
                bonus.defense += 4;
                bonus.hp += 25;
                break;
            case "defaultTop":
                bonus.defense += 2;
                bonus.hp += 10;
                break;
            case "defaultBottom":
                bonus.defense += 2;
                bonus.hp += 10;
                break;
            case "glove":
                bonus.defense += 1;
                bonus.dex += 2;
                break;
            case "shoes":
                bonus.defense += 1;
                bonus.dex += 1;
                break;
        }
    }
    
    private static class EquipmentBonus {
        int str = 0;
        int dex = 0;
        int intelligence = 0;
        int luk = 0;
        int hp = 0;
        int mp = 0;
        int attack = 0;
        int defense = 0;
    }
    
    public static class PlayerStats {
        private final int str;
        private final int dex;
        private final int intelligence;
        private final int luk;
        private final int hp;
        private final int mp;
        private final int attack;
        private final int defense;
        
        public PlayerStats(int str, int dex, int intelligence, int luk, int hp, int mp, int attack, int defense) {
            this.str = str;
            this.dex = dex;
            this.intelligence = intelligence;
            this.luk = luk;
            this.hp = hp;
            this.mp = mp;
            this.attack = attack;
            this.defense = defense;
        }
        
        public int getStr() { return str; }
        public int getDex() { return dex; }
        public int getInt() { return intelligence; }
        public int getLuk() { return luk; }
        public int getHp() { return hp; }
        public int getMp() { return mp; }
        public int getAttack() { return attack; }
        public int getDefense() { return defense; }
    }
}

