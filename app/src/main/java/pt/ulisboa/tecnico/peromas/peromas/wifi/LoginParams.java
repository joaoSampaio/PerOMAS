package pt.ulisboa.tecnico.peromas.peromas.wifi;

/**
 * Created by sampaio on 26-03-2015.
 */
public class LoginParams {
    public LoginParams(String username, String password, String csrf_token){
        this.username = username;
        this.password = password;
        this.csrf_token = csrf_token;
        this.remember_me = "y";
    }
    String username, remember_me, password;
    String csrf_token;
}
