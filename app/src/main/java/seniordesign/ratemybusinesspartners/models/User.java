package seniordesign.ratemybusinesspartners.models;

import java.io.Serializable;

/**
 * Created by Ryan Comer on 2/4/2016.
 */
public class User implements Serializable {
    public static final String LOGIN_USERNAME = "test username";
    public static final String LOGIN_PASSWORD = "test password";

    public static final String REGISTER_FIRSTNAME = "TEST FIRSTNAME";
    public static final String REGISTER_LASTNAME = "TEST LASTNAME";
    public static final String REGISTER_EMAIL = "TEST EMAIL";
    public static final String REGISTER_COMPANY = "TEST COMPANY";
    public static final String REGISTER_USERNAME = "TEST USERNAME";
    public static final String REGISTER_PASSWORD = "TEST PW";
    public static final String REGISTER_CONFIRMPASSWORD = "TEST CONFIRM PW";

    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private String company;
    private String password;
    private String confirmPassword;

    public User() {
        this.firstname = "Bruce";
        this.lastname = "Wayne";
        this.email = "Bruce.Wayne@utexas.edu";
        this.company = "Wayne Enterprises";
        this.username = "Batman";
        this.password = "Catwoman";
        this.confirmPassword = "Catwoman";
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public User(String firstname, String lastname, String username, String email, String company, String password, String confirmPassword){
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.email = email;
        this.company = company;
        this.password = password;
        this.confirmPassword = confirmPassword;
    }


    //Getter and Setter methods

    public String getUsername(){
        return this.username;
    }

    public String getPassword(){
        return this.password;
    }

    public String getEmail(){
        return this.email;
    }

    public String getCompany(){
        return this.company;
    }

    public void setUsername(String username){
         this.username = username;
    }

    public void setPassword(String password){
        this.password = password;
    }

    public void setEmail(String email){
        this.email = email;
    }

    public void setCompany(String company){
        this.company = company;
    }


}
