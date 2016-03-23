package seniordesign.ratemybusinesspartners;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.cognito.CognitoSyncManager;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import org.w3c.dom.Text;

import java.util.List;

import seniordesign.ratemybusinesspartners.models.User;

public class HomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
            GoogleSignIn, GoogleApiClient.OnConnectionFailedListener {
    private NavigationView navigationView = null;
    private Toolbar toolbar = null;
    private MenuItem sign_in_or_out;
    private Menu navMenu;

    // GOOGLE Sign In
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;

    //Abraham Amazon DB
    private DynamoDBMapper mapper;

    //Select Company
    private static final int RC_COMPANY_SELECTION = 9002;
    public static final String SELECTED_COMPANY = "";
    private String userIdToken;
    private String company;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        initializeGoogleSignIn();
        initializeAbrahamDatabase();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navMenu = navigationView.getMenu();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void initializeGoogleSignIn() {
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
    }

    private void initializeAbrahamDatabase() {
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
//        Dataset dataset = syncClient.openOrCreateDataset("myDataset");
//        dataset.put("myKey", "myValue");
//        dataset.synchronize(new DefaultSyncCallback() {
//            @Override
//            public void onSuccess(Dataset dataset, List newRecords) {
//                //Your handler code here
//            }
//        });
        AmazonDynamoDBClient ddbClient = new AmazonDynamoDBClient(credentialsProvider);
        mapper = new DynamoDBMapper(ddbClient);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_home_page_drawer, menu);
        sign_in_or_out = navMenu.findItem(R.id.sign_in_or_out);
        if(MainActivity.sign_in_status == MainActivity.Sign_In_Status.SIGNED_IN) {
            sign_in_or_out.setTitle("Sign Out");
        } else {
            sign_in_or_out.setTitle("Sign In");
        }
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
                company = data.getStringExtra(MainActivity.SELECTED_COMPANY);
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
                MainActivity.sign_in_status = MainActivity.Sign_In_Status.SIGNED_IN;
                sign_in_or_out = navMenu.findItem(R.id.sign_in_or_out);
                sign_in_or_out.setTitle("Sign Out");
                Toast.makeText(this, "You are signed in as " + MainActivity.CURRENT_USER.getUserIdToken(), Toast.LENGTH_LONG).show();
            }
        }
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.sign_in_or_out) {
            if(MainActivity.sign_in_status == MainActivity.Sign_In_Status.SIGNED_IN) {
                signOut();
            } else { //Consider disconnect or onStart
                signIn();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //Test Methods - will not be in final product
    public void switchToCompanyProfile(View view){
        Intent intent = new Intent(this, CompanyProfile.class);
        intent.putExtra(CompanyProfile.COMPANY_PROFILE_TARGET_COMPANY, "Walmart");

        startActivity(intent);
    }

    //Testing the Search
    public void switchToSearchCompany(View view){
        Intent intentSearch = new Intent(this, SearchEngine.class);
        startActivity(intentSearch);
    }

    @Override
    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        MainActivity.sign_in_status = MainActivity.Sign_In_Status.SIGNED_OUT;
                        Intent intent = new Intent(HomePage.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
    }

    @Override
    public void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
//                        updateUI(false);
                    }
                });
        MainActivity.sign_in_status = MainActivity.Sign_In_Status.DISCONNECTED;
        Toast.makeText(HomePage.this, "You have been disconnected. ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            MainActivity.CURRENT_USER = new User(acct.getDisplayName(), company);
            userIdToken = acct.getId();
            Runnable runLoadItem = new Runnable() {
                @Override
                public void run() {
                    User partitionKeyKeyValues = new User();

                    partitionKeyKeyValues.setUserIdToken(userIdToken);
                    DynamoDBQueryExpression<User> queryExpression = new DynamoDBQueryExpression<User>()
                            .withHashKeyValues(partitionKeyKeyValues);

                    List<User> itemList = mapper.query(User.class, queryExpression);
                    if(itemList.size() > 0) {
                        MainActivity.hasCompany = true;
                    }
                    else { MainActivity.hasCompany = false; }
                    Log.d("Has Company", ""+MainActivity.hasCompany);
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
                Intent intent = new Intent(HomePage.this, SelectCompanyPopUp.class);
                startActivityForResult(intent, RC_COMPANY_SELECTION);
            } else {
                MainActivity.sign_in_status = MainActivity.Sign_In_Status.SIGNED_IN;
                sign_in_or_out = navMenu.findItem(R.id.sign_in_or_out);
                sign_in_or_out.setTitle("Sign Out");
                Toast.makeText(this, "You are signed in as " + MainActivity.CURRENT_USER.getUserIdToken(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(HomePage.this, "Log in was unsuccessful. ", Toast.LENGTH_LONG).show();
            MainActivity.sign_in_status = MainActivity.Sign_In_Status.SIGNED_OUT;
            sign_in_or_out = navMenu.findItem(R.id.sign_in_or_out);
            sign_in_or_out.setTitle("Sign In");
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
    }
}
