package common.util;

/**
 * 아이템 타입을 장비 슬롯으로 매핑하는 유틸리티 클래스
 */
public class ItemSlotMapper {
    
    /**
     * 아이템 타입을 장비 슬롯으로 변환
     * @param itemType 아이템 타입 (예: "bigWeapon", "blackHat", "brownTop", "blackBottom")
     * @return 장비 슬롯 타입 (WEAPON, HAT, TOP, BOTTOM) 또는 null
     */
    public static EquipmentSlot getEquipmentSlot(String itemType) {
        if (itemType == null) {
            return null;
        }
        
        // 무기 타입
        if (itemType.equals("defaultWeapon") || itemType.equals("bigWeapon")) {
            return EquipmentSlot.WEAPON;
        }
        
        // 모자 타입
        if (itemType.equals("blackHat") || itemType.equals("blueHat")) {
            return EquipmentSlot.HAT;
        }
        
        // 상의 타입
        if (itemType.equals("brownTop") || itemType.equals("puppleTop") || itemType.equals("defaultTop")) {
            return EquipmentSlot.TOP;
        }
        
        // 하의 타입
        if (itemType.equals("blackBottom") || itemType.equals("defaultBottom")) {
            return EquipmentSlot.BOTTOM;
        }
        
        // 장갑 타입
        if (itemType.equals("glove")) {
            return EquipmentSlot.GLOVES;
        }
        
        // 신발 타입
        if (itemType.equals("shoes")) {
            return EquipmentSlot.SHOES;
        }
        
        // 장비 아이템이 아님
        return null;
    }
    
    /**
     * 장비 슬롯 열거형
     */
    public enum EquipmentSlot {
        WEAPON,
        HAT,
        TOP,
        BOTTOM,
        GLOVES,
        SHOES
    }
}

