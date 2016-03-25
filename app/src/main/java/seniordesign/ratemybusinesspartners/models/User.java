package seniordesign.ratemybusinesspartners.models;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMarshaller;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

/**
 * Created by Ryan Comer on 2/4/2016.
 */

@DynamoDBTable(tableName = "Users")
public class User {
    private String userId;
    private String company;

    public User() {}
    public User (String userId, String company){
        this.userId = userId;
        this.company = company;
    }

    //Getter and Setter methods
    @DynamoDBHashKey(attributeName = "UserIdToken")
    public String getUserId() { return userId; }
    public void setUserId(String userId){
        this.userId = userId;
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

            builder.append(user.getUserId() + "\t" + user.getCompany());
            return builder.toString();
        }

        @Override
        public User unmarshall(Class<User> aClass, String s){

            String[] attributes = s.split("\t");

            User user = new User();
            user.setUserId(attributes[0]);
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
