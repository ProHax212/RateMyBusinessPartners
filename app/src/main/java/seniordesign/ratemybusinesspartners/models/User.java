package seniordesign.ratemybusinesspartners.models;

/**
 * Created by Ryan Comer on 2/4/2016.
 */
public class User {

    private String userName;
    private String email;
    private String password;


    public User(String userName, String password, String email){
        this.userName = userName;
        this.password = password;
        this.email = email;
    }


    //Getter and Setter methods

    public String getUserName(){
        return this.userName;
    }

}
