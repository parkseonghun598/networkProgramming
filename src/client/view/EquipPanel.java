package client.view;

import client.util.SpriteManager;
import common.player.Player;
import common.util.ItemSlotMapper;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * 장비 창 UI 패널
 * 착용한 아이템을 표시하고 관리하는 패널
 */
public class EquipPanel extends JPanel {
    private BufferedImage backgroundImage;
    private boolean isVisible = false;
    private Point initialClick; // 드래그 시작 위치
    
    // 장비 창 크기 (이미지 크기에 맞춤)
    private int panelWidth = 300;
    private int panelHeight = 400;
    
    // 장비 슬롯 위치 (equipment_Inventory.png 이미지 기준)
    // 일반적인 장비 창: 캐릭터 실루엣 중앙, 슬롯들이 주변 배치
    private static final int SLOT_SIZE = 32;
    
    // 이미지 기준 원본 크기 (300x400 가정, 실제 이미지 크기에 맞춰 조정 필요)
    private static final int BASE_WIDTH = 300;
    private static final int BASE_HEIGHT = 400;
    
    // CAP (모자) - 캐릭터 머리 위 가로 슬롯
    private static final int CAP_SLOT_X_BASE = 65; 
    private static final int CAP_SLOT_Y_BASE = 70;
    
    // CLOTHES (상의) - 캐릭터 몸통 중앙
    private static final int CLOTHES_SLOT_X_BASE = 65;
    private static final int CLOTHES_SLOT_Y_BASE = 195;
    
    // PANTS (하의) - 캐릭터 하반신
    private static final int PANTS_SLOT_X_BASE = 65;
    private static final int PANTS_SLOT_Y_BASE = 240;
    
    // WEAPON (무기) - 캐릭터 오른쪽 손 위치
    private static final int WEAPON_SLOT_X_BASE = 180;
    private static final int WEAPON_SLOT_Y_BASE = 195;
    
    // GLOVES (장갑) - 캐릭터 왼쪽 손 위치
    private static final int GLOVES_SLOT_X_BASE = 10;
    private static final int GLOVES_SLOT_Y_BASE = 240;
    
    // SHOES (신발) - 캐릭터 발 위치
    private static final int SHOES_SLOT_X_BASE = 65;
    private static final int SHOES_SLOT_Y_BASE = 280;
    
    // 콜백 함수
    private EquipPanelCallback callback;
    
    public interface EquipPanelCallback {
        void onUnequip(ItemSlotMapper.EquipmentSlot slot);
    }
    
    public EquipPanel(EquipPanelCallback callback) {
        this.callback = callback;
        setLayout(null);
        setOpaque(false);

        // Load equipment inventory background
        try {
            backgroundImage = ImageIO.read(new File("../img/equipment_Inventory.png"));
            System.out.println("Loaded equipment inventory interface");
            
            // 이미지 크기에 맞춰 패널 크기 조정
            if (backgroundImage != null) {
                panelWidth = backgroundImage.getWidth();
                panelHeight = backgroundImage.getHeight();
            }
        } catch (IOException e) {
            System.err.println("Failed to load equipment inventory interface: " + e.getMessage());
        }
        
        setBounds(400, 50, panelWidth, panelHeight);
        
        // 드래그 기능 추가
        addDragListeners();
        
        // 클릭 이벤트 추가
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isVisible) return;
                
                int x = e.getX();
                int y = e.getY();
                
                // 이미지 크기에 비례하여 슬롯 위치 계산
                double scaleX = (double) getWidth() / BASE_WIDTH;
                double scaleY = (double) getHeight() / BASE_HEIGHT;
                
                int capX = (int) (CAP_SLOT_X_BASE * scaleX);
                int capY = (int) (CAP_SLOT_Y_BASE * scaleY);
                int clothesX = (int) (CLOTHES_SLOT_X_BASE * scaleX);
                int clothesY = (int) (CLOTHES_SLOT_Y_BASE * scaleY);
                int pantsX = (int) (PANTS_SLOT_X_BASE * scaleX);
                int pantsY = (int) (PANTS_SLOT_Y_BASE * scaleY);
                int weaponX = (int) (WEAPON_SLOT_X_BASE * scaleX);
                int weaponY = (int) (WEAPON_SLOT_Y_BASE * scaleY);
                int glovesX = (int) (GLOVES_SLOT_X_BASE * scaleX);
                int glovesY = (int) (GLOVES_SLOT_Y_BASE * scaleY);
                int shoesX = (int) (SHOES_SLOT_X_BASE * scaleX);
                int shoesY = (int) (SHOES_SLOT_Y_BASE * scaleY);
                
