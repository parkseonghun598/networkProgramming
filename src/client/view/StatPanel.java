package client.view;

import common.player.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 스텟 창 UI 패널
 * 플레이어의 능력치를 표시하는 패널
 */
public class StatPanel extends JPanel {
    private boolean isVisible = false;
    private Point initialClick; // 드래그 시작 위치
    private Player currentPlayer;
    
    // 패널 크기
    private static final int PANEL_WIDTH = 250;
    private static final int PANEL_HEIGHT = 350;
    
    public StatPanel() {
        setLayout(null);
        setOpaque(false);
        setBounds(100, 100, PANEL_WIDTH, PANEL_HEIGHT);
        
        // 드래그 기능 추가
        addDragListeners();
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
        
        // 배경 그리기
        g.setColor(new Color(50, 50, 50, 230));
        g.fillRect(0, 0, PANEL_WIDTH, PANEL_HEIGHT);
        
        // 테두리
        g.setColor(new Color(150, 150, 150));
        g.drawRect(0, 0, PANEL_WIDTH - 1, PANEL_HEIGHT - 1);
        
        // 제목
        g.setColor(Color.WHITE);
        g.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        g.drawString("능력치", 10, 30);
        
        if (currentPlayer == null) {
            g.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
            g.setColor(Color.GRAY);
            g.drawString("플레이어 정보 없음", 10, 60);
            return;
        }
        
        // 스텟 계산
        PlayerStats stats = calculateStats(currentPlayer);
        
        // 스텟 표시
        g.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        g.setColor(Color.WHITE);
        
        int yPos = 60;
        int lineHeight = 25;
        
        // 기본 스텟
        drawStatLine(g, "STR (힘)", stats.getStr(), 10, yPos);
        yPos += lineHeight;
        drawStatLine(g, "DEX (민첩)", stats.getDex(), 10, yPos);
        yPos += lineHeight;
        drawStatLine(g, "INT (지능)", stats.getInt(), 10, yPos);
        yPos += lineHeight;
        drawStatLine(g, "LUK (운)", stats.getLuk(), 10, yPos);
        yPos += lineHeight + 10;
        
        // 추가 스텟
        g.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        drawStatLine(g, "HP", stats.getHp(), 10, yPos);
        yPos += lineHeight;
        drawStatLine(g, "MP", stats.getMp(), 10, yPos);
        yPos += lineHeight + 10;
        
        drawStatLine(g, "공격력", stats.getAttack(), 10, yPos);
        yPos += lineHeight;
        drawStatLine(g, "방어력", stats.getDefense(), 10, yPos);
        
        // 장비 보너스 표시 (있는 경우)
        if (stats.hasEquipmentBonus()) {
            yPos += lineHeight + 10;
            g.setFont(new Font("맑은 고딕", Font.PLAIN, 10));
            g.setColor(new Color(150, 200, 255));
            g.drawString("(장비 보너스 포함)", 10, yPos);
        }
    }
    
    private void drawStatLine(Graphics g, String label, int value, int x, int y) {
        g.setColor(Color.WHITE);
        g.drawString(label + ":", x, y);
        g.setColor(new Color(255, 215, 0)); // 금색
        g.drawString(String.valueOf(value), x + 120, y);
    }
    
    /**
     * 플레이어의 스텟을 계산합니다 (기본 스텟 + 장비 보너스)
     */
    private PlayerStats calculateStats(Player player) {
        // 기본 스텟
        int baseStr = 10;
        int baseDex = 10;
        int baseInt = 10;
        int baseLuk = 10;
        int baseHp = 100;
        int baseMp = 50;
        int baseAttack = 10;
        int baseDefense = 5;
        
        // 장비 보너스 계산
        EquipmentStatBonus bonus = getEquipmentBonus(player);
        
        return new PlayerStats(
            baseStr + bonus.str,
            baseDex + bonus.dex,
            baseInt + bonus.intelligence,
            baseLuk + bonus.luk,
            baseHp + bonus.hp,
            baseMp + bonus.mp,
            baseAttack + bonus.attack,
            baseDefense + bonus.defense
        );
    }
    
    /**
     * 착용한 장비의 스텟 보너스를 계산합니다
     */
    private EquipmentStatBonus getEquipmentBonus(Player player) {
        EquipmentStatBonus bonus = new EquipmentStatBonus();
        
        // 무기 보너스
        addItemBonus(bonus, player.getEquippedWeapon());
        // 모자 보너스
        addItemBonus(bonus, player.getEquippedHat());
        // 상의 보너스
        addItemBonus(bonus, player.getEquippedTop());
        // 하의 보너스
        addItemBonus(bonus, player.getEquippedBottom());
        // 장갑 보너스
        addItemBonus(bonus, player.getEquippedGloves());
        // 신발 보너스
        addItemBonus(bonus, player.getEquippedShoes());
        
        return bonus;
    }
    
    private void addItemBonus(EquipmentStatBonus bonus, String itemType) {
        if (itemType == null || itemType.equals("none")) {
            return;
        }
        
        // 장비별 스텟 보너스 정의
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
    
    /**
     * 플레이어 스텟 데이터 클래스
     */
    private static class PlayerStats {
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
        
        public boolean hasEquipmentBonus() {
            return str > 10 || dex > 10 || intelligence > 10 || luk > 10 || 
                   hp > 100 || mp > 50 || attack > 10 || defense > 5;
        }
    }
    
    /**
     * 장비 스텟 보너스 클래스
     */
    private static class EquipmentStatBonus {
        int str = 0;
        int dex = 0;
        int intelligence = 0;
        int luk = 0;
        int hp = 0;
        int mp = 0;
        int attack = 0;
        int defense = 0;
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
    
    public boolean isStatPanelVisible() {
        return isVisible;
    }
}

