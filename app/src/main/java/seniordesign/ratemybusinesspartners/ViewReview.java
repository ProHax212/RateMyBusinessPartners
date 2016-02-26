package seniordesign.ratemybusinesspartners;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.text.DateFormat;

import seniordesign.ratemybusinesspartners.models.Review;

public class ViewReview extends AppCompatActivity {

    private Review reviewToShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        reviewToShow = intent.getParcelableExtra(CompanyProfile.COMPANY_PROFILE_REVIEW_TO_VIEW);

        // Initialize the views
        TextView reviewerAndDate = (TextView) findViewById(R.id.viewReviewReviewerDateTextView);
        StringBuilder builder = new StringBuilder();
        if(!reviewToShow.getIsUserAnonymous()){
            builder.append(reviewToShow.getReviewer().getUserIdToken());
        }else{
            builder.append("Anonymous");
        }
        DateFormat dateFormat = DateFormat.getDateInstance();
        dateFormat.setCalendar(reviewToShow.getDateCreated());
        builder.append("\n" + dateFormat.format(dateFormat.getCalendar().getTime()));
        reviewerAndDate.setText(builder.toString());

        TextView reviewText = (TextView) findViewById(R.id.viewReviewTextReview);
        reviewText.setText(reviewToShow.getReviewText());
        reviewText.setMovementMethod(new ScrollingMovementMethod());

        TextView companyName = (TextView) findViewById(R.id.viewReviewCompanyNameTextView);
        companyName.setText(CompanyProfile.currentCompany);

        RatingBar rating = (RatingBar) findViewById(R.id.viewReviewRatingBar);
        rating.setRating(reviewToShow.getNumStars());

    }

}
