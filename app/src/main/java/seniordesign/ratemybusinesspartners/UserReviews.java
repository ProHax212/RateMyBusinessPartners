package seniordesign.ratemybusinesspartners;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.marshallers.CalendarToStringMarshaller;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import seniordesign.ratemybusinesspartners.adapters.ReviewListAdapter;
import seniordesign.ratemybusinesspartners.models.Review;
import seniordesign.ratemybusinesspartners.models.User;

/**
 * Created by ceenajac on 4/22/2016.
 */
public class UserReviews extends AppCompatActivity {


    public static final String SORT_BY_DATE_NEWEST = "ReviewResults.Sort-Date-Newest";
    public static final String SORT_BY_DATE_OLDEST = "ReviewResults.Sort-Date-Oldest";
    public static final String SORT_BY_RATING_HIGHEST = "ReviewResults.Sort-Rating-Highest";
    public static final String SORT_BY_RATING_LOWEST = "ReviewResults.Sort-Rating-Lowest";
    public static final String[] SORT_OPTIONS = {SORT_BY_DATE_NEWEST, SORT_BY_DATE_OLDEST, SORT_BY_RATING_HIGHEST,
            SORT_BY_RATING_LOWEST};

    public static final String SHOW_ALL = "ReviewResults.Show-All";
    public static final String SHOW_LAST_WEEK = "ReviewResults.Show-Last-Week";
    public static final String SHOW_LAST_MONTH = "ReviewResults.Show-Last-Month";
    public static final String SHOW_LAST_3_MONTHS = "ReviewResults.Show-Last-3-Months";
    public static final String SHOW_LAST_6_MONTHS = "ReviewResults.Show-Last-6-Months";
    public static final String SHOW_LAST_12_MONTHS = "ReviewResults.Show-Last-12-Months";
    public static final String[] DATE_OPTIONS = {SHOW_ALL, SHOW_LAST_WEEK, SHOW_LAST_MONTH, SHOW_LAST_3_MONTHS,
            SHOW_LAST_6_MONTHS, SHOW_LAST_12_MONTHS};


    private User currentUser;
    private RatingBar averageReview;
    private TextView countReview;
    private String userID;
    private Boolean isPublic;
    private ArrayAdapter<CharSequence> sortBySpinnerAdapter;
    private ArrayAdapter<CharSequence> dateSpinnerAdapter;
    private ReviewListAdapter reviewResultsAdapter;
    private ArrayList<String> activeCompany = new ArrayList<String>();
    // Ryan database variables
    AmazonDynamoDBClient ryanClient;
    DynamoDBMapper ryanMapper;
    private int count = 0;
    private float avgReviews = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_result_toolbar);


        Intent intent = getIntent();
        currentUser = (User)intent.getSerializableExtra("userProfile");
        userID = currentUser.getUserId();
        isPublic = (Boolean)intent.getBooleanExtra("isPublic",false);
        TextView userName = (TextView) findViewById(R.id.resultNumber);
        userName.setText(userID);
        if(!isPublic){
            toolbar.setTitle("My Reviews");
        }else{
            toolbar.setTitle(userID + "'s Reviews");
        }
        countReview = (TextView)findViewById(R.id.textView2);
        averageReview = (RatingBar)findViewById(R.id.ratingBar);
        // Initialize Ryan Database
        initializeRyanDatabase();




        //Initialize Reviews
        ListView reviewResults = (ListView) findViewById(R.id.reviewResultsListView);
        reviewResultsAdapter = new ReviewListAdapter(this, new ArrayList<Review>());
        reviewResults.setAdapter(reviewResultsAdapter);
        reviewResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<Review> arrayAdapter = (ArrayAdapter<Review>) parent.getAdapter();
                DynamoDBQueryExpression<Review> queryExpression = new DynamoDBQueryExpression<Review>();

                Intent intent = new Intent(view.getContext(), ViewUserReview.class);
                intent.putExtra(CompanyProfile.COMPANY_PROFILE_REVIEW_TO_VIEW, arrayAdapter.getItem(position));
                startActivity(intent);
            }
        });

        AsyncListUpdate updater = new AsyncListUpdate();
        updater.execute(SORT_BY_DATE_NEWEST, SHOW_ALL);

    }


    // Initialize Ryan's Database
    private void initializeRyanDatabase(){

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:7af6d1e9-e1a2-45e5-8d91-8fb5be4b70d4", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );

        this.ryanClient = new AmazonDynamoDBClient(credentialsProvider);
        this.ryanMapper = new DynamoDBMapper(ryanClient);

    }




    /**
     * Filter the review results
     * Get the filters from the other views and apply them here
     * @param view The filter button that fired this event
     */




        /**
         * This Inner class is used to scan the review database and update the 'recent reviews' list
         * The scan is done in an AsyncTask and updates the ListView on the UI thread
         * UI operations have to be done on the onPreExecute() and onPostExecute() methods
         * The doInBackground() method is used to run the actual database scan
         */
        private class AsyncListUpdate extends AsyncTask<String, String, ArrayList<Review>> {

            @Override
            protected void onPreExecute(){

                // Clear the adapter
                reviewResultsAdapter.clear();

            }

            @Override
            protected ArrayList<Review> doInBackground(String... params) {

                DynamoDBQueryExpression<Review> queryExpression = new DynamoDBQueryExpression<Review>();


                activeCompany.add("Walmart");
                activeCompany.add("Microsoft");
                activeCompany.add("Dell");
                activeCompany.add("Google");
                ArrayList<Review> resultsList = new ArrayList<>();
                for(String company : activeCompany){
                    Review queryReview = new Review();
                    queryReview.setTargetCompanyName(company);

                    queryExpression.setHashKeyValues(queryReview);

                    PaginatedQueryList<Review> queryResults = ryanMapper.query(Review.class, queryExpression);


                    for(Review review : queryResults){

                        if((review.getReviewer().getUserId().equals(userID))&& !isPublic){
                            resultsList.add(review);
                            count+=1;
                            avgReviews+=review.getNumStars();
                        }  else if((review.getReviewer().getUserId().equals(userID))&& isPublic && !review.getIsUserAnonymous()){
                            resultsList.add(review);
                            count+=1;
                            avgReviews+=review.getNumStars();
                        }

                    }
                }


                return resultsList;
            }

            @Override
            protected void onPostExecute(ArrayList<Review> scanList){

                for(Review review : scanList){
                    reviewResultsAdapter.add(review);
                }
                countReview.setText(Integer.toString(count));
                averageReview.setRating(avgReviews/count);
            }

        }

    }