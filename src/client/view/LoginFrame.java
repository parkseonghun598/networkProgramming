package client.view;

import common.user.User;

import javax.swing.*;

public class LoginFrame extends JFrame {
    private LoginPanel loginPanel;

    public LoginFrame() {
        setTitle("Mini MapleStory - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        showLoginPanel();

        pack();
        setLocationRelativeTo(null);
    }

    private void showLoginPanel() {
        getContentPane().removeAll();

        loginPanel = new LoginPanel(new LoginPanel.LoginCallback() {
            @Override
            public void onLoginSuccess(User user) {
                user.setCharacterType("defaultWarrior");
                openGameFrame(user);
            }
        });

        add(loginPanel);
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
