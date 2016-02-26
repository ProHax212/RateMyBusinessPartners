package seniordesign.ratemybusinesspartners;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;


import java.util.ArrayList;

/**
 *
 * Created by Abraham on 2/17/2016.
 */

public class SelectCompanyPopUp extends Activity {
    private AutoCompleteTextView selectCompanyAutoComplete;
    private Button submitButton;
    private String company;
    private RelativeLayout selectCompanyViewGroup;
    private final static int COMPANY_SELECTION = 9002;
    private ArrayList<String> companies;
    DisplayMetrics dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_company_pop_up);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        this.setFinishOnTouchOutside(true);

        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        final int width = dm.widthPixels;
        final int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.5));


        selectCompanyAutoComplete = (AutoCompleteTextView) findViewById(R.id.select_company_popup_autocompletetextview);
        submitButton = (Button) findViewById(R.id.select_company_button);

        companies = new ArrayList<String>();
        companies.add("Bose");
        companies.add("Tesla");
        companies.add("D&B");
        companies.add("Toyota");
        companies.add("Google");
        companies.add("Amazon");


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, companies);
        selectCompanyAutoComplete.setThreshold(1);
        selectCompanyAutoComplete.setAdapter(adapter);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                company = selectCompanyAutoComplete.getText().toString();
                if (companies.contains(company)) {
                    Intent intent = new Intent(SelectCompanyPopUp.this, MainActivity.class);
                    Log.d("POPUP COMPANY: ", company);
                    intent.putExtra(MainActivity.SELECTED_COMPANY, company);
                    setResult(COMPANY_SELECTION, intent);
                    finish();
                } else if(company.isEmpty()) {
                    Toast.makeText(SelectCompanyPopUp.this,
                            "Please enter a company.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(SelectCompanyPopUp.this,
                            "The company you entered is not in the D&B database.\nPlease select again.",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
