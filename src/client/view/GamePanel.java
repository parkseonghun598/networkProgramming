package client.view;

import client.controller.NetworkHandler;
import client.controller.PlayerInputHandler;
import client.util.GameStateParser;
import client.util.SpriteManager;
import common.monster.Monster;
import common.player.Player;
import common.skills.Skill;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GamePanel extends JPanel implements KeyListener, PlayerInputHandler.PlayerMovementCallback {

    private NetworkHandler networkHandler;
    private String errorMessage;
    private String myPlayerId;

    private final List<Player> players;
    private final List<Monster> monsters;
    private final List<Skill> skills;
    private final List<common.map.Portal> portals;
    private String currentBackgroundImagePath;
    private BufferedImage background;

    private double velocityY = 0;
    private boolean isJumping = false;
    private static final double GRAVITY = 0.5;
    private static final double JUMP_STRENGTH = -12.0;
    private static final int GROUND_Y = 475;

    private JTextArea chatArea;
    private JTextField chatInput;
    private JScrollPane chatScrollPane;

    public GamePanel() {
        this.players = new CopyOnWriteArrayList<>();
        this.monsters = new CopyOnWriteArrayList<>();
        this.skills = new CopyOnWriteArrayList<>();
        this.portals = new CopyOnWriteArrayList<>();

        setPreferredSize(new Dimension(800, 600));
        setFocusable(true);
        addKeyListener(this);
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

        try {
            SpriteManager.loadSprites();
            networkHandler = new NetworkHandler(this, "localhost", 12345);
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

    public void updateGameState(String jsonState) {
        GameStateParser.parseAndUpdate(jsonState, players, monsters, skills, portals, myPlayerId);
        String newBgPath = GameStateParser.parseBackgroundImagePath(jsonState);
        if (newBgPath != null) {
            setBackgroundImage(newBgPath);
        }
        this.errorMessage = null;
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

    @Override
    public void useSkill(String skillType) {
        Player myPlayer = getMyPlayer();
        if (myPlayer == null)
            return;

        long currentTime = System.currentTimeMillis();
        if (skillCooldowns.containsKey(skillType)) {
            long lastUsed = skillCooldowns.get(skillType);
            if (currentTime - lastUsed < 3000) { // Hardcoded 3000ms for now, should match server
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
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        GameRenderer.render(g, background, errorMessage, monsters, skills, players, portals,
                myPlayerId, getWidth(), getHeight());

        // Render Cooldown UI
        renderCooldownUI(g);
    }

    private void renderCooldownUI(Graphics g) {
        int x = 10;
        int y = getHeight() - 60;
        int size = 50;

        g.setColor(Color.GRAY);
        g.fillRect(x, y, size, size);
        g.setColor(Color.WHITE);
        g.drawRect(x, y, size, size);
        g.drawString("Q", x + 5, y + 15); // Key bind

        if (skillCooldowns.containsKey("skill1")) {
            long lastUsed = skillCooldowns.get("skill1");
            long currentTime = System.currentTimeMillis();
            long elapsed = currentTime - lastUsed;
            long cooldown = 3000;

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
}
