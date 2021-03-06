package seniordesign.ratemybusinesspartners;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedList;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.marshallers.CalendarToStringMarshaller;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.github.mikephil.charting.formatter.DefaultYAxisValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import seniordesign.ratemybusinesspartners.adapters.CompanyProfileTabAdapter;
import seniordesign.ratemybusinesspartners.adapters.ReviewListAdapter;
import seniordesign.ratemybusinesspartners.comparators.ReviewDateComparator;
import seniordesign.ratemybusinesspartners.dynamodb.DynamoHandler;
import seniordesign.ratemybusinesspartners.fragments.CompanyProfileFragment;
import seniordesign.ratemybusinesspartners.fragments.ReviewResultsFragment;
import seniordesign.ratemybusinesspartners.fragments.WriteReviewFragment;
import seniordesign.ratemybusinesspartners.interfaces.ReviewsChangedListener;
import seniordesign.ratemybusinesspartners.models.DummyDatabase;
import seniordesign.ratemybusinesspartners.models.Review;
import seniordesign.ratemybusinesspartners.models.User;


public class CompanyProfile extends AppCompatActivity implements
        CompanyProfileFragment.ReviewFragmentListener, ReviewResultsFragment.UpdateReviews, WriteReviewFragment.SubmitReview, ReviewsChangedListener {

    public static final String COMPANY_PROFILE_TARGET_COMPANY = "com.ryan.target.company";
    public static final String COMPANY_PROFILE_REVIEW_TO_VIEW = "com.ratemybusinesspartners.companyprofile.reviewtoview";

    public static String currentCompany;
    private User currentUser;

    // Ryan's Test Database
    private AmazonDynamoDBClient ryanClient;
    private DynamoDBMapper ryanMapper;

    // Cache'd Review List
    private ArrayList<Review> cachedReviews;

    private ViewPager mViewPager;
    private CompanyProfileTabAdapter mTabsAdapter;

    // Handler for this main thread
    Handler handler;

    // DynamoHandler for accessing the database
    private DynamoHandler dynamoHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_profile);

        //Retrieve Intent inputs
        Intent intent = getIntent();
        currentCompany = intent.getStringExtra(COMPANY_PROFILE_TARGET_COMPANY);
        /*Look RYAN here*/ String companyName = intent.getStringExtra("companyName");
        // Initialize Ryan's Database
        initializeRyanDatabase();

        // Initialize the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.companyProfileToolbar);
        toolbar.setTitle("");
        toolbar.setLogo(getResources().getDrawable(DummyDatabase.companies.get(currentCompany).getCompanyImageResource()));
        setSupportActionBar(toolbar);

        // Initialize the tabs and fragments
        // The viewpager is used to manage which fragment is being shown based on the selected tab
        this.mViewPager = (ViewPager) findViewById(R.id.companyProfileViewPager);
        this.mTabsAdapter = new CompanyProfileTabAdapter(getSupportFragmentManager(), CompanyProfile.this);
        this.mViewPager.setAdapter(this.mTabsAdapter);

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = (TabLayout) findViewById(R.id.companyProfileTabs);
        tabLayout.setupWithViewPager(mViewPager);

        // Cache the reviews
        initializeCachedReviews();

        this.handler = new Handler();

        this.dynamoHandler = DynamoHandler.getInstance(this.getBaseContext());
        dynamoHandler.updateCachedReviews(currentCompany);
        dynamoHandler.addListener(CompanyProfile.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.dynamoHandler.removeListener(this);
        Log.d("Destroyed", "CompanyProfile has been destroyed");
    }

    private void initializeRyanDatabase() {

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:7af6d1e9-e1a2-45e5-8d91-8fb5be4b70d4", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );

        this.ryanClient = new AmazonDynamoDBClient(credentialsProvider);
        this.ryanMapper = new DynamoDBMapper(ryanClient);

    }

    /**
     * Initialize the cached reviews field
     * This is different from updateCachedReviews() because it doesn't notify of a data change
     */
    private void initializeCachedReviews(){

        AsyncListUpdate updater = new AsyncListUpdate();
        updater.execute(currentCompany, ReviewResultsFragment.SORT_BY_DATE_NEWEST, ReviewResultsFragment.SHOW_ALL);
        try{
            cachedReviews = updater.get();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

    /**
     * This method will update the cachedReviews field with the reviews from the database
     * @return

    private void updateCachedReviews(){

        final AsyncListUpdate updater = new AsyncListUpdate();
        updater.execute(currentCompany, ReviewResultsFragment.SORT_BY_DATE_NEWEST, ReviewResultsFragment.SHOW_ALL);
        try{
            cachedReviews = updater.get();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        while(updater.getStatus() != AsyncTask.Status.FINISHED){} // Wait until the reviews are fetched
                        notifyCachedReviewsChanged();
                    }
                });
            }
        });
        t.start();

    }*/

    // Notify the activity that the cachedReviews are changed
    private void notifyCachedReviewsChanged(){
        mViewPager.getAdapter().notifyDataSetChanged(); // Update the fragments
    }


    /*
     * Callback methods so that the fragments can interact with the main activity
     * The fragments will call this method to talk to the activity
     */

    /**
     * Callback method for the Company Profile Fragment
     * Returns a list of reviews for the company
     * @return List of reviews for the company
     */
    @Override
    public ArrayList<Review> updateReviews() {

        ArrayList<Review> returnList = dynamoHandler.getCachedReviews(currentCompany);

        return returnList;

    }

    // Update the listAdapter based on the passed criteria
    @Override
    public ArrayList<Review> updateReviews(String SORT, String SHOW) {

        // Should be returning the cached reviews
        ArrayList<Review> returnList = dynamoHandler.getCachedReviews(currentCompany);

        // Do the sorting and filtering
        switch (SORT){
            case ReviewResultsFragment.SORT_BY_DATE_NEWEST:
                Collections.sort(returnList, new Comparator<Review>() {
                    @Override
                    public int compare(Review lhs, Review rhs) {
                        return rhs.getDateCreated().compareTo(lhs.getDateCreated());
                    }
                });
                break;
            case ReviewResultsFragment.SORT_BY_DATE_OLDEST:
                Collections.sort(returnList, new Comparator<Review>() {
                    @Override
                    public int compare(Review lhs, Review rhs) {
                        return lhs.getDateCreated().compareTo(rhs.getDateCreated());
                    }
                });
                break;
            case ReviewResultsFragment.SORT_BY_RATING_HIGHEST:
                Collections.sort(returnList, new Comparator<Review>() {
                    @Override
                    public int compare(Review lhs, Review rhs) {
                        return rhs.getNumStars().compareTo(lhs.getNumStars());
                    }
                });
                break;
            case ReviewResultsFragment.SORT_BY_RATING_LOWEST:
                Collections.sort(returnList, new Comparator<Review>() {
                    @Override
                    public int compare(Review lhs, Review rhs) {
                        return lhs.getNumStars().compareTo(rhs.getNumStars());
                    }
                });
                break;
        }

        // Filter the results
        Calendar relativeCalendar = Calendar.getInstance(); // Use this to filter based on result date
        switch(SHOW){
            case ReviewResultsFragment.SHOW_ALL:
                break;
            case ReviewResultsFragment.SHOW_LAST_WEEK:
                relativeCalendar.set(Calendar.WEEK_OF_MONTH, relativeCalendar.get(Calendar.WEEK_OF_MONTH) - 1);
                for(Iterator<Review> iterator = returnList.iterator(); iterator.hasNext(); ){
                    Review review = iterator.next();
                    if(review.getDateCreated().compareTo(relativeCalendar) <= 0) iterator.remove();
                }
                break;
            case ReviewResultsFragment.SHOW_LAST_MONTH:
                relativeCalendar.set(Calendar.MONTH, relativeCalendar.get(Calendar.MONTH) - 1);
                for(Iterator<Review> iterator = returnList.iterator(); iterator.hasNext(); ){
                    Review review = iterator.next();
                    if(review.getDateCreated().compareTo(relativeCalendar) <= 0) iterator.remove();
                }
                break;
            case ReviewResultsFragment.SHOW_LAST_3_MONTHS:
                relativeCalendar.set(Calendar.MONTH, relativeCalendar.get(Calendar.MONTH) - 3);
                for(Iterator<Review> iterator = returnList.iterator(); iterator.hasNext(); ){
                    Review review = iterator.next();
                    if(review.getDateCreated().compareTo(relativeCalendar) <= 0) iterator.remove();
                }
                break;
            case ReviewResultsFragment.SHOW_LAST_6_MONTHS:
                relativeCalendar.set(Calendar.MONTH, relativeCalendar.get(Calendar.MONTH) - 6);
                for(Iterator<Review> iterator = returnList.iterator(); iterator.hasNext(); ){
                    Review review = iterator.next();
                    if(review.getDateCreated().compareTo(relativeCalendar) <= 0) iterator.remove();
                }
                break;
            case ReviewResultsFragment.SHOW_LAST_12_MONTHS:
                relativeCalendar.set(Calendar.MONTH, relativeCalendar.get(Calendar.MONTH) - 12);
                for(Iterator<Review> iterator = returnList.iterator(); iterator.hasNext(); ){
                    Review review = iterator.next();
                    if(review.getDateCreated().compareTo(relativeCalendar) <= 0) iterator.remove();
                }
                break;
        }

        return returnList;

    }

    /**
     * Used to submit the review to the database from the WriteReviewFragment
     * @param review
     */
    @Override
    public void submitReview(final Review review) {

        dynamoHandler.saveReview(review);
        mViewPager.setCurrentItem(0, true);
        Toast.makeText(this, "Review Submitted", Toast.LENGTH_SHORT).show();

    }

    /**
     * Update the review object in the database
     * @param review
     */
    public void updateReview(final Review review){

        Thread updateReview = new Thread(new Runnable() {
            @Override
            public void run() {
                ryanMapper.save(review);
            }
        });
        updateReview.start();

    }

    /**
     * This method is called when the reviews from DynamoHandler have changed
     * This will update the screens to take the change into account
     */
    @Override
    public void dataChanged() {
        this.mTabsAdapter.notifyDataSetChanged();
    }


    /**
     * This Inner class is used to scan the review database and update the 'recent reviews' list
     * The scan is done in an AsyncTask and updates the ListView on the UI thread
     * UI operations have to be done on the onPreExecute() and onPostExecute() methods
     * The doInBackground() method is used to run the actual database scan
     */
    private class AsyncListUpdate extends AsyncTask<String, String, ArrayList<Review>> {

        @Override
        protected void onPreExecute() {


        }

        @Override
        protected ArrayList<Review> doInBackground(String... params) {

            Calendar relativeCalendar = Calendar.getInstance(); // Use this to filter based on result date
            CalendarToStringMarshaller calendarMarshaller = CalendarToStringMarshaller.instance();  // Marshaller to turn the Calendar into a String
            Condition rangeKeyCondition;
            DynamoDBQueryExpression<Review> queryExpression;

            // Filter by date
            switch (params[2]) {

                case ReviewResultsFragment.SHOW_ALL:
                    queryExpression = new DynamoDBQueryExpression<Review>();
                    break;
                case ReviewResultsFragment.SHOW_LAST_WEEK:
                    relativeCalendar.set(Calendar.WEEK_OF_MONTH, relativeCalendar.get(Calendar.WEEK_OF_MONTH) - 1);
                    rangeKeyCondition = new Condition().withComparisonOperator(ComparisonOperator.GT)
                            .withAttributeValueList(calendarMarshaller.marshall(relativeCalendar));
                    queryExpression = new DynamoDBQueryExpression<Review>().withRangeKeyCondition("Date Created", rangeKeyCondition);
                    break;
                case ReviewResultsFragment.SHOW_LAST_MONTH:
                    relativeCalendar.set(Calendar.MONTH, relativeCalendar.get(Calendar.MONTH) - 1);
                    rangeKeyCondition = new Condition().withComparisonOperator(ComparisonOperator.GT)
                            .withAttributeValueList(calendarMarshaller.marshall(relativeCalendar));
                    queryExpression = new DynamoDBQueryExpression<Review>().withRangeKeyCondition("Date Created", rangeKeyCondition);
                    break;
                case ReviewResultsFragment.SHOW_LAST_3_MONTHS:
                    relativeCalendar.set(Calendar.MONTH, relativeCalendar.get(Calendar.MONTH) - 3);
                    rangeKeyCondition = new Condition().withComparisonOperator(ComparisonOperator.GT)
                            .withAttributeValueList(calendarMarshaller.marshall(relativeCalendar));
                    queryExpression = new DynamoDBQueryExpression<Review>().withRangeKeyCondition("Date Created", rangeKeyCondition);
                    break;
                case ReviewResultsFragment.SHOW_LAST_6_MONTHS:
                    relativeCalendar.set(Calendar.MONTH, relativeCalendar.get(Calendar.MONTH) - 6);
                    rangeKeyCondition = new Condition().withComparisonOperator(ComparisonOperator.GT)
                            .withAttributeValueList(calendarMarshaller.marshall(relativeCalendar));
                    queryExpression = new DynamoDBQueryExpression<Review>().withRangeKeyCondition("Date Created", rangeKeyCondition);
                    break;
                case ReviewResultsFragment.SHOW_LAST_12_MONTHS:
                    relativeCalendar.set(Calendar.MONTH, relativeCalendar.get(Calendar.MONTH) - 12);
                    rangeKeyCondition = new Condition().withComparisonOperator(ComparisonOperator.GT)
                            .withAttributeValueList(calendarMarshaller.marshall(relativeCalendar));
                    queryExpression = new DynamoDBQueryExpression<Review>().withRangeKeyCondition("Date Created", rangeKeyCondition);
                    break;
                default:
                    // Should not get here, if you do that is very bad
                    queryExpression = new DynamoDBQueryExpression<Review>();

                    break;

            }

            // Sort depending on the filters
            switch (params[1]) {
                case ReviewResultsFragment.SORT_BY_DATE_NEWEST:
                    queryExpression.setScanIndexForward(false);
                    break;
                case ReviewResultsFragment.SORT_BY_DATE_OLDEST:
                    queryExpression.setScanIndexForward(true);
                    break;
                case ReviewResultsFragment.SORT_BY_RATING_HIGHEST:
                    queryExpression.setIndexName("ReviewRatingIndex");
                    queryExpression.setScanIndexForward(false);
                    break;
                case ReviewResultsFragment.SORT_BY_RATING_LOWEST:
                    queryExpression.setIndexName("ReviewRatingIndex");
                    queryExpression.setScanIndexForward(true);
                    break;

            }

            Review queryReview = new Review();
            queryReview.setTargetCompanyName(params[0]);

            queryExpression.setHashKeyValues(queryReview);

            PaginatedQueryList<Review> queryResults = ryanMapper.query(Review.class, queryExpression);

            ArrayList<Review> resultsList = new ArrayList<>();
            for (Review review : queryResults) {
                resultsList.add(review);
            }

            return resultsList;
        }

        @Override
        protected void onPostExecute(ArrayList<Review> scanList) {



        }

    }
}


    /**
     * This Inner class is used to scan the review database and update the 'recent reviews' list
     * The scan is done in an AsyncTask and updates the ListView on the UI thread
     * UI operations have to be done on the onPreExecute() and onPostExecute() methods
     * The doInBackground() method is used to run the actual database scan
     *
    private class AsyncListUpdate extends AsyncTask<String, String, ArrayList<Review>>{

        @Override
        protected void onPreExecute(){



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


        }
    }

}*/
