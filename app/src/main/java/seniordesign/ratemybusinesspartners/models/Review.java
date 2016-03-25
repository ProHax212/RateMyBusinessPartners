package seniordesign.ratemybusinesspartners.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAutoGeneratedKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMarshalling;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * Created by Ryan Comer on 2/3/2016.
 */
@DynamoDBTable(tableName = "Reviews")
public class Review implements Parcelable{

    //Fields for the Review model
    private String reviewID;

    private User reviewer;
    private String reviewText;
    private String targetCompanyName;
    private Float numStars;
    private Calendar dateCreated;
    private boolean isUserAnonymous;



    public Review(User reviewer, String reviewText, String targetCompanyName, Float numStars, boolean isUserAnonymous){

        this.reviewer = reviewer;
        this.reviewText = reviewText;
        this.targetCompanyName = targetCompanyName;
        this.numStars = numStars;
        this.dateCreated = Calendar.getInstance();
        this.isUserAnonymous = isUserAnonymous;

    }

    public Review(){
        this.dateCreated = Calendar.getInstance();
    }


    /**
     * Override the toString method so that the List Views are populated with the proper review format
     * @return The correctly-formatted review
     */
    public String toString(){
        StringBuilder builder = new StringBuilder();

        if(!this.isUserAnonymous){
            builder.append(reviewer.getUserId());
        }else{
            builder.append("Anonymous");
        }
        DateFormat dateFormat = DateFormat.getDateInstance();
        dateFormat.setCalendar(this.dateCreated);
        builder.append("\n" + dateFormat.format(dateFormat.getCalendar().getTime()));
        builder.append("\n\n" + this.reviewText);

        return builder.toString();
    }

    // Getters and Setters
    @DynamoDBAttribute(attributeName = "ReviewID")
    @DynamoDBAutoGeneratedKey
    public String getReviewID(){
        return this.reviewID;
    }
    public void setReviewID(String id){
        this.reviewID = id;
    }

    @DynamoDBAttribute(attributeName = "Review Text")
    public String getReviewText() {
        return reviewText;
    }
    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    @DynamoDBHashKey(attributeName = "Target Company")
    public String getTargetCompanyName() {
        return targetCompanyName;
    }
    public void setTargetCompanyName(String targetCompanyName) {
        this.targetCompanyName = targetCompanyName;
    }

    @DynamoDBAttribute(attributeName = "Reviewer")
    @DynamoDBMarshalling(marshallerClass = User.UserConverter.class)
    public User getReviewer() {
        return reviewer;
    }
    public void setReviewer(User reviewer) {
        this.reviewer = reviewer;
    }

    @DynamoDBRangeKey(attributeName = "Date Created")
    public Calendar getDateCreated() {
        return dateCreated;
    }
    public void setDateCreated(Calendar dateCreated) {
        this.dateCreated = dateCreated;
    }

    @DynamoDBAttribute(attributeName = "Anonymous")
    public boolean getIsUserAnonymous() {
        return isUserAnonymous;
    }
    public void setIsUserAnonymous(boolean isUserAnonymous) {
        this.isUserAnonymous = isUserAnonymous;
    }

    @DynamoDBIndexRangeKey(attributeName = "Rating", localSecondaryIndexName = "ReviewRatingIndex")
    public Float getNumStars() {
        return numStars;
    }
    public void setNumStars(Float numStars) {
        this.numStars = numStars;
    }






    // Parcelable Interface Methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Push all of the fields onto the dest stack
        dest.writeString(this.reviewID);
        dest.writeString(this.reviewer.getUserId());
        dest.writeString(this.reviewer.getCompany());
        dest.writeString(this.reviewText);
        dest.writeString(this.targetCompanyName);
        dest.writeString(this.numStars.toString());
        dest.writeSerializable(this.dateCreated);
        dest.writeString(this.isUserAnonymous == true ? "1" : "0");
    }

    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>(){
        @Override
        public Review createFromParcel(Parcel source) {
            Review review = new Review();
            review.setReviewID(source.readString());

            User reviewer = new User();
            reviewer.setUserId(source.readString());
            reviewer.setCompany(source.readString());
            review.setReviewer(reviewer);

            review.setReviewText(source.readString());
            review.setTargetCompanyName(source.readString());
            review.setNumStars(Float.parseFloat(source.readString()));

            review.setDateCreated((Calendar) source.readSerializable());

            review.setIsUserAnonymous(source.readString() == "1" ? true : false);

            return review;
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[0];
        }
    };
}
