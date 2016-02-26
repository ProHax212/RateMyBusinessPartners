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

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import seniordesign.ratemybusinesspartners.adapters.ReviewListAdapter;
import seniordesign.ratemybusinesspartners.comparators.ReviewDateComparator;
import seniordesign.ratemybusinesspartners.comparators.ReviewRatingComparator;
import seniordesign.ratemybusinesspartners.models.Review;
import seniordesign.ratemybusinesspartners.models.User;

public class ReviewResults extends AppCompatActivity {


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

    private String currentCompany;
    private User currentUser;

    private ArrayAdapter<CharSequence> sortBySpinnerAdapter;
    private ArrayAdapter<CharSequence> dateSpinnerAdapter;
    private ReviewListAdapter reviewResultsAdapter;

    // Ryan database variables
    AmazonDynamoDBClient ryanClient;
    DynamoDBMapper ryanMapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        currentCompany = intent.getStringExtra(CompanyProfile.COMPANY_PROFILE_TARGET_COMPANY);
        TextView companyName = (TextView) findViewById(R.id.reviewResultsCompanyName);
        companyName.setText(currentCompany);

        // Initialize Ryan Database
        initializeRyanDatabase();

        // Initialize Filters
        Spinner sortBySpinner = (Spinner) findViewById(R.id.sortBySpinner);
        sortBySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.sort_by_options, R.layout.support_simple_spinner_dropdown_item);
        sortBySpinner.setAdapter(sortBySpinnerAdapter);
        initializeSortBySpinnerListener(sortBySpinner);

        Spinner dateSpinner = (Spinner) findViewById(R.id.dateSpinner);
        dateSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.review_results_date_options, R.layout.support_simple_spinner_dropdown_item);
        dateSpinner.setAdapter(dateSpinnerAdapter);
        initializeDateSpinnerListener(dateSpinner);

        //Initialize Reviews
        ListView reviewResults = (ListView) findViewById(R.id.reviewResultsListView);
        reviewResultsAdapter = new ReviewListAdapter(this, new ArrayList<Review>());
        reviewResults.setAdapter(reviewResultsAdapter);
        reviewResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<Review> arrayAdapter = (ArrayAdapter<Review>) parent.getAdapter();

                Intent intent = new Intent(view.getContext(), ViewReview.class);
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


    // OnItemSelected Listener methods

    private void initializeSortBySpinnerListener(Spinner spinner){

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Sort Spinner", "Position: " + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void initializeDateSpinnerListener(Spinner spinner){

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Date Spinner", "Position: " + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    /**
     * Filter the review results
     * Get the filters from the other views and apply them here
     * @param view The filter button that fired this event
     */
    public void filterResults(View view){

        Spinner sortBySpinner = (Spinner) findViewById(R.id.sortBySpinner);
        Spinner dateSpinner = (Spinner) findViewById(R.id.dateSpinner);

        AsyncListUpdate updater = new AsyncListUpdate();
        updater.execute(SORT_OPTIONS[sortBySpinner.getSelectedItemPosition()], DATE_OPTIONS[dateSpinner.getSelectedItemPosition()]);

    }


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

            Calendar relativeCalendar = Calendar.getInstance(); // Use this to filter based on result date
            CalendarToStringMarshaller calendarMarshaller = CalendarToStringMarshaller.instance();  // Marshaller to turn the Calendar into a String
            Condition rangeKeyCondition;
            DynamoDBQueryExpression<Review> queryExpression;

            // Filter by date
            switch (params[1]){

                case SHOW_ALL:
                    queryExpression = new DynamoDBQueryExpression<Review>();
                    break;
                case SHOW_LAST_WEEK:
                    relativeCalendar.set(Calendar.WEEK_OF_MONTH, relativeCalendar.get(Calendar.WEEK_OF_MONTH) - 1);
                    rangeKeyCondition = new Condition().withComparisonOperator(ComparisonOperator.GT)
                            .withAttributeValueList(calendarMarshaller.marshall(relativeCalendar));
                    queryExpression = new DynamoDBQueryExpression<Review>().withRangeKeyCondition("Date Created", rangeKeyCondition);
                    break;
                case SHOW_LAST_MONTH:
                    relativeCalendar.set(Calendar.MONTH, relativeCalendar.get(Calendar.MONTH) - 1);
                    rangeKeyCondition = new Condition().withComparisonOperator(ComparisonOperator.GT)
                            .withAttributeValueList(calendarMarshaller.marshall(relativeCalendar));
                    queryExpression = new DynamoDBQueryExpression<Review>().withRangeKeyCondition("Date Created", rangeKeyCondition);
                    break;
                case SHOW_LAST_3_MONTHS:
                    relativeCalendar.set(Calendar.MONTH, relativeCalendar.get(Calendar.MONTH) - 3);
                    rangeKeyCondition = new Condition().withComparisonOperator(ComparisonOperator.GT)
                            .withAttributeValueList(calendarMarshaller.marshall(relativeCalendar));
                    queryExpression = new DynamoDBQueryExpression<Review>().withRangeKeyCondition("Date Created", rangeKeyCondition);
                    break;
                case SHOW_LAST_6_MONTHS:
                    relativeCalendar.set(Calendar.MONTH, relativeCalendar.get(Calendar.MONTH) - 6);
                    rangeKeyCondition = new Condition().withComparisonOperator(ComparisonOperator.GT)
                            .withAttributeValueList(calendarMarshaller.marshall(relativeCalendar));
                    queryExpression = new DynamoDBQueryExpression<Review>().withRangeKeyCondition("Date Created", rangeKeyCondition);
                    break;
                case SHOW_LAST_12_MONTHS:
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
            switch (params[0]){
                case SORT_BY_DATE_NEWEST:
                    queryExpression.setScanIndexForward(false);
                    break;
                case SORT_BY_DATE_OLDEST:
                    queryExpression.setScanIndexForward(true);
                    break;
                case SORT_BY_RATING_HIGHEST:
                    queryExpression.setIndexName("ReviewRatingIndex");
                    queryExpression.setScanIndexForward(false);
                    break;
                case SORT_BY_RATING_LOWEST:
                    queryExpression.setIndexName("ReviewRatingIndex");
                    queryExpression.setScanIndexForward(true);
                    break;

            }

            Review queryReview = new Review();
            queryReview.setTargetCompanyName("Walmart");

            queryExpression.setHashKeyValues(queryReview);

            PaginatedQueryList<Review> queryResults = ryanMapper.query(Review.class, queryExpression);

            ArrayList<Review> resultsList = new ArrayList<>();
            for(Review review : queryResults){
                resultsList.add(review);
            }

            return resultsList;
        }

        @Override
        protected void onPostExecute(ArrayList<Review> scanList){

            for(Review review : scanList){
                reviewResultsAdapter.add(review);
            }

        }

    }

}
