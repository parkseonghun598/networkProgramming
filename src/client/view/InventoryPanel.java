package client.view;

import client.util.SpriteManager;
import common.inventory.Inventory;
import common.item.Item;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class InventoryPanel extends JPanel {
    private BufferedImage backgroundImage;
    private Inventory inventory;
    private boolean isVisible = false;
    private int mesos = 0; // 현재 메소

    public InventoryPanel(Inventory inventory) {
        this.inventory = inventory;
        setLayout(null);
        setOpaque(false);
        setBounds(200, 50, 172, 262); // 인벤토리 크기에 맞춤

        // Load inventory background
        try {
            backgroundImage = ImageIO.read(new File("../img/item_interface.png"));
            System.out.println("Loaded inventory interface");
        } catch (IOException e) {
            System.err.println("Failed to load inventory interface: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (!isVisible) {
            return;
        }

        // Draw background
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Fallback background
            g.setColor(new Color(50, 50, 50, 200));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(Color.WHITE);
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }

        // Draw items in grid
        drawInventoryItems(g);
        
        // Draw mesos
        drawMesos(g);
    }

    private void drawInventoryItems(Graphics g) {
        // 4x6 그리드 (4열 6행)
        int cols = 4;
        int slotSize = 31; // 각 슬롯 크기
        int startX = 6; // 시작 X 위치 (item_interface.png의 첫 번째 슬롯 시작점)
        int startY = 8; // 시작 Y 위치 (메소 표시 아래)
        int spacingX = 6; // 슬롯 간 가로 간격
        int spacingY = 6; // 슬롯 간 세로 간격

        java.util.List<Item> items = inventory.getItems();
        
        for (int i = 0; i < items.size() && i < 24; i++) {
            Item item = items.get(i);
            int row = i / cols;
            int col = i % cols;
            
            int x = startX + col * (slotSize + spacingX);
            int y = startY + row * (slotSize + spacingY);

            // Draw item sprite - 먼저 타입으로 찾고, 없으면 경로로 로드
            Image itemSprite = SpriteManager.getSprite(item.getType());
            if (itemSprite == null && item.getSpritePath() != null) {
                itemSprite = SpriteManager.getSpriteByPath(item.getSpritePath());
            }
            
            if (itemSprite != null) {
                // 슬롯 내부에 아이템 이미지 그리기 (약간의 패딩)
                g.drawImage(itemSprite, x + 3, y + 3, slotSize - 6, slotSize - 6, this);
            } else {
                // Fallback - 아이템 타입 텍스트 표시
                g.setColor(Color.ORANGE);
                g.fillRect(x + 3, y + 3, slotSize - 6, slotSize - 6);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.PLAIN, 8));
                String displayText = item.getType() != null ? item.getType() : "ITEM";
                g.drawString(displayText.substring(0, Math.min(4, displayText.length())), x + 5, y + 18);
            }
            
            // 디버깅용: 슬롯 경계선 그리기 (임시)
            g.setColor(Color.RED);
            g.drawRect(x, y, slotSize, slotSize);
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

    public boolean isInventoryVisible() {
        return isVisible;
    }

    public void updateInventory() {
        repaint();
    }

    public void setMesos(int mesos) {
        this.mesos = mesos;
        repaint();
    }

    private void drawMesos(Graphics g) {
        // 메소 아이콘 옆에 금액 표시
        // item_interface.png에서 메소 아이콘은 왼쪽 하단에 위치
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 12));
        
        // 메소 금액 표시 위치 (메소 아이콘 오른쪽)
        String mesosText = String.format("%,d", mesos); // 천 단위 콤마
        g.drawString(mesosText, 40, 253); // 메소 아이콘 오른쪽
    }
}
