package client.view;

import client.controller.NetworkHandler;
import client.controller.PlayerInputHandler;
import client.controller.NpcDialogHandler;
import client.util.GameStateParser;
import client.util.SpriteManager;
import client.util.CharacterAnimator;
import common.inventory.Inventory;
import common.monster.Monster;
import common.player.Player;
import common.skills.Skill;
import common.user.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class GamePanel extends JPanel implements KeyListener, MouseListener, PlayerInputHandler.PlayerMovementCallback {

    private NetworkHandler networkHandler;
    private NpcDialogHandler npcDialogHandler;
    private InventoryPanel inventoryPanel;
    private EquipPanel equipPanel;
    private Inventory inventory;
    private String errorMessage;
    private String myPlayerId;
    private User currentUser;

    private final List<Player> players;
    private final List<Monster> monsters;
    private final List<Skill> skills;
    private final List<common.map.Portal> portals;
    private final List<common.npc.NPC> npcs;
    private final List<common.item.Item> items;
    private String currentBackgroundImagePath;
    private BufferedImage background;
    
    // 플레이어별 애니메이터 관리
    private final Map<String, CharacterAnimator> playerAnimators;

    private double velocityY = 0;
    private boolean isJumping = false;
    private static final double GRAVITY = 0.5;
    private static final double JUMP_STRENGTH = -12.0;
    private static final int GROUND_Y = 475;

    private JTextArea chatArea;
    private JTextField chatInput;
    private JScrollPane chatScrollPane;

    public GamePanel(User user) {
        this.currentUser = user;
        this.players = new CopyOnWriteArrayList<>();
        this.monsters = new CopyOnWriteArrayList<>();
        this.skills = new CopyOnWriteArrayList<>();
        this.portals = new CopyOnWriteArrayList<>();
        this.npcs = new CopyOnWriteArrayList<>();
        this.items = new CopyOnWriteArrayList<>();
        this.playerAnimators = new ConcurrentHashMap<>();

        setPreferredSize(new Dimension(800, 600));
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        setLayout(null); // Use absolute positioning

        // Initialize Chat UI
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Arial", Font.PLAIN, 12));
        chatArea.setBackground(new Color(0, 0, 0, 150)); // Semi-transparent black
        chatArea.setForeground(Color.WHITE);

        chatScrollPane = new JScrollPane(chatArea);
        chatScrollPane.setBounds(580, 10, 200, 150); // Top-right corner
        chatScrollPane.setBorder(BorderFactory.createEmptyBorder());
        chatScrollPane.getViewport().setOpaque(false);
        chatScrollPane.setOpaque(false);
        chatScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(chatScrollPane);

        chatInput = new JTextField();
        chatInput.setBounds(580, 165, 200, 25);
        chatInput.setVisible(false); // Hidden by default
        add(chatInput);

        chatInput.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    String message = chatInput.getText().trim();
                    if (!message.isEmpty()) {
                        sendChatMessage(message);
                        chatInput.setText("");
                    }
                    chatInput.setVisible(false);
                    GamePanel.this.requestFocusInWindow(); // Return focus to game
                }
            }
        });

        // Initialize inventory
        inventory = new Inventory();
        inventoryPanel = new InventoryPanel(inventory);
        inventoryPanel.hide();
        
        // 인벤토리 아이템 착용 콜백 설정
        inventoryPanel.setCallback(item -> {
            equipItem(item);
        });
        
        add(inventoryPanel);
        
        // Initialize equip panel
        equipPanel = new EquipPanel(slot -> {
            unequipItem(slot);
        });
        equipPanel.hide();
        add(equipPanel);

        try {
            SpriteManager.loadSprites();
            networkHandler = new NetworkHandler(this, "localhost", 12345);
            npcDialogHandler = new NpcDialogHandler(this, networkHandler, this::getMyPlayer);
            new Thread(networkHandler).start();
        } catch (Exception e) {
            showError("Failed to connect to the server or load assets.");
        }

        // Client-side game loop
        Timer timer = new Timer(16, e -> update()); // Approx 60 FPS
        timer.start();
    }

    private void sendChatMessage(String message) {
        String jsonMsg = String.format("{\"type\":\"CHAT\",\"payload\":{\"message\":\"%s\"}}", message);
        networkHandler.sendMessage(jsonMsg);
    }

    public void addChatMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            chatArea.append(message + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength()); // Auto-scroll

            // Limit history to 10 lines (approximate, or just let it scroll)
            // User asked for "recent 10 items visible", scrollable history implies keeping
            // more.
            // I'll keep it simple for now.
        });
    }

    private void update() {
        Player myPlayer = getMyPlayer();
        if (myPlayer == null)
            return;

        if (isJumping) {
            velocityY += GRAVITY;
            int newY = myPlayer.getY() + (int) velocityY;

            if (newY >= GROUND_Y) {
                newY = GROUND_Y;
                isJumping = false;
                velocityY = 0;
                // 점프가 끝났으므로 idle 상태로 변경
                myPlayer.setState("idle");
            }
            myPlayer.setY(newY);
            sendPlayerUpdate();
        }
        repaint();
    }

    private Player getMyPlayer() {
        if (myPlayerId == null)
            return null;
        return players.stream().filter(p -> p.getId().equals(myPlayerId)).findFirst().orElse(null);
    }

    public void setMyPlayerId(String id) {
        this.myPlayerId = id;
    }

    public String getUsername() {
        return currentUser != null ? currentUser.getUsername() : null;
    }

    public String getCharacterType() {
        return currentUser != null ? currentUser.getCharacterType() : "defaultWarrior";
    }

    public void updateGameState(String jsonState) {
        GameStateParser.parseAndUpdate(jsonState, players, monsters, skills, portals, npcs, items, myPlayerId);
        String newBgPath = GameStateParser.parseBackgroundImagePath(jsonState);
        if (newBgPath != null) {
            setBackgroundImage(newBgPath);
        }
        
        // 플레이어 애니메이터 업데이트
        updatePlayerAnimators();
        
        // 인벤토리와 메소 업데이트
        if (inventoryPanel != null) {
            Player myPlayer = getMyPlayer();
            if (myPlayer != null) {
                // 플레이어 인벤토리로 업데이트
                inventoryPanel.updatePlayer(myPlayer);
                
                // 장비 창 업데이트
                if (equipPanel != null) {
                    equipPanel.updatePlayer(myPlayer);
                }
            }
        }
        
        this.errorMessage = null;
    }
    
    public void updateMesos(int mesos) {
        // 메소 업데이트 (플레이어 객체를 통해 처리되므로 여기서는 인벤토리 패널만 업데이트)
        if (inventoryPanel != null) {
            inventoryPanel.setMesos(mesos);
        }
    }
    
    private void updatePlayerAnimators() {
        long currentTime = System.currentTimeMillis();
        
        for (Player player : players) {
            String playerId = player.getId();
            String characterFolderPath = player.getCharacterFolderPath(); // 착용 아이템 조합 경로
            
            // 애니메이터가 없으면 생성
            if (!playerAnimators.containsKey(playerId)) {
                CharacterAnimator animator = new CharacterAnimator(characterFolderPath);
                playerAnimators.put(playerId, animator);
                System.out.println("Created animator for player " + playerId + " with appearance " + characterFolderPath);
            } else {
                // 착용 아이템이 변경되었는지 확인하고 애니메이터 업데이트
                CharacterAnimator existingAnimator = playerAnimators.get(playerId);
                if (!existingAnimator.getCharacterFolderPath().equals(characterFolderPath)) {
                    existingAnimator.updateCharacterAppearance(characterFolderPath);
                }
            }
            
            // 플레이어 상태에 따라 애니메이션 설정
            CharacterAnimator animator = playerAnimators.get(playerId);
            
            // 내 플레이어이고 공격 애니메이션 중이면 attack 유지
            if (playerId.equals(myPlayerId) && 
                currentTime - lastAttackTime < ATTACK_ANIMATION_DURATION) {
                animator.setState("attack");
                continue;
            }
            
            String state = player.getState();
            
            // 플레이어 상태에 따라 애니메이션 상태 설정
            if (state == null || state.isEmpty()) {
                animator.setState("idle");
            } else if (state.equals("jump")) {
                animator.setState("jump");
            } else if (state.equals("move")) {
                animator.setState("move");
            } else {
                animator.setState("idle");
            }
        }
        
        // 삭제된 플레이어의 애니메이터 제거
        playerAnimators.keySet().removeIf(playerId -> 
            players.stream().noneMatch(p -> p.getId().equals(playerId))
        );
    }

    public void setBackgroundImage(String path) {
        if (path != null && !path.equals(currentBackgroundImagePath)) {
            try {
                this.background = ImageIO.read(new File(path));
                this.currentBackgroundImagePath = path;
                repaint();
            } catch (IOException e) {
                System.err.println("Failed to load background image: " + path);
            }
        }
    }

    public void showError(String message) {
        this.errorMessage = message;
        repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!chatInput.isVisible()) {
                chatInput.setVisible(true);
                chatInput.requestFocusInWindow();
            }
            return;
        }

        // I 키: 인벤토리 토글
        if (e.getKeyCode() == KeyEvent.VK_I) {
            inventoryPanel.toggleVisibility();
            repaint();
            return;
        }
        
        // E 키: 장비 창 토글
        if (e.getKeyCode() == KeyEvent.VK_E) {
            equipPanel.toggleVisibility();
            repaint();
            return;
        }

        // Z 키: 아이템 습득
        if (e.getKeyCode() == KeyEvent.VK_Z) {
            pickupItem();
            return;
        }

        if (myPlayerId == null)
            return;
        PlayerInputHandler.handleKeyPress(e, getMyPlayer(), this);
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (myPlayerId == null)
            return;
        PlayerInputHandler.handleKeyRelease(e, getMyPlayer(), this);
    }

    @Override
    public boolean canJump() {
        return !isJumping;
    }

    @Override
    public void initiateJump() {
        isJumping = true;
        velocityY = JUMP_STRENGTH;
    }

    private final java.util.Map<String, Long> skillCooldowns = new java.util.concurrent.ConcurrentHashMap<>();
    private final java.util.Map<String, Long> skillCooldownDurations = new java.util.HashMap<String, Long>() {{
        put("skill1", 2000L);
        put("skill2", 3000L);
        put("skill3", 5000L);
        put("skill4", 4000L);
    }};
    
    private long lastAttackTime = 0;
    private static final long ATTACK_ANIMATION_DURATION = 400; // 공격 애니메이션 지속 시간 (밀리초)

    @Override
    public void useSkill(String skillType) {
        Player myPlayer = getMyPlayer();
        if (myPlayer == null)
            return;

        long currentTime = System.currentTimeMillis();
        long cooldownDuration = skillCooldownDurations.getOrDefault(skillType, 2000L);

        if (skillCooldowns.containsKey(skillType)) {
            long lastUsed = skillCooldowns.get(skillType);
            if (currentTime - lastUsed < cooldownDuration) {
                System.out.println("Skill " + skillType + " is on cooldown.");
                return;
            }
        }

        skillCooldowns.put(skillType, currentTime);

        String direction = myPlayer.getDirection().getValue();

        String skillMsg = String.format(
                "{\"type\":\"SKILL_USE\",\"payload\":{\"skillType\":\"%s\",\"direction\":\"%s\"}}",
                skillType, direction);
        networkHandler.sendMessage(skillMsg);
        System.out.println("Skill used: " + skillType + " in direction: " + direction);
        
        // 공격 애니메이션 트리거
        lastAttackTime = currentTime;
        CharacterAnimator myAnimator = playerAnimators.get(myPlayerId);
        if (myAnimator != null) {
            myAnimator.setState("attack");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        GameRenderer.render(g, background, errorMessage, monsters, skills, players, portals, npcs, items,
                myPlayerId, playerAnimators, getWidth(), getHeight());

        // Render Cooldown UI
        renderCooldownUI(g);
    }

    private void renderCooldownUI(Graphics g) {
        int startX = 10;
        int y = getHeight() - 60;
        int size = 50;
        int spacing = 60;

        // Skill1 (Q)
        renderSingleSkillCooldown(g, "skill1", "Q", startX, y, size);

        // Skill2 (W)
        renderSingleSkillCooldown(g, "skill2", "W", startX + spacing, y, size);

        // Skill4 (E)
        renderSingleSkillCooldown(g, "skill4", "E", startX + spacing * 2, y, size);

        // Skill3 (R)
        renderSingleSkillCooldown(g, "skill3", "R", startX + spacing * 3, y, size);
    }

    private void renderSingleSkillCooldown(Graphics g, String skillType, String keyBind, int x, int y, int size) {
        g.setColor(Color.GRAY);
        g.fillRect(x, y, size, size);
        g.setColor(Color.WHITE);
        g.drawRect(x, y, size, size);
        g.drawString(keyBind, x + 5, y + 15);

        if (skillCooldowns.containsKey(skillType)) {
            long lastUsed = skillCooldowns.get(skillType);
            long currentTime = System.currentTimeMillis();
            long elapsed = currentTime - lastUsed;
            long cooldown = skillCooldownDurations.getOrDefault(skillType, 2000L);

            if (elapsed < cooldown) {
                int arc = (int) (360 * (1.0 - (double) elapsed / cooldown));
                g.setColor(new Color(0, 0, 0, 150));
                g.fillArc(x, y, size, size, 90, arc);

                g.setColor(Color.WHITE);
                String timeLeft = String.format("%.1f", (cooldown - elapsed) / 1000.0);
                g.drawString(timeLeft, x + 15, y + 30);
            }
        }
    }

    @Override
    public void usePortal() {
        String msg = "{\"type\":\"USE_PORTAL\"}";
        networkHandler.sendMessage(msg);
    }

    @Override
    public void sendUpdate() {
        sendPlayerUpdate();
    }

    private void sendPlayerUpdate() {
        Player myPlayer = getMyPlayer();
        if (myPlayer == null)
            return;

        String updateMsg = String.format(
                "{\"type\":\"PLAYER_UPDATE\",\"payload\":{\"x\":%d,\"y\":%d,\"state\":\"%s\",\"direction\":\"%s\"}}",
                myPlayer.getX(), myPlayer.getY(), myPlayer.getState(),
                myPlayer.getDirection().getValue());
        networkHandler.sendMessage(updateMsg);
    }

    // MouseListener implementation
    @Override
    public void mouseClicked(MouseEvent e) {
        npcDialogHandler.handleNpcClick(e.getX(), e.getY(), npcs);
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void pickupItem() {
        Player myPlayer = getMyPlayer();
        if (myPlayer == null) return;

        // 인벤토리가 가득 찼는지 확인
        if (inventory.isFull()) {
            System.out.println("Inventory is full!");
            return;
        }

        // 근처 아이템 찾기 (픽업 범위: 80픽셀)
        final int PICKUP_RANGE = 80;
        for (common.item.Item item : items) {
            int dx = item.getX() - myPlayer.getX();
            int dy = item.getY() - myPlayer.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance <= PICKUP_RANGE) {
                // 서버에 아이템 픽업 요청
                String msg = String.format(
                    "{\"type\":\"PICKUP_ITEM\",\"payload\":{\"itemId\":\"%s\"}}",
                    item.getId()
                );
                networkHandler.sendMessage(msg);
                System.out.println("Requested pickup for item: " + item.getId());
                break; // 한 번에 하나만 습득
            }
        }
    }

    @Override
    public void toggleInventory() {
        inventoryPanel.toggleVisibility();
        repaint();
    }

    public void addItemToInventory(common.item.Item item) {
        if (inventory.addItem(item)) {
            System.out.println("Added item to inventory: " + item.getName());
            inventoryPanel.updateInventory();
        } else {
            System.out.println("Failed to add item: inventory full");
        }
    }
    
    private void equipItem(common.item.Item item) {
        if (item == null || networkHandler == null) {
            return;
        }
        
        // 아이템 타입을 장비 슬롯으로 매핑
        common.util.ItemSlotMapper.EquipmentSlot slot = 
            common.util.ItemSlotMapper.getEquipmentSlot(item.getType());
        
        if (slot == null) {
            System.out.println("This item cannot be equipped: " + item.getType());
            return;
        }
        
        // 서버에 착용 요청 전송
        String msg = String.format(
            "{\"type\":\"EQUIP_ITEM\",\"payload\":{\"itemId\":\"%s\",\"slot\":\"%s\"}}",
            item.getId(), slot.name()
        );
        networkHandler.sendMessage(msg);
        System.out.println("Requested equip item: " + item.getType() + " to slot: " + slot.name());
    }
    
    private void unequipItem(common.util.ItemSlotMapper.EquipmentSlot slot) {
        if (slot == null || networkHandler == null) {
            return;
        }
        
        // 서버에 해제 요청 전송
        String msg = String.format(
            "{\"type\":\"UNEQUIP_ITEM\",\"payload\":{\"slot\":\"%s\"}}",
            slot.name()
        );
        networkHandler.sendMessage(msg);
        System.out.println("Requested unequip from slot: " + slot.name());
    }
}
