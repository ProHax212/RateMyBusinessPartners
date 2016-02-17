package seniordesign.ratemybusinesspartners;

import android.content.Intent;
import android.media.Rating;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import seniordesign.ratemybusinesspartners.models.DummyDatabase;
import seniordesign.ratemybusinesspartners.models.Review;
import seniordesign.ratemybusinesspartners.models.User;

public class WriteReview extends AppCompatActivity {

    private String currentCompany;

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
    }


    /**
     * Submit the review to the database
     * @param view The button that was pressed to submit the review
     */
    public void submitReview(View view){

        EditText reviewEditText= (EditText) findViewById(R.id.writeReviewEditText);
        String reviewText = reviewEditText.getText().toString();

        RatingBar reviewRatingBar = (RatingBar) findViewById(R.id.reviewRatingBar);
        Float numStars = reviewRatingBar.getRating();

        CheckBox remainAnonymousCheckbox = (CheckBox)findViewById(R.id.anonymousCheckbox);

        User reviewer = new User("Dummy ID Token", "Walmart");

        String targetCompany = this.currentCompany;

        Review review = new Review(reviewer, reviewText, targetCompany, numStars, remainAnonymousCheckbox.isChecked());

        DummyDatabase.reviews.add(review);

        // Switch back to Company Profile page
        Intent intent = new Intent(this, CompanyProfile.class);
        intent.putExtra(CompanyProfile.COMPANY_PROFILE_TARGET_COMPANY, this.currentCompany);

        startActivity(intent);

    }


}
