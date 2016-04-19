package seniordesign.ratemybusinesspartners.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import seniordesign.ratemybusinesspartners.R;
import seniordesign.ratemybusinesspartners.models.Review;

/**
 * Created by Ryan Comer on 4/4/2016.
 */
public class CompactReviewListAdapter extends ArrayAdapter{
    private final Context context;
    private final ArrayList<Review> modelsArrayList;

    public CompactReviewListAdapter(Context context, ArrayList<Review> modelsArrayList) {

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

        // 3. Get icon,title & counter views from the rowView
        RatingBar reviewRatingBar = (RatingBar) rowView.findViewById(R.id.reviewRatingBar);
        //RelativeLayout likeButtonLayout = (RelativeLayout) rowView.findViewById(R.id.reviewLikeLayout);
        ((ViewGroup) rowView).removeView(reviewRatingBar);  // Rating Bar isn't in the compact version
        //((ViewGroup) rowView).removeView(likeButtonLayout); // Like button isn't in the compact version
        TextView reviewTextView = (TextView) rowView.findViewById(R.id.reviewTextView);

        // 4. Set the text for textView
        Review review = modelsArrayList.get(position);

        reviewTextView.setText(review.compactString());

        // 5. return rowView
        return rowView;
    }
}
