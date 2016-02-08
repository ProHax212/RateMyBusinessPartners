package seniordesign.ratemybusinesspartners.models;

/**
 * Created by Ryan Comer on 2/4/2016.
 */
public class User {

    private String userName;
    private String email;
    private String password;
    private String company;


    public User(String userName, String password, String email, String company){
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.company = company;
    }


    //Getter and Setter methods

    public String getUserName(){
        return this.userName;
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

    public void setUserName(String username){
         this.userName = username;
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
