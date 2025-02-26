package sg.edu.np.mad.mad24p03team2.DatabaseFunctions;

public class LoginInfoClass {
    private String email;
    private String password;

    public LoginInfoClass() {
        email = "";
        password = "";
    }
    public LoginInfoClass(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
