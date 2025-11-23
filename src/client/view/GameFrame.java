package client.view;

import common.user.User;

import javax.swing.JFrame;

public class GameFrame extends JFrame {

    public GameFrame(User user) {
        setTitle("Mini MapleStory - " + user.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        GamePanel gamePanel = new GamePanel(user);
        this.add(gamePanel);
        this.pack(); // Adjust frame size to fit the panel

        setLocationRelativeTo(null); // Center the window after packing
        gamePanel.requestFocusInWindow(); // Ensure the panel has focus to receive key events
    }
}
