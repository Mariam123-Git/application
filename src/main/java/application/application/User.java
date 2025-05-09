package application.application;

public class User {
    private int id;
    private String username;
    private String email;
    private String role;

    public User(int id, String username, String email, String role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    // Getters et setters
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }

    // Ajoute des setters si tu modifies l'utilisateur plus tard
}
