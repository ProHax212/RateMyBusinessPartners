package seniordesign.ratemybusinesspartners.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import seniordesign.ratemybusinesspartners.CompanyProfile;
import seniordesign.ratemybusinesspartners.R;
import seniordesign.ratemybusinesspartners.ViewReview;
import seniordesign.ratemybusinesspartners.adapters.ReviewListAdapter;
import seniordesign.ratemybusinesspartners.models.Review;
import seniordesign.ratemybusinesspartners.models.User;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReviewResultsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewResultsFragment extends Fragment implements View.OnClickListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    public static final String SORT_BY_DATE_NEWEST = "ReviewResults.Sort-Date-Newest";
    public static final String SORT_BY_DATE_OLDEST = "ReviewResults.Sort-Date-Oldest";
    public static final String SORT_BY_RATING_HIGHEST = "ReviewResults.Sort-Rating-Highest";
    public static final String SORT_BY_RATING_LOWEST = "ReviewResults.Sort-Rating-Lowest";
    public static final String[] SORT_OPTIONS = {SORT_BY_DATE_NEWEST, SORT_BY_DATE_OLDEST, SORT_BY_RATING_HIGHEST,
            SORT_BY_RATING_LOWEST};

    public static final String SHOW_ALL = "ReviewResults.Show-All";
    public static final String SHOW_LAST_WEEK = "ReviewResults.Show-Last-Week";
    public static final String SHOW_LAST_MONTH = "ReviewResults.Show-Last-Month";
    public static final String SHOW_LAST_3_MONTHS = "ReviewResults.Show-Last-3-Months";
    public static final String SHOW_LAST_6_MONTHS = "ReviewResults.Show-Last-6-Months";
    public static final String SHOW_LAST_12_MONTHS = "ReviewResults.Show-Last-12-Months";
    public static final String[] DATE_OPTIONS = {SHOW_ALL, SHOW_LAST_WEEK, SHOW_LAST_MONTH, SHOW_LAST_3_MONTHS,
            SHOW_LAST_6_MONTHS, SHOW_LAST_12_MONTHS};

    private String currentCompany;
    private User currentUser;

    private ArrayAdapter<CharSequence> sortBySpinnerAdapter;
    private ArrayAdapter<CharSequence> dateSpinnerAdapter;
    private ReviewListAdapter reviewResultsAdapter;

    private UpdateReviews mListener;

    private Spinner sortBySpinner;
    private Spinner dateSpinner;

    public ReviewResultsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ReviewResultsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ReviewResultsFragment newInstance() {
        ReviewResultsFragment fragment = new ReviewResultsFragment();
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
        View returnView = inflater.inflate(R.layout.fragment_review_results, container, false);

        // Initialize Company Name
        TextView companyName = (TextView) returnView.findViewById(R.id.reviewResultsCompanyName);
        companyName.setText(this.currentCompany);

        // Initialize Filters
        sortBySpinner = (Spinner) returnView.findViewById(R.id.sortBySpinner);
        sortBySpinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.sort_by_options, R.layout.support_simple_spinner_dropdown_item);
        sortBySpinner.setAdapter(sortBySpinnerAdapter);
        initializeSortBySpinnerListener(sortBySpinner);

        dateSpinner = (Spinner) returnView.findViewById(R.id.dateSpinner);
        dateSpinnerAdapter = ArrayAdapter.createFromResource(getContext(), R.array.review_results_date_options, R.layout.support_simple_spinner_dropdown_item);
        dateSpinner.setAdapter(dateSpinnerAdapter);
        initializeDateSpinnerListener(dateSpinner);

        //Initialize Reviews
        ListView reviewResults = (ListView) returnView.findViewById(R.id.reviewResultsListView);
        reviewResultsAdapter = new ReviewListAdapter(getContext(), new ArrayList<Review>());
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

        ArrayList<Review> reviews = mListener.updateReviews(SORT_BY_DATE_NEWEST, SHOW_ALL);

        reviewResultsAdapter.clear();
        for(Review review : reviews){
            reviewResultsAdapter.add(review);
        }

        // Set the onClickListener for the Apply Button
        Button applyButton = (Button) returnView.findViewById(R.id.filterButton);
        applyButton.setOnClickListener(this);

        return returnView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UpdateReviews) {
            mListener = (UpdateReviews) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement UpdateReviews");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    // Initialize Spinners
    private void initializeSortBySpinnerListener(Spinner spinner){

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Sort Spinner", "Position: " + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void initializeDateSpinnerListener(Spinner spinner){

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Date Spinner", "Position: " + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    @Override
    public void onClick(View v) {

        ArrayList<Review> reviews = mListener.updateReviews(SORT_OPTIONS[sortBySpinner.getSelectedItemPosition()], DATE_OPTIONS[dateSpinner.getSelectedItemPosition()]);

        this.reviewResultsAdapter.clear();
        for(Review review : reviews){
            reviewResultsAdapter.add(review);
        }
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
    public interface UpdateReviews {
        ArrayList<Review> updateReviews(String SORT, String SHOW);
    }
}
