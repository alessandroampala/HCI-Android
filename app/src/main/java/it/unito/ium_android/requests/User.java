package it.unito.ium_android.requests;

// Class of the users
public class User {
    private final String username;

    public User(String username) {
        this.username = username;
    }

    // Returns The username
    public String getUsername() {
        return this.username;
    }

}
