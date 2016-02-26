package seniordesign.ratemybusinesspartners;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;

/**
 *
 * Created by Abraham on 2/17/2016.
 */

public class SelectCompanyPopUp extends Activity {
    private AutoCompleteTextView selectCompanySpinner;
    private Button submitButton;
    private String company;
    private RelativeLayout selectCompanyViewGroup;
    private final static int COMPANY_SELECTION = 9002;
    DisplayMetrics dm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_company_pop_up);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.5));


        selectCompanySpinner = (AutoCompleteTextView) findViewById(R.id.select_company_popup_autocompletetextview);
        submitButton = (Button) findViewById(R.id.select_company_button);

        ArrayList<String> companies = new ArrayList<String>();
        companies.add("Bose");
        companies.add("Tesla");
        companies.add("D&B");
        companies.add("Toyota");
        companies.add("Google");
        companies.add("Amazon");


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, companies);
        selectCompanySpinner.setThreshold(1);
        selectCompanySpinner.setAdapter(adapter);

//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, companies);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        selectCompanySpinner.setAdapter(adapter);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                company = selectCompanySpinner.getText().toString();
                Intent intent = new Intent(SelectCompanyPopUp.this, MainActivity.class);
                Log.d("POPUP COMPANY: ", company);
                intent.putExtra(MainActivity.SELECTED_COMPANY, company);
                setResult(COMPANY_SELECTION, intent);
                finish();
            }
        });


    }
}
