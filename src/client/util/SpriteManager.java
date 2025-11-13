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
            sprites.put("player", ImageIO.read(new File(PLAYER_IMAGE_PATH)));
            sprites.put("그린 슬라임", new ImageIcon(GREEN_SLIME_IMAGE_PATH).getImage());
        } catch (IOException e) {
            System.err.println("Failed to load sprites.");
        }
    }

    public static Image getSprite(String name) {
        return sprites.get(name);
    }
}