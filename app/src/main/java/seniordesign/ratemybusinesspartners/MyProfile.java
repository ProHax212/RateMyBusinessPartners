package seniordesign.ratemybusinesspartners;

import android.content.Intent;
import android.graphics.Color;
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
import java.util.List;
import java.util.Map;

import seniordesign.ratemybusinesspartners.adapters.ReviewListAdapter;
import seniordesign.ratemybusinesspartners.adapters.SearchListAdapter;
import seniordesign.ratemybusinesspartners.comparators.ReviewDateComparator;
import seniordesign.ratemybusinesspartners.comparators.ReviewRatingComparator;
import seniordesign.ratemybusinesspartners.models.Review;
import seniordesign.ratemybusinesspartners.models.User;

public class MyProfile extends AppCompatActivity {
    private ReviewListAdapter reviewResultsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("My Reviews");
        setSupportActionBar(toolbar);


        TextView companyName = (TextView) findViewById(R.id.resultNumber);

        companyName.setTextColor(Color.BLACK);
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

    }

}
