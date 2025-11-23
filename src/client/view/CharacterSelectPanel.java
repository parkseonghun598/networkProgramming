package client.view;

import client.util.UserManager;
import common.user.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CharacterSelectPanel extends JPanel {
    private User user;
    private UserManager userManager;
    private String selectedCharacter;
    private CharacterSelectCallback callback;
    private JButton char1Button;
    private JButton char2Button;

    public interface CharacterSelectCallback {
        void onCharacterSelected(User user);
    }

    public CharacterSelectPanel(User user, CharacterSelectCallback callback) {
        this.user = user;
        this.userManager = new UserManager();
        this.callback = callback;
        this.selectedCharacter = user.getCharacterType();

        setPreferredSize(new Dimension(500, 400));
        setLayout(null);
        setBackground(new Color(240, 240, 240));

        // Title
        JLabel titleLabel = new JLabel("Select Your Character");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setBounds(130, 30, 250, 30);
        add(titleLabel);

        // Welcome message
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getUsername() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        welcomeLabel.setBounds(160, 70, 250, 25);
        add(welcomeLabel);

        // Character 1 button
        char1Button = new JButton("Character 1");
        char1Button.setBounds(80, 150, 150, 100);
        char1Button.setFont(new Font("Arial", Font.BOLD, 14));
        char1Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectCharacter("character1");
            }
        });
        add(char1Button);

        // Character 2 button
        char2Button = new JButton("Character 2");
        char2Button.setBounds(270, 150, 150, 100);
        char2Button.setFont(new Font("Arial", Font.BOLD, 14));
        char2Button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectCharacter("character2");
            }
        });
        add(char2Button);

        // Start button
        JButton startButton = new JButton("Start Game");
        startButton.setBounds(175, 290, 150, 40);
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleStartGame();
            }
        });
        add(startButton);

        // Highlight current selection
        updateButtonStyles();
    }

    private void selectCharacter(String characterType) {
        this.selectedCharacter = characterType;
        updateButtonStyles();
    }

    private void updateButtonStyles() {
        if (selectedCharacter.equals("character1")) {
            char1Button.setBackground(new Color(100, 200, 100));
            char2Button.setBackground(null);
        } else {
            char1Button.setBackground(null);
            char2Button.setBackground(new Color(100, 200, 100));
        }
    }

    private void handleStartGame() {
        user.setCharacterType(selectedCharacter);
        userManager.updateCharacterType(user.getUsername(), selectedCharacter);
        callback.onCharacterSelected(user);
    }
}
