package seniordesign.ratemybusinesspartners.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;

import java.util.ArrayList;

import seniordesign.ratemybusinesspartners.R;
import seniordesign.ratemybusinesspartners.fragments.CompanyProfileFragment;
import seniordesign.ratemybusinesspartners.fragments.ReviewResultsFragment;
import seniordesign.ratemybusinesspartners.fragments.WriteReviewFragment;

/**
 * Created by Ryan Comer on 3/15/2016.
 * This adapter is used to interact with the ViewPager to show the tabs for the CompanyProfile activity
 * Certain methods are overriden to match the intended functionality of the Activity
 */
public class CompanyProfileTabAdapter extends FragmentPagerAdapter{
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Profile", "Reviews", "Write Review" };
    private Context context;

    private SparseArray<Fragment> registeredFragments = new SparseArray<>(); // Array used to hold the fragments attached to the viewPager


    public CompanyProfileTabAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    /**
     * This method will return which fragment needs to be shown based on which tab is selected
     * @param position which tab is currently selected
     * @return the fragment to be shown
     */
    @Override
    public Fragment getItem(int position) {
        switch (position){
            // Profile
            case 0:
                return CompanyProfileFragment.newInstance();
            // Reviews
            case 1:
                return ReviewResultsFragment.newInstance();
            // Write Review
            case 2:
                return WriteReviewFragment.newInstance();
            // Should never reach here
            default:
                return new Fragment();
        }
    }

    @Override
    public int getCount() {
        return this.PAGE_COUNT;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;   // Need this to update the ViewPager after adding a review
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }

    /*@Override
    public void notifyDataSetChanged() {
        // Update the graph
        BarChart barChart = (BarChart) getRegisteredFragment(0).getView().findViewById(R.id.companyProfileBarGraph);
        barChart.notifyDataSetChanged();
        barChart.invalidate();
        super.notifyDataSetChanged();
    }*/
}
