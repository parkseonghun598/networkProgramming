package common.user;

public class User {
    private String username;
    private String password;
    private String characterType; // "character1" or "character2"

    public User(String username, String password, String characterType) {
        this.username = username;
        this.password = password;
        this.characterType = characterType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCharacterType() {
        return characterType;
    }

    public void setCharacterType(String characterType) {
        this.characterType = characterType;
    }
}
