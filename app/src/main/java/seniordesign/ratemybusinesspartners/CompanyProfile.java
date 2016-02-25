package seniordesign.ratemybusinesspartners;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.util.ArrayList;
import java.util.Collections;

import seniordesign.ratemybusinesspartners.adapters.ReviewListAdapter;
import seniordesign.ratemybusinesspartners.comparators.ReviewDateComparator;
import seniordesign.ratemybusinesspartners.models.DummyDatabase;
import seniordesign.ratemybusinesspartners.models.Review;
import seniordesign.ratemybusinesspartners.models.User;

public class CompanyProfile extends AppCompatActivity {

    public static final String COMPANY_PROFILE_TARGET_COMPANY = "com.ryan.target.company";

    ReviewListAdapter reviewArrayAdapter;
    String currentCompany;
    private User currentUser;

    // Ryan's Test Database
    private AmazonDynamoDBClient ryanClient;
    private DynamoDBMapper ryanMapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Retrieve Intent inputs
        Intent intent = getIntent();
        currentCompany = intent.getStringExtra(COMPANY_PROFILE_TARGET_COMPANY);

        //Initialize profile information
        TextView companyNameTextView = (TextView) findViewById(R.id.companyNameTextView);
        companyNameTextView.setText(currentCompany);

        //Initialize List View
        ListView reviewList = (ListView) findViewById(R.id.companyProfileReviewList);

        reviewArrayAdapter = new ReviewListAdapter(this, new ArrayList<Review>());
        reviewList.setAdapter(reviewArrayAdapter);

        // Initialize Ryan's Database
        initializeRyanDatabase();

        // Show the reviews
        AsyncListUpdate updater = new AsyncListUpdate();
        updater.execute();
    }

    private void initializeRyanDatabase(){

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:7af6d1e9-e1a2-45e5-8d91-8fb5be4b70d4", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );

        this.ryanClient = new AmazonDynamoDBClient(credentialsProvider);
        this.ryanMapper = new DynamoDBMapper(ryanClient);

    }


    // Navigation Methods
    public void viewAllReviews(View view){
        Intent intent = new Intent(this, ReviewResults.class);
        intent.putExtra(COMPANY_PROFILE_TARGET_COMPANY, this.currentCompany);
        startActivity(intent);
    }

    public void writeReview(View view){
        Intent intent = new Intent(this, WriteReview.class);
        intent.putExtra(COMPANY_PROFILE_TARGET_COMPANY, this.currentCompany);
        startActivity(intent);
    }


    /**
     * This Inner class is used to scan the review database and update the 'recent reviews' list
     * The scan is done in an AsyncTask and updates the ListView on the UI thread
     * UI operations have to be done on the onPreExecute() and onPostExecute() methods
     * The doInBackground() method is used to run the actual database scan
     */
    private class AsyncListUpdate extends AsyncTask<String, String, ArrayList<Review>>{

        @Override
        protected void onPreExecute(){

            // Clear the adapter
            reviewArrayAdapter.clear();

        }

        @Override
        protected ArrayList<Review> doInBackground(String... params) {
            Review comparedReview = new Review();
            comparedReview.setTargetCompanyName("Walmart");

            DynamoDBQueryExpression<Review> queryExpression = new DynamoDBQueryExpression<Review>().withHashKeyValues(comparedReview);
            queryExpression.setScanIndexForward(false); // Setting this to false returns reviews (newest first)

            PaginatedQueryList<Review> queryList = ryanMapper.query(Review.class, queryExpression);

            ArrayList<Review> reviewResults = new ArrayList<>();

            for(Review review : queryList){
                reviewResults.add(review);
            }

            return reviewResults;
        }

        @Override
        protected void onPostExecute(ArrayList<Review> queryList){

            for(Review review : queryList){
                reviewArrayAdapter.add(review);
            }

        }
    }

}
