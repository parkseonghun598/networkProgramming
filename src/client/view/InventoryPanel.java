package client.view;

import client.util.SpriteManager;
import common.inventory.Inventory;
import common.item.Item;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class InventoryPanel extends JPanel {
    private BufferedImage backgroundImage;
    private Inventory inventory; // 로컬 인벤토리 (하위 호환성)
    private common.player.Player player; // 플레이어 객체 (플레이어 인벤토리 사용)
    private boolean isVisible = false;
    private int mesos = 0; // 현재 메소
    private Point initialClick; // 드래그 시작 위치
    
    // 콜백 함수
    private InventoryPanelCallback callback;
    
    public interface InventoryPanelCallback {
        void onItemEquip(Item item);
    }

    public InventoryPanel(Inventory inventory) {
        this.inventory = inventory;
        this.player = null;
        setLayout(null);
        setOpaque(false);
        setFocusable(false); // 키 입력을 가로채지 않도록 설정
        setBounds(200, 50, 172, 262); // 인벤토리 크기에 맞춤

        // Load inventory background
        try {
            backgroundImage = ImageIO.read(new File("../img/item_interface.png"));
            System.out.println("Loaded inventory interface");
        } catch (IOException e) {
            System.err.println("Failed to load inventory interface: " + e.getMessage());
        }

        // 드래그 및 더블클릭 기능 추가 (통합 리스너)
        addDragAndClickListeners();
    }
    
    public void setCallback(InventoryPanelCallback callback) {
        this.callback = callback;
    }
    
    public void setPlayer(common.player.Player player) {
        this.player = player;
        repaint();
    }
    
    private Item getItemAtPosition(int x, int y) {
        int cols = 4;
        int slotSize = 31;
        int startX = 6;
        int startY = 8;
        int spacingX = 6;
        int spacingY = 6;

        // 플레이어 인벤토리를 우선 사용, 없으면 로컬 인벤토리 사용
        java.util.List<Item> items = (player != null && player.getInventory() != null) 
            ? player.getInventory() 
            : inventory.getItems();
        
        for (int i = 0; i < items.size() && i < 24; i++) {
            Item item = items.get(i);
            int row = i / cols;
            int col = i % cols;
            
            int itemX = startX + col * (slotSize + spacingX);
            int itemY = startY + row * (slotSize + spacingY);
            
            // 클릭한 위치가 이 아이템 슬롯 안에 있는지 확인
            if (x >= itemX && x <= itemX + slotSize &&
                y >= itemY && y <= itemY + slotSize) {
                return item;
            }
        }
        return null;
    }

    private void addDragAndClickListeners() {
        MouseAdapter dragAndClickListener = new MouseAdapter() {
            private long lastClickTime = 0;
            private Point lastClickPoint = null;
            private static final int DOUBLE_CLICK_DELAY = 300; // 밀리초
            private static final int DOUBLE_CLICK_DISTANCE = 5; // 픽셀

            @Override
            public void mousePressed(MouseEvent e) {
                if (isVisible) {
                    initialClick = e.getPoint();
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (isVisible && initialClick != null) {
                    // 현재 패널 위치 가져오기
                    int thisX = getLocation().x;
                    int thisY = getLocation().y;

                    // 마우스 이동 거리 계산
                    int xMoved = e.getX() - initialClick.x;
                    int yMoved = e.getY() - initialClick.y;

                    // 새 위치 계산
                    int newX = thisX + xMoved;
                    int newY = thisY + yMoved;

                    // 화면 밖으로 나가지 않도록 제한 (부모 컨테이너 기준)
                    Container parent = getParent();
                    if (parent != null) {
                        newX = Math.max(0, Math.min(newX, parent.getWidth() - getWidth()));
                        newY = Math.max(0, Math.min(newY, parent.getHeight() - getHeight()));
                    }

                    // 패널 위치 업데이트
                    setLocation(newX, newY);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                initialClick = null;
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!isVisible) return;
                
                // 더블클릭 감지
                long currentTime = System.currentTimeMillis();
                Point currentPoint = e.getPoint();
                
                if (lastClickPoint != null && 
                    currentTime - lastClickTime < DOUBLE_CLICK_DELAY &&
                    Math.abs(currentPoint.x - lastClickPoint.x) < DOUBLE_CLICK_DISTANCE &&
                    Math.abs(currentPoint.y - lastClickPoint.y) < DOUBLE_CLICK_DISTANCE) {
                    
                    // 더블클릭으로 아이템 착용
                    Item clickedItem = getItemAtPosition(e.getX(), e.getY());
                    if (clickedItem != null && callback != null) {
                        System.out.println("Double-clicked item: " + clickedItem.getType());
                        callback.onItemEquip(clickedItem);
                    }
                    
                    lastClickTime = 0;
                    lastClickPoint = null;
                } else {
                    lastClickTime = currentTime;
                    lastClickPoint = currentPoint;
                }
            }
        };

        addMouseListener(dragAndClickListener);
        addMouseMotionListener(dragAndClickListener);
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

        // 플레이어 인벤토리를 우선 사용, 없으면 로컬 인벤토리 사용
        java.util.List<Item> items = (player != null && player.getInventory() != null) 
            ? player.getInventory() 
            : inventory.getItems();
        
        // 인벤토리가 비어있으면 그리기 중단
        if (items == null || items.isEmpty()) {
            return;
        }
        
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
    
    public void updatePlayer(common.player.Player player) {
        this.player = player;
        if (player != null) {
            this.mesos = player.getMesos();
        }
        repaint();
    }

    public void setMesos(int mesos) {
        this.mesos = mesos;
        repaint();
    }

    private void drawMesos(Graphics g) {
        // 메소 금액 표시 (인벤토리 창 하단)
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        
        String mesosText = String.format("%,d", mesos); // 천 단위 콤마
        int textX = 100; // 텍스트 X 위치 (오른쪽으로 이동)
        int textY = 255; // 텍스트 Y 위치 
        
        g.drawString(mesosText, textX, textY);
    }
}
