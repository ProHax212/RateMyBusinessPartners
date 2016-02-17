package seniordesign.ratemybusinesspartners;

import android.app.Activity;
import android.content.Intent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by HP on 2/17/2016.
 */
public class SelectCompanyPopUp extends Activity {
    private Spinner selectCompanySpinner;
    private Button submitButton;
    private String company;
    private final static int COMPANY_SELECTION = 9002;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.select_company_pop_up);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * 0.8), (int) (height * 0.5));

        selectCompanySpinner = (Spinner) findViewById(R.id.select_company_popup_spinner);
        submitButton = (Button) findViewById(R.id.select_company_button);

        ArrayList<String> companies = new ArrayList<String>();
        companies.add("Bose");
        companies.add("Tesla");
        companies.add("D&B");
        companies.add("Toyota");
        companies.add("Google");
        companies.add("Amazon");

        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, companies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        selectCompanySpinner.setAdapter(adapter);
        company = selectCompanySpinner.getSelectedItem().toString();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectCompanyPopUp.this, MainActivity.class);
                intent.putExtra(MainActivity.SELECTED_COMPANY, company);
                setResult(COMPANY_SELECTION, intent);
                finish();
            }
        });
    }
}
