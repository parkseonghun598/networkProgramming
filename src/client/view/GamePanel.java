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
    private static final int GROUND_Y = 500;

    public GamePanel() {
        this.players = new CopyOnWriteArrayList<>();
        this.monsters = new CopyOnWriteArrayList<>();
        this.skills = new CopyOnWriteArrayList<>();
        this.portals = new CopyOnWriteArrayList<>();

        setPreferredSize(new Dimension(800, 600));
        setFocusable(true);
        addKeyListener(this);

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

    private void update() {
        Player myPlayer = getMyPlayer();
        if (myPlayer == null) return;

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
        if (myPlayerId == null) return null;
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
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        GameRenderer.render(g, background, errorMessage, monsters, skills, players, portals,
                           myPlayerId, getWidth(), getHeight());
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {
        if (myPlayerId == null) return;
        PlayerInputHandler.handleKeyPress(e, getMyPlayer(), this);
        repaint();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (myPlayerId == null) return;
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

    @Override
    public void useSkill(String skillType) {
        Player myPlayer = getMyPlayer();
        if (myPlayer == null) return;

        String direction = myPlayer.getDirection().getValue();

        String skillMsg = String.format(
            "{\"type\":\"SKILL_USE\",\"payload\":{\"skillType\":\"%s\",\"direction\":\"%s\"}}",
            skillType, direction
        );
        networkHandler.sendMessage(skillMsg);
        System.out.println("Skill used: " + skillType + " in direction: " + direction);
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
        if (myPlayer == null) return;

        String updateMsg = String.format(
            "{\"type\":\"PLAYER_UPDATE\",\"payload\":{\"x\":%d,\"y\":%d,\"state\":\"%s\",\"direction\":\"%s\"}}",
            myPlayer.getX(), myPlayer.getY(), myPlayer.getState(),
            myPlayer.getDirection().getValue()
        );
        networkHandler.sendMessage(updateMsg);
    }}
