package seniordesign.ratemybusinesspartners;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

//Google API
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.api.GoogleApiClient;

//AWS API
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DefaultSyncCallback;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

//Dynamo

import java.util.List;

import seniordesign.ratemybusinesspartners.models.User;

public class MainActivity extends AppCompatActivity implements  GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, GoogleSignIn {

    String targetCompany;
    public static User CURRENT_USER = new User("Dummy ID Token", "Walmart");

    //D&B Login Credintials
    public static final String RYAN_DNB_LOGIN_USERNAME = "P200000FC7D9C7FA3D74F55BFF5D6D15";
    public static final String RYAN_DNB_LOGIN_PASSWORD = "ratemybusinesspartners";

    // google sign in
    private TextView mStatusTextView;
    private static final String TAG = "SignInActivity";
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    public enum Sign_In_Status {
        SIGNED_IN, SIGNED_OUT, DISCONNECTED, ON_START;
    };
    public static Sign_In_Status sign_in_status = Sign_In_Status.ON_START;
    //Select Company
    private static final int RC_COMPANY_SELECTION = 9002;
    public static final String SELECTED_COMPANY = "";
    public static boolean hasCompany = false;
    private String userIdToken;
    private String company;

    private DynamoDBMapper mapper;

    //Ryan's Database for Testing
    private DynamoDBMapper ryanMapper;
    private AmazonDynamoDBClient ryanClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        if(sign_in_status == Sign_In_Status.SIGNED_OUT) {
            Toast.makeText(MainActivity.this, "You have successfully signed out.", Toast.LENGTH_LONG).show();
        }
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.disconnect_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);
        findViewById(R.id.continue_without_login).setOnClickListener(this);
        //TextView textView = (TextView)findViewById(R.id.main_company_review_textView);
        //Typeface font = Typeface.createFromAsset(getAssets(),"fonts/BILLY ARGEL TRIAL___.otf_");
        //textView.setTypeface(font);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(getString(R.string.server_client_id))
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        //Amazon Testing
        // Initialize the Amazon Cognito credentials provider
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:f5ba73d3-acbf-45bb-83e2-e4fbe40f269c", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );

        // Initialize the Cognito Sync client
        CognitoSyncManager syncClient = new CognitoSyncManager(
                getApplicationContext(),
                Regions.US_EAST_1, // Region
                credentialsProvider);

        // Create a record in a dataset and synchronize with the server
        Dataset dataset = syncClient.openOrCreateDataset("myDataset");
        dataset.put("myKey", "myValue");
        dataset.synchronize(new DefaultSyncCallback() {
            @Override
            public void onSuccess(Dataset dataset, List newRecords) {
                //Your handler code here
            }
        });

        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        mapper = new DynamoDBMapper(ddbClient);


        //Ryan's initialization for database
        initializeRyanDatabase();

        //Company Profile
        this.targetCompany = "Walmart";
    }

    private void initializeRyanDatabase(){

        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:7af6d1e9-e1a2-45e5-8d91-8fb5be4b70d4", // Identity Pool ID
                Regions.US_EAST_1 // Region
        );

        this.ryanClient = new AmazonDynamoDBClient(credentialsProvider);
        this.ryanMapper = new DynamoDBMapper(ryanClient);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.sign_out_button:
                signOut();
                break;
            case R.id.disconnect_button:
                revokeAccess();
                break;
            case R.id.continue_without_login:
                sign_in_status = Sign_In_Status.SIGNED_OUT;
                switchToHomePage();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else if (requestCode == RC_COMPANY_SELECTION) {
            if(resultCode == 0) {
                Toast.makeText(this, "You must associate your account with a company.", Toast.LENGTH_LONG).show();
                signOut();
            } else {
                data.getData();
                company = data.getStringExtra(SELECTED_COMPANY);
                Runnable runSaveItem = new Runnable() {
                    @Override
                    public void run() {
                        User user = new User();
                        user.setUserIdToken(userIdToken);
                        user.setCompany(company);

                        mapper.save(user);
                    }
                };
                Thread thread = new Thread(runSaveItem);
                thread.start();
            }
        }
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

    public void switchToHomePage() {
        Intent intent = new Intent(this, HomePage.class);
        startActivity(intent);
    }

    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Toast.makeText(MainActivity.this, "You have successfully signed out. ", Toast.LENGTH_LONG).show();
                        sign_in_status = Sign_In_Status.SIGNED_OUT;
                    }
                });
    }

    public void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
//                        updateUI(false);
                    }
                });
        sign_in_status = Sign_In_Status.DISCONNECTED;
        Toast.makeText(MainActivity.this, "You have been disconnected. ", Toast.LENGTH_LONG).show();

    }

    public void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            CURRENT_USER = new User(acct.getDisplayName(), company);
            userIdToken = acct.getId();
            Runnable runLoadItem = new Runnable() {
                @Override
                public void run() {
                    User partitionKeyKeyValues = new User();

                    partitionKeyKeyValues.setUserIdToken(userIdToken);
                    DynamoDBQueryExpression<User> queryExpression = new DynamoDBQueryExpression<User>()
                            .withHashKeyValues(partitionKeyKeyValues);

                    List<User> itemList = mapper.query(User.class, queryExpression);
                    if(itemList.size() > 0) { MainActivity.hasCompany = true; }
                    else { MainActivity.hasCompany = false; }
                }
            };

            Thread thread = new Thread(runLoadItem);
            thread.start();
            try {
                thread.join();
            } catch(Exception e) {
                Log.d("ERROR: AT THREAD.JOIN: ", e.toString());
            }
            if(!MainActivity.hasCompany) {
                Intent intent = new Intent(MainActivity.this, SelectCompanyPopUp.class);
                startActivityForResult(intent, RC_COMPANY_SELECTION);
            }
            sign_in_status = Sign_In_Status.SIGNED_IN;
            Intent intent = new Intent(this, HomePage.class);
            startActivity(intent);

        } else {
            Toast.makeText(MainActivity.this, "Log in was unsuccessful. ", Toast.LENGTH_LONG).show();
            sign_in_status = Sign_In_Status.SIGNED_OUT;
        }
    }

//    public void updateUI(boolean signedIn) {
//        mStatusTextView.postDelayed(new Runnable(){
//            @Override
//            public void run()
//            {
//                mStatusTextView.setVisibility(View.GONE);
//            }
//        }, 10000);
//        if (signedIn) {
//            mStatusTextView.setVisibility(View.VISIBLE);
//            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
//            findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
//            findViewById(R.id.disconnect_button).setVisibility(View.VISIBLE);
//        } else {
//            mStatusTextView.setVisibility(View.VISIBLE);
//            mStatusTextView.setText(R.string.main_textView_signedout);
//            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
//            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
//            findViewById(R.id.disconnect_button).setVisibility(View.GONE);
//        }
//    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
}
