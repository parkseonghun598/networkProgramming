package client.view;

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
            public void onLoginSuccess(String username) {
                openGameFrame(username);
            }
        });

        add(loginPanel);
        pack();
        revalidate();
        repaint();
    }

    private void openGameFrame(String username) {
        SwingUtilities.invokeLater(() -> {
            String characterType = "defaultWarrior";
            GameFrame gameFrame = new GameFrame(username, characterType);
            gameFrame.setVisible(true);
            LoginFrame.this.dispose();
        });
    }
}
