package seniordesign.ratemybusinesspartners.models;

import java.util.Calendar;

/**
 * Created by Ryan Comer on 2/3/2016.
 */
public class Review {

    //Fields for the Review model

    private User reviewer;
    private String reviewText;
    private String targetCompanyName;
    private Float numStars;
    private Calendar dateCreated;



    public Review(User reviewer, String reviewText, String targetCompanyName, Float numStars){

        this.reviewer = reviewer;
        this.reviewText = reviewText;
        this.targetCompanyName = targetCompanyName;
        this.numStars = numStars;
        this.dateCreated = Calendar.getInstance();

    }





    /**
     * Override the toString method so that the List Views are populated with the proper review format
     * @return The correctly-formatted review
     */
    public String toString(){
        StringBuilder builder = new StringBuilder();

        builder.append(this.targetCompanyName + "\n");
        builder.append(reviewer.getUsername() + "\t\t");
        builder.append("Stars: " + this.numStars);
        builder.append("\n" + this.reviewText);

        return builder.toString();
    }

    //Getters and Setters
    public User getReviewer(){
        return this.reviewer;
    }

}
