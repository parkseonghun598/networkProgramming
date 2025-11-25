package client.util;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static common.ImagePath.*;

// 이미지 리소스 로드 & 저장 클래스
public class SpriteManager {
    private static final Map<String, Image> sprites = new HashMap<>();

    public static void loadSprites() {
        try {
            // Legacy player sprite
            sprites.put("player", ImageIO.read(new File(PLAYER_IMAGE_PATH)));
            
            // Load defaultWarrior sprite (stand animation frame 0)
            File defaultWarriorFile = new File("../img/character/defaultWarrior/stand1_0.png");
            if (defaultWarriorFile.exists()) {
                sprites.put("defaultWarrior", ImageIO.read(defaultWarriorFile));
                System.out.println("Loaded defaultWarrior sprite");
            } else {
                System.err.println("defaultWarrior sprite not found: " + defaultWarriorFile.getAbsolutePath());
            }
            
            // Load NPC sprites
            File npcWarriorFile = new File(NPC_WARRIOR_IMAGE_PATH);
            if (npcWarriorFile.exists()) {
                sprites.put("npc_warrior", ImageIO.read(npcWarriorFile));
                System.out.println("Loaded npc_warrior sprite");
            } else {
                System.err.println("npc_warrior sprite not found: " + npcWarriorFile.getAbsolutePath());
            }
            
            // Load item sprites from clothes folder
            String[] itemTypes = {
                "defaultWeapon", "bigWeapon", "blackBottom", "blackHat", "blueHat",
                "brownTop", "defaultBottom", "defaultTop", "glove", "hair", "puppleTop", "shoes"
            };
            
            for (String itemType : itemTypes) {
                File itemFile = new File("../img/clothes/" + itemType + ".png");
                if (itemFile.exists()) {
                    sprites.put(itemType, ImageIO.read(itemFile));
                    System.out.println("Loaded " + itemType + " sprite");
                } else {
                    System.err.println(itemType + " sprite not found: " + itemFile.getAbsolutePath());
                }
            }
            
            // Monsters and effects
            sprites.put("그린 슬라임", new ImageIcon(GREEN_SLIME_IMAGE_PATH).getImage());
            sprites.put("portal", new ImageIcon(PORTAL_GIF_PATH).getImage());
        } catch (IOException e) {
            System.err.println("Failed to load sprites: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Image getSprite(String name) {
        return sprites.get(name);
    }
}