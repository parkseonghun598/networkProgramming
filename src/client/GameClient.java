package client;

import client.view.LoginFrame;

import javax.swing.SwingUtilities;

public class GameClient {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
