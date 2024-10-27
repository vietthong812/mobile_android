package tdtu.EStudy_App.models;

public class User {
    private String email;
    private String password;
    private String avatar = null;


    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
