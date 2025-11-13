package client.view;

import client.controller.NetworkHandler;
import client.controller.PlayerInputHandler;
import client.util.GameStateParser;
import client.util.SpriteManager;
import common.monster.Monster;
import common.player.Player;
import common.skills.Skill;

import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static common.ImagePath.*;

public class GamePanel extends JPanel implements KeyListener, PlayerInputHandler.PlayerMovementCallback {

    private NetworkHandler networkHandler;
    private String errorMessage = null;
    private String myPlayerId = null;
    private final List<Player> players = new CopyOnWriteArrayList<>();
    private final List<Monster> monsters = new CopyOnWriteArrayList<>();
    private final List<Skill> skills = new CopyOnWriteArrayList<>();
    private BufferedImage background;
    private double velocityY = 0;
    private boolean isJumping = false;
    private static final double GRAVITY = 0.5;
    private static final double JUMP_STRENGTH = -12.0;
    private static final int GROUND_Y = 500;


    public GamePanel() {
        setPreferredSize(new Dimension(800, 600));
        setFocusable(true);
        addKeyListener(this);

        try {
            background = ImageIO.read(new File(BACKGROUND_IMAGE_PATH));
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
        GameStateParser.parseAndUpdate(jsonState, players, monsters, skills);
        this.errorMessage = null;
    }

    public void showError(String message) {
        this.errorMessage = message;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        GameRenderer.render(g, background, errorMessage, monsters, skills, players,
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

        String direction = myPlayer.getDirection() != null ? myPlayer.getDirection().getValue() : "right";
        String skillMsg = String.format(
            "{\"type\":\"SKILL_USE\",\"payload\":{\"skillType\":\"%s\",\"direction\":\"%s\"}}",
            skillType, direction
        );
        networkHandler.sendMessage(skillMsg);
        System.out.println("Skill used: " + skillType + " in direction: " + direction);
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
            myPlayer.getDirection() != null ? myPlayer.getDirection().getValue() : "right"
        );
        networkHandler.sendMessage(updateMsg);
    }}
