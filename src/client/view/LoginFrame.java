package client.view;

import common.user.User;

import javax.swing.*;

public class LoginFrame extends JFrame {
    private LoginPanel loginPanel;
    private CharacterSelectPanel characterSelectPanel;

    public LoginFrame() {
        setTitle("Mini MapleStory - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Show login panel first
        showLoginPanel();

        pack();
        setLocationRelativeTo(null);
    }

    private void showLoginPanel() {
        getContentPane().removeAll();

        loginPanel = new LoginPanel(new LoginPanel.LoginCallback() {
            @Override
            public void onLoginSuccess(User user) {
                showCharacterSelectPanel(user);
            }
        });

        add(loginPanel);
        pack();
        revalidate();
        repaint();
    }

    private void showCharacterSelectPanel(User user) {
        getContentPane().removeAll();

        characterSelectPanel = new CharacterSelectPanel(user, new CharacterSelectPanel.CharacterSelectCallback() {
            @Override
            public void onCharacterSelected(User user) {
                openGameFrame(user);
            }
        });

        add(characterSelectPanel);
        pack();
        revalidate();
        repaint();
    }

    private void openGameFrame(User user) {
        SwingUtilities.invokeLater(() -> {
            GameFrame gameFrame = new GameFrame(user);
            gameFrame.setVisible(true);
            LoginFrame.this.dispose();
        });
    }
}