                // 장비 슬롯 클릭 감지
                if (isInSlot(x, y, capX, capY)) {
                    if (callback != null) {
                        callback.onUnequip(ItemSlotMapper.EquipmentSlot.HAT);
                    }
                } else if (isInSlot(x, y, clothesX, clothesY)) {
                    if (callback != null) {
                        callback.onUnequip(ItemSlotMapper.EquipmentSlot.TOP);
                    }
                } else if (isInSlot(x, y, pantsX, pantsY)) {
                    if (callback != null) {
                        callback.onUnequip(ItemSlotMapper.EquipmentSlot.BOTTOM);
                    }
                } else if (isInSlot(x, y, weaponX, weaponY)) {
                    if (callback != null) {
                        callback.onUnequip(ItemSlotMapper.EquipmentSlot.WEAPON);
                    }
                } else if (isInSlot(x, y, glovesX, glovesY)) {
                    if (callback != null) {
                        callback.onUnequip(ItemSlotMapper.EquipmentSlot.GLOVES);
                    }
                } else if (isInSlot(x, y, shoesX, shoesY)) {
                    if (callback != null) {
                        callback.onUnequip(ItemSlotMapper.EquipmentSlot.SHOES);
                    }
                }
            }
        });
    }
    
    private boolean isInSlot(int x, int y, int slotX, int slotY) {
        return x >= slotX && x <= slotX + SLOT_SIZE &&
               y >= slotY && y <= slotY + SLOT_SIZE;
    }
    
    private void addDragListeners() {
        MouseAdapter dragListener = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (isVisible) {
                    initialClick = e.getPoint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (isVisible && initialClick != null) {
                    int thisX = getLocation().x;
                    int thisY = getLocation().y;

                    int xMoved = e.getX() - initialClick.x;
                    int yMoved = e.getY() - initialClick.y;

                    int newX = thisX + xMoved;
                    int newY = thisY + yMoved;

                    Container parent = getParent();
                    if (parent != null) {
                        newX = Math.max(0, Math.min(newX, parent.getWidth() - getWidth()));
                        newY = Math.max(0, Math.min(newY, parent.getHeight() - getHeight()));
                    }

                    setLocation(newX, newY);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                initialClick = null;
            }
        };

        addMouseListener(dragListener);
        addMouseMotionListener(dragListener);
    }
    
    private Player currentPlayer;
    
    public void updatePlayer(Player player) {
        this.currentPlayer = player;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (!isVisible) {
            return;
        }

        // 배경 이미지 그리기
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fallback 배경
            g.setColor(new Color(50, 50, 50, 220));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(new Color(100, 100, 100));
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
        
        if (currentPlayer == null) {
            return;
        }
        
        // 이미지 크기에 비례하여 슬롯 위치 계산
        double scaleX = (double) getWidth() / BASE_WIDTH;
        double scaleY = (double) getHeight() / BASE_HEIGHT;
        
        int capX = (int) (CAP_SLOT_X_BASE * scaleX);
        int capY = (int) (CAP_SLOT_Y_BASE * scaleY);
        int clothesX = (int) (CLOTHES_SLOT_X_BASE * scaleX);
        int clothesY = (int) (CLOTHES_SLOT_Y_BASE * scaleY);
        int pantsX = (int) (PANTS_SLOT_X_BASE * scaleX);
        int pantsY = (int) (PANTS_SLOT_Y_BASE * scaleY);
        int weaponX = (int) (WEAPON_SLOT_X_BASE * scaleX);
        int weaponY = (int) (WEAPON_SLOT_Y_BASE * scaleY);
        int glovesX = (int) (GLOVES_SLOT_X_BASE * scaleX);
        int glovesY = (int) (GLOVES_SLOT_Y_BASE * scaleY);
        int shoesX = (int) (SHOES_SLOT_X_BASE * scaleX);
        int shoesY = (int) (SHOES_SLOT_Y_BASE * scaleY);
        
        // 모든 슬롯에 빨간색 테두리 그리기 
        g.setColor(Color.RED);
        g.drawRect(capX, capY, SLOT_SIZE, SLOT_SIZE);
        g.drawRect(clothesX, clothesY, SLOT_SIZE, SLOT_SIZE);
        g.drawRect(pantsX, pantsY, SLOT_SIZE, SLOT_SIZE);
        g.drawRect(weaponX, weaponY, SLOT_SIZE, SLOT_SIZE);
        g.drawRect(glovesX, glovesY, SLOT_SIZE, SLOT_SIZE);
        g.drawRect(shoesX, shoesY, SLOT_SIZE, SLOT_SIZE);
        
        // 착용된 아이템을 슬롯에 표시
        drawEquippedItem(g, capX, capY, currentPlayer.getEquippedHat());
        drawEquippedItem(g, clothesX, clothesY, currentPlayer.getEquippedTop());
        drawEquippedItem(g, pantsX, pantsY, currentPlayer.getEquippedBottom());
        drawEquippedItem(g, weaponX, weaponY, currentPlayer.getEquippedWeapon());
        drawEquippedItem(g, glovesX, glovesY, currentPlayer.getEquippedGloves());
        drawEquippedItem(g, shoesX, shoesY, currentPlayer.getEquippedShoes());
    }
    
    private void drawEquippedItem(Graphics g, int x, int y, String itemType) {
        if (itemType == null || itemType.equals("none")) {
            return;
        }
        
        // 아이템 스프라이트 로드
        Image itemSprite = SpriteManager.getSprite(itemType);
        if (itemSprite == null) {
            itemSprite = SpriteManager.getSpriteByPath("../img/clothes/" + itemType + ".png");
        }
        
        if (itemSprite != null) {
            // 슬롯 내부에 아이템 이미지 그리기
            g.drawImage(itemSprite, x + 2, y + 2, SLOT_SIZE - 4, SLOT_SIZE - 4, this);
        } else {
            // Fallback - 아이템 타입 텍스트
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            String displayText = itemType.length() > 6 ? itemType.substring(0, 6) : itemType;
            g.drawString(displayText, x + 2, y + 20);
        }
    }

    public void toggleVisibility() {
        isVisible = !isVisible;
        repaint();
    }

    public void show() {
        isVisible = true;
        repaint();
    }

    public void hide() {
        isVisible = false;
        repaint();
    }

    public boolean isEquipPanelVisible() {
        return isVisible;
    }
}
