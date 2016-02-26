package seniordesign.ratemybusinesspartners;

import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import seniordesign.ratemybusinesspartners.models.DummyDatabase;
import seniordesign.ratemybusinesspartners.models.Review;
import seniordesign.ratemybusinesspartners.models.User;

public class WriteReview extends AppCompatActivity {

    private String currentCompany;

    private AmazonDynamoDBClient ryanClient;
    private DynamoDBMapper ryanMapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        // Fill in company name from Intent
        Intent intent = getIntent();
        currentCompany = intent.getStringExtra(CompanyProfile.COMPANY_PROFILE_TARGET_COMPANY);
        TextView companyName = (TextView) findViewById(R.id.writeReviewCompanyTextView);
        companyName.setText(currentCompany);

        // Initialize Database
        initializeRyanDatabase();

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


    /**
     * Submit the review to the database
     * @param view The button that was pressed to submit the review
     */
    public void submitReview(View view){

        EditText reviewEditText= (EditText) findViewById(R.id.writeReviewEditText);
        String reviewText = reviewEditText.getText().toString().trim();

        // Check if review has any text
        if(reviewText.length() == 0){
            Toast.makeText(WriteReview.this, "Please type a review", Toast.LENGTH_SHORT).show();
            return;
        }

        RatingBar reviewRatingBar = (RatingBar) findViewById(R.id.reviewRatingBar);
        Float numStars = reviewRatingBar.getRating();

        CheckBox remainAnonymousCheckbox = (CheckBox)findViewById(R.id.anonymousCheckbox);

        User reviewer = MainActivity.CURRENT_USER; //new User("Dummy ID Token", "Walmart");

        String targetCompany = this.currentCompany;

        final Review review = new Review(reviewer, reviewText, targetCompany, numStars, remainAnonymousCheckbox.isChecked());

        Thread submitReviewThread = new Thread(new Runnable() {
            @Override
            public void run() {

                ryanMapper.save(review);

            }
        });
        submitReviewThread.start();

        // Switch back to Company Profile page
        Intent intent = new Intent(this, CompanyProfile.class);
        intent.putExtra(CompanyProfile.COMPANY_PROFILE_TARGET_COMPANY, this.currentCompany);

        startActivity(intent);

    }


}
