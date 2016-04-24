package seniordesign.ratemybusinesspartners.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.util.ArrayList;

import seniordesign.ratemybusinesspartners.CompanyProfile;
import seniordesign.ratemybusinesspartners.MainActivity;
import seniordesign.ratemybusinesspartners.R;
import seniordesign.ratemybusinesspartners.models.Review;
import seniordesign.ratemybusinesspartners.models.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WriteReviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WriteReviewFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private String currentCompany;

    private SubmitReview mListener;

    public WriteReviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment WriteReviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WriteReviewFragment newInstance() {
        WriteReviewFragment fragment = new WriteReviewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        this.currentCompany = CompanyProfile.currentCompany;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View returnView = inflater.inflate(R.layout.fragment_write_review, container, false);

        /*TextView companyName = (TextView) returnView.findViewById(R.id.writeReviewCompanyTextView);
        companyName.setText(this.currentCompany);*/

        // Set the listener for the submit button
        Button submitButton = (Button) returnView.findViewById(R.id.submitReviewButton);
        submitButton.setOnClickListener(this);

        return returnView;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SubmitReview) {
            mListener = (SubmitReview) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SubmitReview");
        }
    }

    @Override
    public void onClick(View v) {

        EditText reviewEditText= (EditText) getView().findViewById(R.id.writeReviewEditText);
        String reviewText = reviewEditText.getText().toString().trim();

        // Check if review has any text
        if(reviewText.length() == 0){
            Toast.makeText(getContext(), "Please type a review", Toast.LENGTH_SHORT).show();
            return;
        }

        RatingBar reviewRatingBar = (RatingBar) getView().findViewById(R.id.reviewRatingBar);
        Float numStars = reviewRatingBar.getRating();

        CheckBox remainAnonymousCheckbox = (CheckBox)getView().findViewById(R.id.anonymousCheckbox);

        User reviewer = MainActivity.CURRENT_USER; //new User("Dummy ID Token", "Walmart");

        String targetCompany = this.currentCompany;

        final Review review = new Review(reviewer, reviewText, targetCompany, numStars, remainAnonymousCheckbox.isChecked());

        // Clear the user input
        reviewEditText.setText("");
        reviewRatingBar.setRating(0f);
        remainAnonymousCheckbox.setChecked(false);

        mListener.submitReview(review);



    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface SubmitReview {
        void submitReview(Review review);
    }
}
