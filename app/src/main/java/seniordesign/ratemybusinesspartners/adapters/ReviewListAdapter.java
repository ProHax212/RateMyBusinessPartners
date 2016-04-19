package seniordesign.ratemybusinesspartners.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import seniordesign.ratemybusinesspartners.MainActivity;
import seniordesign.ratemybusinesspartners.R;
import seniordesign.ratemybusinesspartners.models.Review;

/**
 * Created by Ryan Comer on 2/12/2016.
 */
public class ReviewListAdapter extends ArrayAdapter<Review> {

    private final Context context;
    private final ArrayList<Review> modelsArrayList;

    public ReviewListAdapter(Context context, ArrayList<Review> modelsArrayList) {

        super(context, R.layout.review_list_item, modelsArrayList);

        this.context = context;
        this.modelsArrayList = modelsArrayList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // 1. Create inflater
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // 2. Get rowView from inflater
        View rowView = inflater.inflate(R.layout.review_list_item, parent, false);

        // 3. Get icon,title & counter views from the rowView and the Review itself
        RatingBar reviewRatingBar = (RatingBar) rowView.findViewById(R.id.reviewRatingBar);
        TextView reviewTextView = (TextView) rowView.findViewById(R.id.reviewTextView);
        //ImageButton likeButton = (ImageButton) rowView.findViewById(R.id.reviewLikeButton);
        //TextView likeButtonTextView = (TextView) rowView.findViewById(R.id.reviewLikeTextView);
        Review review = modelsArrayList.get(position);

        // 4. Set the text for textViews and the rating bar
        reviewTextView.setText(modelsArrayList.get(position).toString());
        reviewRatingBar.setRating(modelsArrayList.get(position).getNumStars());
        //likeButton.setBackgroundResource(0);
        boolean userLikes = false;
        for(String userId : review.getLikedUsers()){
            if(userId.equals(MainActivity.CURRENT_USER.getUserId())) userLikes = true; // The current user likes the review
        }
        /*if(userLikes) likeButtonTextView.setText("Review Liked");
        else likeButtonTextView.setText("Like");

        // 5. Set the onClickListener for the like button
        likeButton.setTag(review);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Review reviewLiked = (Review) v.getTag();
                String currentUser = MainActivity.CURRENT_USER.getUserId();
                if(reviewLiked.getLikedUsers().contains(currentUser)) {
                    reviewLiked.likeReview(currentUser);
                    Log.d("Like", "User: " + currentUser + " liked the review");
                }else{
                    reviewLiked.unlikeReview(currentUser);
                    Log.d("Unlike", "User: " + currentUser + " unliked the review");
                }
            }
        });*/

        // 6. return rowView
        return rowView;
    }

}
