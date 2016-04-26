package seniordesign.ratemybusinesspartners;

/**
 * Created by ceenajac on 2/26/2016.
 */

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


import javax.net.ssl.HttpsURLConnection;

import seniordesign.ratemybusinesspartners.models.User;


//I am not sure if I should check about the 8 hour token.
//Would pulling that many times be fine?
public class SearchEngine extends AppCompatActivity implements
        GoogleSignIn, GoogleApiClient.OnConnectionFailedListener,
        NavigationView.OnNavigationItemSelectedListener {
    private SearchView companyEditText;
    private ListView lv;
    private ArrayList<Response> result;
    //ArrayList<String> previouslySearched = new ArrayList<String>();
    String[] items = {"Walmart ( Active ) ","ExxonMobil", "Dell ( Active ) ", "Kroger", "Gorman","Chevron", "Shell", "Google ( Active ) ", "Microsoft ( Active ) ", "SoundCloud", "Facebook"};
    ArrayList<String> databaseCompany = new ArrayList<String>();//ArrayAdapter<ArrayList<String>> adapter
    ArrayAdapter<String> sadapter;

    //nav view
    private NavigationView navigationView = null;
    private MenuItem sign_in_or_out;
    private Menu navMenu;

    //Google Sign In
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;

    //Abraham Amazon DB
    private DynamoDBMapper mapper;

    //Select Company
    private static final int RC_COMPANY_SELECTION = 9002;
    private String userIdToken;
    private String company;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_company);
        this.findAllViewsById();
        addCompanyToList(databaseCompany, items);
       // adapter = new ArrayAdapter<ArrayList<String>>(this, android.R.layout.simple_list_item_1);
        sadapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,items);
        lv.setAdapter(sadapter);
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_engine_toolbar);
        setSupportActionBar(toolbar);
        companyEditText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String text) {
                Intent companyIntent = new Intent(SearchEngine.this, CompanyProfile.class);
               // String submitWord = text + " ( Active )";
                String submitWord = text.toLowerCase();
                for(String comp: items) {
                   String word =  comp.toLowerCase();
                    if (word.contains(submitWord)) {
                        if (comp.contains("( Active )")) {
                            submitWord = comp.replace(" ( Active ) ", "");
                            companyIntent.putExtra(CompanyProfile.COMPANY_PROFILE_TARGET_COMPANY, submitWord);
                            startActivity(companyIntent);
                            return false;
                        } else {
                            displayQuery(" Sorry! Cannot find the company. Search and select an Active company");
                            return false;
                        }

                    }
                }

                displayQuery(" Sorry! Cannot find the company. Search and select an Active company");
                return false;
            }


            @Override
            public boolean onQueryTextChange(String text) {
                sadapter.getFilter().filter(text);
                return false;
            }
        });

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String itext = (String)parent.getItemAtPosition(position);
                Intent companyIntent = new Intent(SearchEngine.this,CompanyProfile.class);
                //Response company = (Response)parent.getItemAtPosition(position);
                if(itext.contains("( Active )")){
                    itext = itext.replace(" ( Active ) ", "");
                    companyIntent.putExtra(CompanyProfile.COMPANY_PROFILE_TARGET_COMPANY, itext);
                    startActivity(companyIntent);
                }else{
                    displayQuery(" Sorry! Cannot find the company. Search and select an Active company");
                }


                /*Do not remove. Need to use for the actual thing*/
                //String authToken = doPostRequest();
                //doGetRequest(authToken, itext);
            }
        });
        /*&searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String authToken = doPostRequest();
                doGetRequest(authToken);
            }
        });*/
        //   String authToken = doPostRequest();
        // doGetRequest(authToken);


        /* Nav View */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.search_engine_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        sign_in_or_out = navMenu.findItem(R.id.sign_in_or_out);

        initializeGoogleSignIn();
        initializeUserDatabase();
    }
    private void findAllViewsById(){
        companyEditText = (SearchView)findViewById(R.id.companyEditText);
        lv = (ListView) findViewById(R.id.searchListView);
        // Nav View
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navMenu = navigationView.getMenu();
    }
    private void displayQuery(CharSequence message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    public String compileUrl(String comp){
        this.findAllViewsById();
        String urlString = null;
        String company = comp;
        if(!company.isEmpty()) { /* Ceena - I changed if(company != null) to if(!company.isEmpty()) b/c the original always returns true. */
            urlString = String.format("https://maxcvservices.dnb.com/V6.2/organizations?KeywordText=%s", Uri.encode(company));
            /*if (!previouslySearched.contains(comp)) {
                previouslySearched.add(comp);
            }*/
        }
        urlString = urlString + "&SearchModeDescription=Basic&findcompany=true";
        return urlString;
    }

    public ArrayList<Response> getResult(ArrayList<Response> result) {
        return result;
    }

//    public void doGetRequest(String token, String company){
//        HttpsURLConnection urlConnection = null;
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//        String urlString = this.compileUrl(company);
//        Response getResponse = new Response();
//        try {
//            URL url = new URL(urlString);
//            urlConnection = (HttpsURLConnection)url.openConnection();
//            urlConnection.setRequestMethod("GET");
//            urlConnection.setRequestProperty("AUTHORIZATION", token);
//            urlConnection.setRequestProperty("Host", "maxcvservices.dnb.com");
//            urlConnection.connect();
//
//            int HttpResult = urlConnection.getResponseCode();
//            String HttpString = urlConnection.getResponseMessage();
//            System.out.println(HttpResult + ":" + HttpString);
//            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
//            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
//            JsonReader jsonReader = new JsonReader(new InputStreamReader(in, "UTF-8"));ArrayList<Response> searchResult = getResponse.parseResponse(jsonReader);
//            Intent intent = new Intent(this, SearchResults.class);
//            intent.putExtra("searchResults",searchResult);
//            startActivity(intent);
//        }catch(Exception e){
//            e.printStackTrace();
//        }finally{
//            urlConnection.disconnect();
//        }
//    }


//    public String doPostRequest(){
//        HttpsURLConnection urlConnection = null;
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);
//        String token=null;
//
//        try {
//            URL url = new URL("https://maxcvservices.dnb.com/Authentication/V2.0/");
//            urlConnection = (HttpsURLConnection)url.openConnection();
//            urlConnection.setDoOutput(true);
//            urlConnection.setRequestMethod("POST");
//            urlConnection.setConnectTimeout(10000);
//
//
//            urlConnection.connect();
//            String HttpResult = urlConnection.getResponseMessage();
//            System.out.println(HttpResult);
//            token = urlConnection.getHeaderField("Authorization");
//            String time = urlConnection.getHeaderField("Date");
//            this.afterEight(time);
//        }catch(Exception e){
//            e.printStackTrace();
//        }finally{
//            urlConnection.disconnect();
//            return token;
//        }
//    }
    private void addCompanyToList(ArrayList<String> dbCompany, String[] comp){
        for(String company:comp){
            dbCompany.add(company);
        }

    }
    private boolean afterEight(String date){
        boolean expired = false;
        Date expDate = toDate(date, "EEE, d MMM yyyy HH:mm:ss zzz");
        long diffInMillisec = new Date().getTime()- expDate.getTime();
        long diffInSec = TimeUnit.MILLISECONDS.toHours(diffInMillisec);
        System.out.println(diffInSec);
        return expired;
    }
    private Date toDate(String sDate, String aFormat){
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(aFormat);
        Date date = simpleDateFormat.parse(sDate, pos);
        return date;

    }

    // Nav View & Related functions
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.search_company) {
//            Intent intent = new Intent(this, SearchEngine.class);
//            startActivity(intent);
        } else if (id == R.id.my_account) {
            if(MainActivity.sign_in_status == MainActivity.Sign_In_Status.SIGNED_IN) {
                Intent intent = new Intent(this, HomePage.class);
                startActivity(intent);
            } else {
                Toast.makeText(this, "You must be signed in.", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.my_reviews) {

            if(MainActivity.sign_in_status == MainActivity.Sign_In_Status.SIGNED_IN) {
                Intent intentUser = new Intent(SearchEngine.this,UserReviews.class);
                intentUser.putExtra("userProfile", MainActivity.CURRENT_USER);

                startActivity(intentUser);
            } else {
                Toast.makeText(this, "You must be signed in.", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.sign_in_or_out) {
            if (MainActivity.sign_in_status == MainActivity.Sign_In_Status.SIGNED_IN) {
                signOut();
            } else { //Consider disconnect or onStart
                signIn();
            }
        } else if (id == R.id.about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.search_engine_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
///        getMenuInflater().inflate(R.menu.nav_drawer, menu);
        sign_in_or_out = navMenu.findItem(R.id.sign_in_or_out);
        if(MainActivity.sign_in_status == MainActivity.Sign_In_Status.SIGNED_IN) {
            sign_in_or_out.setTitle("Sign Out");
        } else {
            sign_in_or_out.setTitle("Sign In");
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.search_engine_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    // Google sign in & Dynamo functions
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
                        User user = new User(userIdToken, company);
                        mapper.save(user);
                    }
                };
                Thread thread = new Thread(runSaveItem);
                thread.start();
                MainActivity.CURRENT_USER.setCompany(company);
                MainActivity.sign_in_status = MainActivity.Sign_In_Status.SIGNED_IN;
                sign_in_or_out.setTitle("Sign Out");
                Toast.makeText(this, "You are signed in as " + MainActivity.CURRENT_USER.getUserId(), Toast.LENGTH_LONG).show();
            }
        }
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
                    }
                });
        MainActivity.sign_in_status = MainActivity.Sign_In_Status.SIGNED_OUT;
        sign_in_or_out.setTitle("Sign In");
        Toast.makeText(this, "You have successfully signed out. ", Toast.LENGTH_LONG).show();

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
        Toast.makeText(this, "You have been disconnected. ", Toast.LENGTH_LONG).show();
    }

    @Override
    public void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            MainActivity.CURRENT_USER = new User(acct.getDisplayName(), company);
            MainActivity.email = acct.getEmail();
            userIdToken = acct.getId();
            Runnable runLoadItem = new Runnable() {
                @Override
                public void run() {
                    User partitionKeyKeyValues = new User();

                    partitionKeyKeyValues.setUserId(userIdToken);
                    DynamoDBQueryExpression<User> queryExpression = new DynamoDBQueryExpression<User>()
                            .withHashKeyValues(partitionKeyKeyValues);

                    List<User> itemList = mapper.query(User.class, queryExpression);
                    if(itemList.size() > 0) {
                        MainActivity.hasCompany = true;
                        MainActivity.CURRENT_USER.setCompany(itemList.get(0).getCompany());
                    }
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
                Intent intent = new Intent(this, SelectCompanyPopUp.class);
                startActivityForResult(intent, RC_COMPANY_SELECTION);
            } else {
                MainActivity.sign_in_status = MainActivity.Sign_In_Status.SIGNED_IN;
                sign_in_or_out.setTitle("Sign Out");
                Toast.makeText(this, "You are signed in as " + MainActivity.CURRENT_USER.getUserId(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Log in was unsuccessful. ", Toast.LENGTH_LONG).show();
            MainActivity.sign_in_status = MainActivity.Sign_In_Status.SIGNED_OUT;
            sign_in_or_out.setTitle("Sign In");
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

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

    private void initializeUserDatabase() {
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
}

