package seniordesign.ratemybusinesspartners;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import seniordesign.ratemybusinesspartners.models.Review;
import seniordesign.ratemybusinesspartners.models.User;

public class CompanyProfile extends AppCompatActivity {

    ArrayAdapter<Review> reviewArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initialize List View
        ListView reviewList = (ListView) findViewById(R.id.companyProfileReviewList);
        User user = new User("ProHax", "1234", "ryancomer94@gmail.com");
        Review[] companyReviews = {new Review(user, "This is the best company ever", "Walmart", 5), new Review(user, "I agree", "Walmart", 3)};
        reviewArrayAdapter = new ArrayAdapter<Review>(this, android.R.layout.simple_list_item_1, new ArrayList<Review>());
        reviewList.setAdapter(reviewArrayAdapter);

        Review testReview = new Review(user, "Test Review", "Test Review Co", 5);

        for(int i = 0; i < 10; i++){
            reviewArrayAdapter.add(testReview);
        }
    }

}
