package seniordesign.ratemybusinesspartners.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import seniordesign.ratemybusinesspartners.fragments.CompanyProfileFragment;
import seniordesign.ratemybusinesspartners.fragments.ReviewResultsFragment;
import seniordesign.ratemybusinesspartners.fragments.WriteReviewFragment;

/**
 * Created by Ryan Comer on 3/15/2016.
 */
public class CompanyProfileTabAdapter extends FragmentPagerAdapter{
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Profile", "Reviews", "Write Review" };
    private Context context;


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
}
