package seniordesign.ratemybusinesspartners.dynamodb;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.lang.reflect.Array;
import java.util.ArrayList;

import seniordesign.ratemybusinesspartners.CompanyProfile;
import seniordesign.ratemybusinesspartners.MainActivity;
import seniordesign.ratemybusinesspartners.interfaces.ReviewsChangedListener;
import seniordesign.ratemybusinesspartners.models.Review;

/**
 * Created by Ryan Comer on 4/18/2016.
 * The purpose of this class is to handle all interactions with the Dynamo Database
 */
public class DynamoHandler {

    private static DynamoHandler instance = null;

    private AmazonDynamoDBClient client;
    private DynamoDBMapper mapper;

    private ArrayList<Review> cachedReviews;
    private String currentCompany;  // To make sure the cached reviews match the company

    private ArrayList<ReviewsChangedListener> reviewsChangedListeners;

    private DynamoHandler(Context context){

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                context,
                "us-east-1:7af6d1e9-e1a2-45e5-8d91-8fb5be4b70d4", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );

        this.client = new AmazonDynamoDBClient(credentialsProvider);
        this.mapper = new DynamoDBMapper(client);
        this.reviewsChangedListeners = new ArrayList<>();

        updateCachedReviews(CompanyProfile.currentCompany);

    }

    public static DynamoHandler getInstance(Context context){
        if(instance == null){
            instance = new DynamoHandler(context);
        }

        return instance;
    }

    public void updateCachedReviews(final String companyName){

        AsyncReviewsUpdate updater = new AsyncReviewsUpdate();
        updater.execute(companyName);

        try{
            this.cachedReviews = updater.get();
        }catch (Exception e){
            Log.e("CachedReviews", "Exception thrown in updateCachedReviews: " + e.getLocalizedMessage());
        }

        this.currentCompany = companyName;

    }

    private void notifyListeners(){
        for(ReviewsChangedListener listener : this.reviewsChangedListeners){
            listener.dataChanged();
        }
    }

    public boolean addListener(ReviewsChangedListener listener){
        if(reviewsChangedListeners.contains(listener)) return false;

        reviewsChangedListeners.add(listener);
        return true;
    }

    public boolean removeListener(ReviewsChangedListener listener){
        if(!reviewsChangedListeners.contains(listener)) return false;

        reviewsChangedListeners.remove(listener);
        return true;
    }

    public ArrayList<Review> getCachedReviews(String targetCompany){
        if(!targetCompany.equals(this.currentCompany)){
            updateCachedReviews(targetCompany);
        }
        return this.cachedReviews;
    }

    /**
     * Save the review to DynamoDB
     * @param review Review to save
     */
    public void saveReview(final Review review){

        Thread saveReviewThread = new Thread(new Runnable() {
            @Override
            public void run() {
                mapper.save(review);
            }
        });
        saveReviewThread.start();
        try {
            saveReviewThread.join();
        }catch (InterruptedException e){
            Log.e("Save Review", e.getMessage());
        }
        updateCachedReviews(CompanyProfile.currentCompany);
        notifyListeners();

    }

    private class AsyncReviewsUpdate extends AsyncTask<String, String, ArrayList<Review>>{


        @Override
        protected ArrayList<Review> doInBackground(String... params) {
            ArrayList<Review> returnedReviews = new ArrayList<>();
            DynamoDBQueryExpression<Review> queryExpression = new DynamoDBQueryExpression<>();
            Review queryReview = new Review();
            queryReview.setTargetCompanyName(params[0]);
            queryExpression.setHashKeyValues(queryReview);

            PaginatedQueryList<Review> queryResults = mapper.query(Review.class, queryExpression);

            for (Review review : queryResults) {
                returnedReviews.add(review);
            }

            return returnedReviews;
        }
    }

}
