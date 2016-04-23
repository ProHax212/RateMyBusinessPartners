package seniordesign.ratemybusinesspartners;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;

import seniordesign.ratemybusinesspartners.dynamodb.DynamoHandler;
import seniordesign.ratemybusinesspartners.models.Company;
import seniordesign.ratemybusinesspartners.models.Review;

public class ViewUserReview extends AppCompatActivity {

    private Review reviewToShow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        reviewToShow = (Review) intent.getSerializableExtra(CompanyProfile.COMPANY_PROFILE_REVIEW_TO_VIEW);
        //reviewToShow = intent.getParcelableExtra(CompanyProfile.COMPANY_PROFILE_REVIEW_TO_VIEW);

        // Set up the DynamoHandler


        // Initialize the views
        TextView reviewerAndDate = (TextView) findViewById(R.id.viewReviewReviewerDateTextView);
        StringBuilder builder = new StringBuilder();
        if(!reviewToShow.getIsUserAnonymous()){
            builder.append(reviewToShow.getReviewer().getUserId());
        }else{
            builder.append("Anonymous");
        }
        DateFormat dateFormat = DateFormat.getDateInstance();
        dateFormat.setCalendar(reviewToShow.getDateCreated());
        builder.append("\n" + dateFormat.format(dateFormat.getCalendar().getTime()));
        reviewerAndDate.setText(builder.toString());

        final TextView reviewText = (TextView) findViewById(R.id.viewReviewTextReview);
        reviewText.setText(reviewToShow.getReviewText());
        reviewText.setMovementMethod(new ScrollingMovementMethod());

        TextView companyName = (TextView) findViewById(R.id.viewReviewCompanyNameTextView);
        companyName.setText(CompanyProfile.currentCompany);

        RatingBar rating = (RatingBar) findViewById(R.id.viewReviewRatingBar);
        rating.setRating(reviewToShow.getNumStars());



        // Set the listener for the like button


    }

}
