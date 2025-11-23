package client.view;

import client.util.UserManager;
import common.user.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private UserManager userManager;
    private LoginCallback callback;

    public interface LoginCallback {
        void onLoginSuccess(User user);
    }

    public LoginPanel(LoginCallback callback) {
        this.callback = callback;
        this.userManager = new UserManager();

        setPreferredSize(new Dimension(400, 300));
        setLayout(null);
        setBackground(new Color(240, 240, 240));

        // Title
        JLabel titleLabel = new JLabel("Mini MapleStory");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBounds(110, 30, 250, 30);
        add(titleLabel);

        // Username label and field
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(60, 100, 80, 25);
        add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(150, 100, 180, 25);
        add(usernameField);

        // Password label and field
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(60, 140, 80, 25);
        add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 140, 180, 25);
        add(passwordField);

        // Login button
        loginButton = new JButton("Login");
        loginButton.setBounds(100, 190, 90, 30);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
        add(loginButton);

        // Register button
        registerButton = new JButton("Register");
        registerButton.setBounds(210, 190, 90, 30);
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleRegister();
            }
        });
        add(registerButton);

        // Add Enter key listener for password field
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = userManager.login(username, password);
        if (user != null) {
            callback.onLoginSuccess(user);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.",
                "Login Failed", JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }

    private void handleRegister() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter both username and password.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (username.contains(":")) {
            JOptionPane.showMessageDialog(this, "Username cannot contain ':'",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = userManager.register(username, password);
        if (success) {
            JOptionPane.showMessageDialog(this, "Registration successful! Please login.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            passwordField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Username already exists.",
                "Registration Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
