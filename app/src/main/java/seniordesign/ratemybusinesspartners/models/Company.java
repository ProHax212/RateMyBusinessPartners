package seniordesign.ratemybusinesspartners.models;


import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import seniordesign.ratemybusinesspartners.R;

/**
 * Created by Ryan Comer on 3/24/2016.
 * Dummy model for companies until we get access to the D&B database (never)
 */
public class Company {

    private String companyName;
    private int companyImageResource;

    public Company(String name, int companyImageResource) {
        this.companyName = name;
        this.companyImageResource = companyImageResource;
    }

    public void setCompanyName(String companyName){
        this.companyName = companyName;
    }

    public String getCompanyName(){
        return this.companyName;
    }

    /**
     * Return the icon for the company
     */
    public int getCompanyImageResource(){

        return this.companyImageResource;

    }

}
