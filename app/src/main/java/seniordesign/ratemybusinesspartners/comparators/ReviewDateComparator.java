package seniordesign.ratemybusinesspartners.comparators;

import java.util.Comparator;

import seniordesign.ratemybusinesspartners.models.Review;

/**
 * Created by Ryan Comer on 2/22/2016.
 */
public class ReviewDateComparator implements Comparator<Review> {

    public static final String DATE_ASCENDING = "ReviewDateComparator.Date.Ascending";
    public static final String DATE_DESCENDING = "ReviewDateComparator.Date.Descending";

    private String ordering;

    public ReviewDateComparator(String ordering){

        this.ordering = ordering;

    }

    @Override
    public int compare(Review lhs, Review rhs) {
        if(lhs.getDateCreated().compareTo(rhs.getDateCreated()) == 0) return 0;

        switch(this.ordering){
            case DATE_ASCENDING:
                return lhs.getDateCreated().compareTo(rhs.getDateCreated());
            case DATE_DESCENDING:
                return lhs.getDateCreated().compareTo(rhs.getDateCreated()) * -1;   // Flip the result of compareTo to get descending order
        }

        return 0;   // Should never get here because an ordering should be defined
    }
}
