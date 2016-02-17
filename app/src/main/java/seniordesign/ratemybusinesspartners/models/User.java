package seniordesign.ratemybusinesspartners.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Ryan Comer on 2/4/2016.
 */
public class User {
    /*public static final String LOGIN_USERNAME = "test username";
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
    private String confirmPassword;*/

    private String userIdToken;
    private String company;

    public User(){

    }

    public User (String userIdToken, String company){

    }

    public String getUserIdToken(){
        return this.userIdToken;
    }

    /*public User() {
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

    public String getFirstName() { return this.firstname; }
    public String getLastName() { return this.lastname; }
    public String getEmail() { return this.email; }
    public String getCompany(){
        return this.company;
    }
    public String getUsername(){
        return this.username;
    }
    public String getPassword(){
        return this.password;
    }
    public String getConfirmPassword() { return this.confirmPassword; }


    public void setFirstName(String firstname){
        this.firstname = firstname;
    }
    public void setLastname(String lastname){
        this.lastname = lastname;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setCompany(String company){
        this.company = company;
    }
    public void setUsername(String username){
         this.username = username;
    }
    public void setPassword(String password){
        this.password = password;
    }
    public void setConfirmPassword(String confirmPassword){
        this.confirmPassword = confirmPassword;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.firstname);
        dest.writeString(this.lastname);
        dest.writeString(this.username);
        dest.writeString(this.email);
        dest.writeString(this.company);
        dest.writeString(this.password);
        dest.writeString(this.confirmPassword);
    }

    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>(){

        @Override
        public User createFromParcel(Parcel source) {
            return new User(source.readString(), source.readString(), source.readString(), source.readString(), source.readString(), source.readString(), source.readString());
        }

        @Override
        public User[] newArray(int size) {
            return new User[0];
        }
    };*/
}
