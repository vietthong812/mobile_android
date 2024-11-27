package tdtu.EStudy_App.models;

public class User {
    private String email;
    private String password;
    private String avatar = null;
    private String fullName;
    private String birthday;

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public User(String password, String email, String fullName, String birthday) {
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.birthday = birthday;
    }

    public String getPassword() {
        return password;
    }
}
