package seniordesign.ratemybusinesspartners;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

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
        TextView reviewText = (TextView) findViewById(R.id.viewReviewTextReview);
        reviewText.setText(reviewToShow.toString());

    }

}
