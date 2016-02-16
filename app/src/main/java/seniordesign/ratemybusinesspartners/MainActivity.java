package seniordesign.ratemybusinesspartners;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import seniordesign.ratemybusinesspartners.models.User;

public class MainActivity extends AppCompatActivity {

    String targetCompany;
    User currentUser;

    public static final String CURRENT_USER = "com.ryan.current.user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.targetCompany = "Walmart";
        this.currentUser = new User("Ryan", "Comer", "ProHax", "ryancomer94@gmail.com", "University of Texas", "1234", "1234");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //Test Methods - will not be in final product
    public void switchToCompanyProfile(View view){
        Intent intent = new Intent(this, CompanyProfile.class);
        intent.putExtra(CompanyProfile.COMPANY_PROFILE_TARGET_COMPANY, "Walmart");
        intent.putExtra(this.CURRENT_USER, this.currentUser);

        startActivity(intent);
    }

    public void switchToLogin(View view){
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
    }
}
