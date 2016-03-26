package seniordesign.ratemybusinesspartners.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import seniordesign.ratemybusinesspartners.R;
import seniordesign.ratemybusinesspartners.Response;
import seniordesign.ratemybusinesspartners.models.Review;

/**
 * Created by Ryan Comer on 2/12/2016.
 */
public class SearchListAdapter extends ArrayAdapter<Response> {

    private final Context context;
    private final ArrayList<Response> companyArrayList;
    private static class ViewHolder {
        private TextView iView;
    }
    public SearchListAdapter(Context context, List<Response> companyArrayList) {

        super(context, R.layout.search_list_item, companyArrayList);
        this.context = context;
        this.companyArrayList = (ArrayList<Response>)companyArrayList;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
             convertView = LayoutInflater.from(this.getContext())
                    .inflate(R.layout.search_list_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.iView= (TextView) convertView.findViewById(R.id.searchTextView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        Response item = companyArrayList.get(position);
        if(item !=null){
            viewHolder.iView.setTextColor(Color.BLACK);
            viewHolder.iView.setText(String.format("%s \n%s,%s", item.getPrimaryName(),item.getCity(),item.getState()));
        }
        /*LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.search_list_item, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.searchTextView);
        textView.setText(companyArrayList.get(position).getPrimaryName());
        // change the icon for Windows and iPhone*/

        return convertView;
    }


}
