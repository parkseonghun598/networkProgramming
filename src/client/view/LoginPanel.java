package client.view;

import common.user.User;

import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class LoginPanel extends JPanel {
    private JTextField nicknameField;
    private JButton startButton;
    private LoginCallback callback;
    private BufferedImage backgroundImage;

    public interface LoginCallback {
        void onLoginSuccess(User user);
    }

    public LoginPanel(LoginCallback callback) {
        this.callback = callback;

        setPreferredSize(new Dimension(800, 600));
        setLayout(null);

        // Load background image
        try {
            backgroundImage = ImageIO.read(new File("../img/login_background.png"));
        } catch (IOException e) {
            System.err.println("Failed to load login background image: " + e.getMessage());
            e.printStackTrace();
        }

        // Title label with shadow effect
        JLabel titleLabel = new JLabel("Mini MapleStory", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 36));
        titleLabel.setForeground(new Color(255, 255, 255));
        titleLabel.setBounds(200, 180, 400, 50);
        add(titleLabel);

        // Nickname label
        JLabel nicknameLabel = new JLabel("닉네임을 입력하세요", SwingConstants.CENTER);
        nicknameLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        nicknameLabel.setForeground(new Color(255, 255, 255));
        nicknameLabel.setBounds(250, 260, 300, 30);
        add(nicknameLabel);

        // Nickname input field - styled
        nicknameField = new JTextField();
        nicknameField.setBounds(250, 300, 300, 40);
        nicknameField.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
        nicknameField.setHorizontalAlignment(JTextField.CENTER);
        nicknameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(139, 90, 43), 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        add(nicknameField);

        // Start button - styled
        startButton = new JButton("게임 시작");
        startButton.setBounds(325, 360, 150, 45);
        startButton.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        startButton.setBackground(new Color(255, 200, 100));
        startButton.setForeground(new Color(139, 69, 19));
        startButton.setFocusPainted(false);
        startButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(139, 90, 43), 2),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleStart();
            }
        });
        add(startButton);

        // Add hover effect to button
        startButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                startButton.setBackground(new Color(255, 220, 120));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                startButton.setBackground(new Color(255, 200, 100));
            }
        });

        // Add Enter key listener for nickname field
        nicknameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleStart();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            // Draw background image scaled to panel size
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            
            // Add semi-transparent overlay for better text visibility
            g.setColor(new Color(0, 0, 0, 80));
            g.fillRect(0, 0, getWidth(), getHeight());
        } else {
            // Fallback gradient background
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(135, 206, 250),
                0, getHeight(), new Color(70, 130, 180)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }

    private void handleStart() {
        String nickname = nicknameField.getText().trim();

        if (nickname.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "닉네임을 입력해주세요.",
                "알림", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (nickname.length() < 2 || nickname.length() > 12) {
            JOptionPane.showMessageDialog(this, 
                "닉네임은 2~12자 사이로 입력해주세요.",
                "알림", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (nickname.contains(":")) {
            JOptionPane.showMessageDialog(this, 
                "닉네임에 ':' 문자는 사용할 수 없습니다.",
                "알림", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create user with nickname (no password needed for simple login)
        User user = new User(nickname, "", "defaultWarrior");
        callback.onLoginSuccess(user);
    }
}
