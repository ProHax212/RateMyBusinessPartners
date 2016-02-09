package seniordesign.ratemybusinesspartners.models;

/**
 * Created by Ryan Comer on 2/3/2016.
 */
public class Review {

    //Fields for the Review model

    private User reviewer;
    private String reviewText;
    private String targetCompanyName;
    private int numStars;



    public Review(User reviewer, String reviewText, String targetCompanyName, int numStars){

        this.reviewer = reviewer;
        this.reviewText = reviewText;
        this.targetCompanyName = targetCompanyName;
        this.numStars = numStars;

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
