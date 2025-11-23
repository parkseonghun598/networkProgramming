package client.util;

import common.user.User;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserManager {
    private static final String USERS_FILE = "users.txt";
    private Map<String, User> users;

    public UserManager() {
        users = new HashMap<>();
        loadUsers();
    }

    private void loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length >= 2) {
                    String username = parts[0];
                    String password = parts[1];
                    String characterType = parts.length > 2 ? parts[2] : "character1";
                    users.put(username, new User(username, password, characterType));
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load users: " + e.getMessage());
        }
    }

    private void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User user : users.values()) {
                writer.write(String.format("%s:%s:%s%n",
                    user.getUsername(),
                    user.getPassword(),
                    user.getCharacterType()));
            }
        } catch (IOException e) {
            System.err.println("Failed to save users: " + e.getMessage());
        }
    }

    public User login(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    public boolean register(String username, String password) {
        if (users.containsKey(username)) {
            return false; // Username already exists
        }
        User newUser = new User(username, password, "character1");
        users.put(username, newUser);
        saveUsers();
        return true;
    }

    public void updateCharacterType(String username, String characterType) {
        User user = users.get(username);
        if (user != null) {
            user.setCharacterType(characterType);
            saveUsers();
        }
    }
}
