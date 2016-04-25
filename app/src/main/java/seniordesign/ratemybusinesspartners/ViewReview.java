package seniordesign.ratemybusinesspartners;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;

import seniordesign.ratemybusinesspartners.dynamodb.DynamoHandler;
import seniordesign.ratemybusinesspartners.models.Company;
import seniordesign.ratemybusinesspartners.models.Review;

public class ViewReview extends AppCompatActivity {

    private Review reviewToShow;
    private DynamoHandler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_review);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        reviewToShow = (Review) intent.getSerializableExtra(CompanyProfile.COMPANY_PROFILE_REVIEW_TO_VIEW);
        //reviewToShow = intent.getParcelableExtra(CompanyProfile.COMPANY_PROFILE_REVIEW_TO_VIEW);

        // Set up the DynamoHandler
        this.handler = DynamoHandler.getInstance(this.getBaseContext());

        // Initialize the views
        TextView reviewerAndDate = (TextView) findViewById(R.id.viewReviewReviewerDateTextView);
        StringBuilder builder = new StringBuilder();
        if(!reviewToShow.getIsUserAnonymous()){
            builder.append(reviewToShow.getReviewer().getUserId());

            reviewerAndDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent viewUser = new Intent(ViewReview.this,UserReviews.class);
                    viewUser.putExtra("userProfile", reviewToShow.getReviewer());
                    viewUser.putExtra("isPublic",true);
                    startActivity(viewUser);
                }
            });

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

        // Check if the user has liked the review
        ImageButton likeButton = (ImageButton) findViewById(R.id.viewReviewLikeButton);
        final TextView likeButtonText = (TextView) findViewById(R.id.viewReviewLikeTextView);
        String currentUserID = MainActivity.CURRENT_USER.getUserId();
        if(reviewToShow.getLikedUsers().contains(currentUserID)){
            likeButtonText.setText("Review Liked");
        }else{
            likeButtonText.setText("Like");
        }

        // Put the number of likes
        TextView numberOfLikesTextView = (TextView) findViewById(R.id.viewReviewNumberOfLikesTextView);
        int numberOfLikes = reviewToShow.getNumLikes();
        if(numberOfLikes != 1) numberOfLikesTextView.setText("\t|\t" + numberOfLikes + " Likes");
        else numberOfLikesTextView.setText("\t|\t" + numberOfLikes + " Like");

        // Set the listener for the like button
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentUserID = MainActivity.CURRENT_USER.getUserId();
                if(reviewToShow.getLikedUsers().contains(currentUserID)){
                    if(reviewToShow.unlikeReview(currentUserID)){
                        likeButtonText.setText("Like");
                        handler.saveReview(reviewToShow);
                        Toast.makeText(ViewReview.this, "Review Unliked", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    if(reviewToShow.likeReview(currentUserID)){
                        likeButtonText.setText("Review Liked");
                        handler.saveReview(reviewToShow);
                        Toast.makeText(ViewReview.this, "Review Liked", Toast.LENGTH_SHORT).show();
                    }
                }

                RelativeLayout parentLayout = (RelativeLayout) v.getParent();
                TextView likesTextView = (TextView) parentLayout.findViewById(R.id.viewReviewNumberOfLikesTextView);
                if(reviewToShow.getNumLikes() != 1) likesTextView.setText("\t|\t" + reviewToShow.getNumLikes() + " Likes");
                else likesTextView.setText("\t|\t" + reviewToShow.getNumLikes() + " Like");
            }
        });

    }

}
