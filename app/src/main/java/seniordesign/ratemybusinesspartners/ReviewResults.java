package seniordesign.ratemybusinesspartners;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

import seniordesign.ratemybusinesspartners.models.Review;
import seniordesign.ratemybusinesspartners.models.User;

public class ReviewResults extends AppCompatActivity {

    private String currentCompany;

    private ArrayAdapter<CharSequence> sortBySpinnerAdapter;
    private ArrayAdapter<Review> reviewResultsAdapter;

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

        //Initialize Reviews
        ListView reviewResults = (ListView) findViewById(R.id.reviewResultsListView);
        reviewResultsAdapter = new ArrayAdapter<Review>(this, R.layout.review_list_item, new ArrayList<Review>());
        reviewResults.setAdapter(reviewResultsAdapter);

        User user = new User("Elizabeth", "Pika", "pika@utexas.edu","wall-e mart", "zergbanger", "1234", "1234");
        Review testReview = new Review(user, "This is a test review, please disregard (btw Abraham is awesome)", "Walmart", 5f);

        for(int i = 0; i < 20; i++){
            reviewResultsAdapter.add(testReview);
        }

        // Initialize Filters
        Spinner sortBySpinner = (Spinner) findViewById(R.id.sortBySpinner);
        sortBySpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.sort_by_options, R.layout.support_simple_spinner_dropdown_item);
        sortBySpinner.setAdapter(sortBySpinnerAdapter);
    }

}
