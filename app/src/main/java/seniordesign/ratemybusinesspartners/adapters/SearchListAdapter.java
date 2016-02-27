package seniordesign.ratemybusinesspartners.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import seniordesign.ratemybusinesspartners.R;
import seniordesign.ratemybusinesspartners.Response;
import seniordesign.ratemybusinesspartners.models.Review;

/**
 * Created by Ryan Comer on 2/12/2016.
 */
public class SearchListAdapter extends ArrayAdapter<Response> {

    private final Context context;
    private final ArrayList<Response> companyArrayList;

    public SearchListAdapter(Context context, ArrayList<Response> companyArrayList) {

        super(context, R.layout.search_list_item, companyArrayList);
        this.context = context;
        this.companyArrayList = companyArrayList;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.search_list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.searchTextView);
        textView.setText(companyArrayList.get(position).getPrimaryName());
        // change the icon for Windows and iPhone

        return rowView;
    }


}
