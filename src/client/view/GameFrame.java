package client.view;

import javax.swing.JFrame;

public class GameFrame extends JFrame {

    public GameFrame(String username, String characterType) {
        setTitle("Mini MapleStory - " + username);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        GamePanel gamePanel = new GamePanel(username, characterType);
        this.add(gamePanel);
        this.pack(); // Adjust frame size to fit the panel

        setLocationRelativeTo(null); // Center the window after packing
        gamePanel.requestFocusInWindow(); // Ensure the panel has focus to receive key events
    }
}
