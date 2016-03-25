package seniordesign.ratemybusinesspartners;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.nio.charset.MalformedInputException;

public class MyAccount extends AppCompatActivity {


    //Abraham Amazon DB
    private DynamoDBMapper mapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        EditText userId_edittext = (EditText) findViewById(R.id.myaccount_userId_edittext);
        EditText email_edittext = (EditText) findViewById(R.id.myaccount_email_edittext);
        EditText company_edittext = (EditText) findViewById(R.id.myaccount_company_edittext);

        userId_edittext.setText(MainActivity.CURRENT_USER.getUserId());
        email_edittext.setText(MainActivity.email);
        company_edittext.setText(MainActivity.CURRENT_USER.getCompany());
    }

}
