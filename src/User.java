// Class representing a user with a username and password
class User {
    private String username; // Private field to store the username
    private String password; // Private field to store the password

    // Constructor to initialize a User object with a username and password
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters and setters for the username and password fields

    // Setter method to set the username
    public void setUsername(String username) {
        this.username = username;
    }

    // Setter method to set the password
    public void setPassword(String password) {
        this.password = password;
    }

    // Getter method to retrieve the username
    public String getUsername() {
        return username;
    }

    // Getter method to retrieve the password
    public String getPassword() {
        return password;
    }
}
