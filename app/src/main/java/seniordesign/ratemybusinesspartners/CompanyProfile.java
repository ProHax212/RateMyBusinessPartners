package seniordesign.ratemybusinesspartners;

import android.content.Intent;
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
import android.widget.TextView;

import java.util.ArrayList;

import seniordesign.ratemybusinesspartners.models.DummyDatabase;
import seniordesign.ratemybusinesspartners.models.Review;
import seniordesign.ratemybusinesspartners.models.User;

public class CompanyProfile extends AppCompatActivity {

    public static final String COMPANY_PROFILE_TARGET_COMPANY = "com.ryan.target.company";

    ArrayAdapter<Review> reviewArrayAdapter;
    String currentCompany;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Retrieve Intent inputs
        Intent intent = getIntent();
        currentCompany = intent.getStringExtra(COMPANY_PROFILE_TARGET_COMPANY);
        this.currentUser = intent.getParcelableExtra(MainActivity.CURRENT_USER);

        //Initialize profile information
        TextView companyNameTextView = (TextView) findViewById(R.id.companyNameTextView);
        companyNameTextView.setText(currentCompany);

        //Initialize List View
        ListView reviewList = (ListView) findViewById(R.id.companyProfileReviewList);

        reviewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("ListView", "Item: " + position + " pressed");
                TextView textView = (TextView) view;
                Log.d("ListView", "Text: " + textView.getText().toString());
                Review review = (Review) parent.getItemAtPosition(position);
                Log.d("ListView", "Name: " + review.getReviewer().getUsername());
            }
        });

//        User user = (User) intent.getSerializableExtra("newUser");
//        user.setFirstName("Elizabeth");
//        user.setLastname("Bob");
//        user.setEmail("pika@utexas.edu");
//        user.setCompany("Audi");
//        user.setUsername("blah");
//        user.setPassword("s");
//        user.setConfirmPassword("ble");

        User user = new User("Elizabeth", "Pika", "pika@utexas.edu","wall-e mart", "zergbanger", "1234", "1234");
        Review[] companyReviews = {new Review(user, "This is the best company ever", "Walmart", 5f), new Review(user, "I agree", "Walmart", 3f)};
        reviewArrayAdapter = new ArrayAdapter<Review>(this, R.layout.review_list_item, new ArrayList<Review>());
        reviewList.setAdapter(reviewArrayAdapter);



        Review testReview = new Review(user, "This is a test review, please disregard (btw Abraham is awesome)", "Walmart", 5f);

        for(Review review : DummyDatabase.reviews){
            reviewArrayAdapter.add(review);
        }
    }


    // Navigation Methods
    public void viewAllReviews(View view){
        Intent intent = new Intent(this, ReviewResults.class);
        intent.putExtra(COMPANY_PROFILE_TARGET_COMPANY, this.currentCompany);
        startActivity(intent);
    }

    public void writeReview(View view){
        Intent intent = new Intent(this, WriteReview.class);
        intent.putExtra(COMPANY_PROFILE_TARGET_COMPANY, this.currentCompany);
        intent.putExtra(MainActivity.CURRENT_USER, this.currentUser);
        startActivity(intent);
    }

}
