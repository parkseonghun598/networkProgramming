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
    }

    private void drawInventoryItems(Graphics g) {
        // 4x6 그리드 (4열 6행)
        int cols = 4;
        int slotSize = 36; // 각 슬롯 크기
        int startX = 12; // 시작 X 위치
        int startY = 38; // 시작 Y 위치 (메소 표시 아래)
        int spacing = 2; // 슬롯 간격

        java.util.List<Item> items = inventory.getItems();
        
        for (int i = 0; i < items.size() && i < 24; i++) {
            Item item = items.get(i);
            int row = i / cols;
            int col = i % cols;
            
            int x = startX + col * (slotSize + spacing);
            int y = startY + row * (slotSize + spacing);

            // Draw item sprite
            Image itemSprite = SpriteManager.getSprite(item.getType());
            if (itemSprite != null) {
                g.drawImage(itemSprite, x + 2, y + 2, slotSize - 4, slotSize - 4, this);
            } else {
                // Fallback - 아이템 타입 텍스트 표시
                g.setColor(Color.ORANGE);
                g.fillRect(x + 2, y + 2, slotSize - 4, slotSize - 4);
                g.setColor(Color.WHITE);
                g.setFont(new Font("Arial", Font.PLAIN, 8));
                g.drawString(item.getType().substring(0, Math.min(4, item.getType().length())), x + 4, y + 20);
            }
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
}
