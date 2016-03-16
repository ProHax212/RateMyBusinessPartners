package seniordesign.ratemybusinesspartners.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import seniordesign.ratemybusinesspartners.CompanyProfile;
import seniordesign.ratemybusinesspartners.R;
import seniordesign.ratemybusinesspartners.ViewReview;
import seniordesign.ratemybusinesspartners.adapters.ReviewListAdapter;
import seniordesign.ratemybusinesspartners.models.Review;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link seniordesign.ratemybusinesspartners.fragments.CompanyProfileFragment.ReviewFragmentListener} interface
 * to handle interaction events.
 * Use the {@link CompanyProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CompanyProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    // TODO: Rename and change types of parameters
    private String mCompanyName;

    private ReviewFragmentListener mListener;

    ReviewListAdapter reviewArrayAdapter;

    public CompanyProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CompanyProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CompanyProfileFragment newInstance() {
        CompanyProfileFragment fragment = new CompanyProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mCompanyName = CompanyProfile.currentCompany;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.fragment_company_profile, container, false);

        // Initialize the company information
        TextView companyNameTextView = (TextView) returnView.findViewById(R.id.companyNameTextView);
        companyNameTextView.setText(this.mCompanyName);

        //Initialize List View
        ListView reviewList = (ListView) returnView.findViewById(R.id.companyProfileReviewList);

        reviewArrayAdapter = new ReviewListAdapter(getContext(), new ArrayList<Review>());
        reviewList.setAdapter(reviewArrayAdapter);
        reviewList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<Review> arrayAdapter = (ArrayAdapter<Review>) parent.getAdapter();

                Intent intent = new Intent(view.getContext(), ViewReview.class);
                intent.putExtra(CompanyProfile.COMPANY_PROFILE_REVIEW_TO_VIEW, arrayAdapter.getItem(position));
                startActivity(intent);
            }
        });

        // Initialize the reviews
        ArrayList<Review> reviews = mListener.updateReviews();

        reviewArrayAdapter.clear();

        for(Review review : reviews){
            reviewArrayAdapter.add(review);
        }

        return returnView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ReviewFragmentListener) {
            mListener = (ReviewFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface ReviewFragmentListener {
        // TODO: Update argument type and name
        ArrayList<Review> updateReviews();
    }
}
