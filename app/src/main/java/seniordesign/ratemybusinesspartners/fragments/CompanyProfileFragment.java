package seniordesign.ratemybusinesspartners.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import seniordesign.ratemybusinesspartners.CompanyProfile;
import seniordesign.ratemybusinesspartners.R;
import seniordesign.ratemybusinesspartners.ViewReview;
import seniordesign.ratemybusinesspartners.adapters.CompactReviewListAdapter;
import seniordesign.ratemybusinesspartners.adapters.ReviewListAdapter;
import seniordesign.ratemybusinesspartners.models.Company;
import seniordesign.ratemybusinesspartners.models.DummyDatabase;
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

    private BarChart mBarChart;

    private CompactReviewListAdapter top5Adapter;
    private CompactReviewListAdapter bottom5Adapter;
    private CompactReviewListAdapter ratedReviewsAdapter;

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

        ArrayList<Review> reviews = mListener.updateReviews();

        // Initialize the Bar Graph for review ratings
        BarData data = new BarData(getXAxisValues(), getDataSet(reviews, returnView.getContext()));

        mBarChart = (BarChart) returnView.findViewById(R.id.companyProfileBarGraph);
        mBarChart.setData(data);

        // Formatting for the Bar Chart
        XAxis xAxis = mBarChart.getXAxis();
        YAxis yAxisLeft = mBarChart.getAxisLeft();
        YAxis yAxisRight = mBarChart.getAxisRight();

        yAxisLeft.setDrawAxisLine(false);
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setDrawLabels(false);

        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setDrawLabels(false);

        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(8f);

        mBarChart.getLegend().setEnabled(false);
        mBarChart.setDescription("");
        mBarChart.getBarData().setValueTextSize(10f);
        mBarChart.getBarData().setValueFormatter(new ValueFormatter() {
            @Override
            // Use this to take off the decimal values on the ratings (i.e. 3.00 -> 3)
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                return Integer.toString(Math.round(v));
            }
        });
        mBarChart.setDragEnabled(false);
        mBarChart.setScaleEnabled(false);
        mBarChart.setPinchZoom(false);
        mBarChart.setHighlightPerDragEnabled(false);
        mBarChart.setDoubleTapToZoomEnabled(false);
        mBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                View topBottom5Layout = getView().findViewById(R.id.companyProfileTopBottom5LinearLayout);
                View ratedReviewsLayout = getView().findViewById(R.id.companyProfileRatedReviewsLayout);
                TextView ratedReviewTextView = (TextView) getView().findViewById(R.id.companyProfileRatedReviewsTextView);
                Log.d("Chart", Integer.toString(entry.getXIndex()));

                ArrayList<Review> reviews = mListener.updateReviews();
                ratedReviewsAdapter.clear();
                for(Review review : reviews){
                    if(Math.round(review.getNumStars()) == entry.getXIndex()) ratedReviewsAdapter.add(review);
                }

                ratedReviewTextView.setText(entry.getXIndex() + " Star Reviews");
                topBottom5Layout.setVisibility(View.GONE);
                ratedReviewsLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected() {
                View topBottom5Layout = getView().findViewById(R.id.companyProfileTopBottom5LinearLayout);
                View ratedReviewsLayout = getView().findViewById(R.id.companyProfileRatedReviewsLayout);

                ratedReviewsLayout.setVisibility(View.GONE);
                topBottom5Layout.setVisibility(View.VISIBLE);
            }
        });

        // Initialize the Top 5 and Bottom 5 lists
        top5Adapter = new CompactReviewListAdapter(returnView.getContext(), new ArrayList<Review>());
        bottom5Adapter = new CompactReviewListAdapter(returnView.getContext(), new ArrayList<Review>());
        ListView top5List = (ListView) returnView.findViewById(R.id.companyProfileTop5ListView);
        ListView bottom5List = (ListView) returnView.findViewById(R.id.companyProfileBottom5ListView);

        top5List.setAdapter(top5Adapter);
        bottom5List.setAdapter(bottom5Adapter);

        //Initialize the ListView to display reviews with a specific rating when the graph is pressed
        ratedReviewsAdapter = new CompactReviewListAdapter(returnView.getContext(), new ArrayList<Review>());
        ListView ratedReviewsList = (ListView) returnView.findViewById(R.id.companyProfileRatedReviewsListView);
        ratedReviewsList.setAdapter(ratedReviewsAdapter);

        // Sort the list to easily take the top 5 and bottom 5
        Collections.sort(reviews, new Comparator<Review>() {
            @Override
            public int compare(Review lhs, Review rhs) {
                if(lhs.getNumStars() > rhs.getNumStars()) return 1;
                else if(lhs.getNumStars() < rhs.getNumStars()) return -1;
                else return 0;
            }
        });

        // Update Top 5
        for(int i = reviews.size() - 1; i > reviews.size() - 6 && i >= 0; i--){
            top5Adapter.add(reviews.get(i));
        }

        // Update Bottom 5
        for(int i = 0; i < 5 && i < reviews.size(); i++){
            bottom5Adapter.add(reviews.get(i));
        }

        // Set adapters for the lists
        AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ArrayAdapter<Review> arrayAdapter = (ArrayAdapter<Review>) parent.getAdapter();

                Intent intent = new Intent(view.getContext(), ViewReview.class);
                intent.putExtra(CompanyProfile.COMPANY_PROFILE_REVIEW_TO_VIEW, arrayAdapter.getItem(position));
                startActivity(intent);
            }
        };

        top5List.setOnItemClickListener(onItemClickListener);
        bottom5List.setOnItemClickListener(onItemClickListener);
        ratedReviewsList.setOnItemClickListener(onItemClickListener);

        return returnView;
    }

    /**
     * Return the reviews for this company as a BarDataSet
     * @return
     */
    private BarDataSet getDataSet(ArrayList<Review> reviews, Context c){

        int counter0 = 0;
        int counter1 = 0;
        int counter2 = 0;
        int counter3 = 0;
        int counter4 = 0;
        int counter5 = 0;

        // Increment the counters for the appropriate rating
        for(Review review : reviews){
            switch(Math.round(review.getNumStars())){
                case 0:
                    counter0 += 1;
                    break;
                case 1:
                    counter1 += 1;
                    break;
                case 2:
                    counter2 += 1;
                    break;
                case 3:
                    counter3 += 1;
                    break;
                case 4:
                    counter4 += 1;
                    break;
                case 5:
                    counter5 += 1;
                    break;
            }
        }

        ArrayList<BarEntry> entries = new ArrayList<>();
        BarEntry entry = new BarEntry(1, 2);
        entries.add(new BarEntry(counter0, 0));
        entries.add(new BarEntry(counter1, 1));
        entries.add(new BarEntry(counter2, 2));
        entries.add(new BarEntry(counter3, 3));
        entries.add(new BarEntry(counter4, 4));
        entries.add(new BarEntry(counter5, 5));

        BarDataSet dataSet = new BarDataSet(entries, "");
        dataSet.setColors(new int[] {R.color.rating_0, R.color.rating_1,
                R.color.rating_2, R.color.rating_3, R.color.rating_4, R.color.rating_5}, c);

        return dataSet;

    }

    /**
     * Return the X labels for the bar graph (0-5)
     * @return
     */
    private ArrayList<String> getXAxisValues(){

        ArrayList<String> xValues = new ArrayList<>();
        xValues.add("0");
        xValues.add("1");
        xValues.add("2");
        xValues.add("3");
        xValues.add("4");
        xValues.add("5");

        return xValues;
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
