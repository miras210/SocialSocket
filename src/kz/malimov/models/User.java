package kz.malimov.models;

public class User {
    private int id, visibility;
    private String username, password;

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public User copyWithoutPassword() {
        User u = this;
        u.setPassword("");
        return u;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", visibility=" + visibility +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}