package seniordesign.ratemybusinesspartners.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMarshaller;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.ArrayList;

/**
 * Created by Ryan Comer on 2/4/2016.
 */

@DynamoDBTable(tableName = "Users")
public class User {
    private String userIdToken;
    private String company;
    private ArrayList<String> reviewIds;

    public User() {}
    public User (String userIdToken, String company){
        this.userIdToken = userIdToken;
        this.company = company;
        this.reviewIds = new ArrayList<>();
    }

    //Getter and Setter methods
    @DynamoDBHashKey(attributeName = "UserIdToken")
    public String getUserIdToken() { return userIdToken; }
    public void setUserIdToken(String userIdToken){
        this.userIdToken = userIdToken;
    }

    @DynamoDBRangeKey(attributeName = "Company")
    public String getCompany() { return company; }
    public void setCompany(String company){
        this.company = company;
    }


    public static class UserConverter implements DynamoDBMarshaller<User>{

        @Override
        public String marshall(User user) {
            StringBuilder builder = new StringBuilder();

            builder.append(user.getUserIdToken() + "\t" + user.getCompany());
            return builder.toString();
        }

        @Override
        public User unmarshall(Class<User> aClass, String s){

            String[] attributes = s.split("\t");

            User user = new User();
            user.setUserIdToken(attributes[0]);
            user.setCompany(attributes[1]);

            return user;
        }
    }


//
//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeString(this.firstname);
//        dest.writeString(this.lastname);
//        dest.writeString(this.username);
//        dest.writeString(this.email);
//        dest.writeString(this.company);
//        dest.writeString(this.password);
//        dest.writeString(this.confirmPassword);
//    }
//
//    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>(){
//
//        @Override
//        public User createFromParcel(Parcel source) {
//            return new User(source.readString(), source.readString(), source.readString(), source.readString(), source.readString(), source.readString(), source.readString());
//        }
//
//        @Override
//        public User[] newArray(int size) {
//            return new User[0];
//        }
//    };
}
