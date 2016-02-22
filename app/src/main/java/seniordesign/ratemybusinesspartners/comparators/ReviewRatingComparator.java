package seniordesign.ratemybusinesspartners.comparators;

import java.util.Comparator;

import seniordesign.ratemybusinesspartners.models.Review;

/**
 * Created by Ryan Comer on 2/22/2016.
 */
public class ReviewRatingComparator implements Comparator<Review> {

    public static final String RATING_ASCENDING = "ReviewRatingComparator.Rating.Ascending";
    public static final String RATING_DESCENDING = "ReviewRatingComparator.Rating.Descending";

    private String ordering;

    public ReviewRatingComparator(String ordering){

        this.ordering = ordering;

    }

    @Override
    public int compare(Review lhs, Review rhs) {

        if(lhs.getNumStars() == rhs.getNumStars()) return 0;

        switch (ordering){
            case RATING_ASCENDING:
                if(lhs.getNumStars() > rhs.getNumStars()) return 1;
                else return -1;
            case RATING_DESCENDING:
                if(lhs.getNumStars() < rhs.getNumStars()) return 1;
                else return -1;
        }

        return 0;   // Should never get here because there should always be an ordering
    }
}
