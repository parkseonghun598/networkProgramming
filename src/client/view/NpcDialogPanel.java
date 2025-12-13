package client.view;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class NpcDialogPanel extends JPanel {
    private BufferedImage npcImage;
    private String[] dialogMessages;
    private int currentDialogIndex = 0;
    private JTextArea messageArea;
    private JButton actionButton;
    private DialogCallback callback;

    public interface DialogCallback {
        void onDialogComplete();
        void onDialogCancel();
    }

    public NpcDialogPanel(String npcImagePath, String[] messages, DialogCallback callback) {
        this.dialogMessages = messages;
        this.callback = callback;

        setLayout(null);
        setPreferredSize(new Dimension(700, 300));
        setBackground(new Color(240, 245, 255));
        setFocusable(false); // 키 입력을 가로채지 않도록 설정
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 150), 3),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Load NPC image
        try {
            npcImage = ImageIO.read(new File(npcImagePath));
        } catch (IOException e) {
            System.err.println("Failed to load NPC image: " + npcImagePath);
        }

        // NPC Name Label
        JLabel nameLabel = new JLabel("주먹펴고 일어서서", SwingConstants.CENTER);
        nameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        nameLabel.setForeground(new Color(50, 50, 100));
        nameLabel.setBounds(20, 200, 150, 30);
        add(nameLabel);

        // Message Area
        messageArea = new JTextArea();
        messageArea.setFont(new Font("맑은 고딕", Font.PLAIN, 14));
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setEditable(false);
        messageArea.setOpaque(false);
        messageArea.setBounds(200, 30, 450, 180);
        messageArea.setText(dialogMessages[0]);
        add(messageArea);

        // Action Button (다음/예)
        actionButton = new JButton("다음 ▶");
        actionButton.setBounds(560, 230, 100, 40);
        actionButton.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        actionButton.setBackground(new Color(100, 200, 100));
        actionButton.setForeground(Color.WHITE);
        actionButton.setFocusPainted(false);
        actionButton.setBorder(BorderFactory.createRaisedBevelBorder());
        actionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        actionButton.addActionListener(e -> handleAction());
        add(actionButton);

        // 대화 그만하기 버튼
        JButton cancelButton = new JButton("대화 그만하기");
        cancelButton.setBounds(20, 240, 140, 30);
        cancelButton.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        cancelButton.setBackground(new Color(200, 200, 200));
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> {
            if (callback != null) {
                callback.onDialogCancel();
            }
        });
        add(cancelButton);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw NPC image on the left
        if (npcImage != null) {
            g.drawImage(npcImage, 30, 30, 140, 160, this);
        } else {
            // Fallback rectangle
            g.setColor(Color.GRAY);
            g.fillRect(30, 30, 140, 160);
        }

        // Draw border around NPC image
        g.setColor(new Color(100, 100, 150));
        g.drawRect(29, 29, 141, 161);
    }

    private void handleAction() {
        currentDialogIndex++;
        
        if (currentDialogIndex < dialogMessages.length) {
            // Show next message
            messageArea.setText(dialogMessages[currentDialogIndex]);
            
            // Change button to "예" on the last message
            if (currentDialogIndex == dialogMessages.length - 1) {
                actionButton.setText("예");
                actionButton.setBackground(new Color(100, 150, 255));
            }
        } else {
            // Dialog complete
            if (callback != null) {
                callback.onDialogComplete();
            }
        }
    }

    public void reset() {
        currentDialogIndex = 0;
        messageArea.setText(dialogMessages[0]);
        actionButton.setText("다음 ▶");
        actionButton.setBackground(new Color(100, 200, 100));
    }
}

