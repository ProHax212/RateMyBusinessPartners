package seniordesign.ratemybusinesspartners;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
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
import android.widget.ImageView;
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

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import seniordesign.ratemybusinesspartners.models.User;

public class HomePage extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleSignIn, GoogleApiClient.OnConnectionFailedListener {

    //Nav View
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
    private final int PHOTO = 1;
    private ImageView imageView;
    private EditText userId_edittext;
    private EditText email_edittext;
    private EditText company_edittext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        Toolbar toolbar = (Toolbar) findViewById(R.id.home_page_toolbar);
        imageView = (ImageView) findViewById(R.id.imageView);
        toolbar.setTitle("My Profile");
        Button selectText = (Button) findViewById(R.id.changeImageButton);
        Button myReviews = (Button) findViewById(R.id.button);
        Button companyReviews = (Button) findViewById(R.id.button2);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.home_page_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        initializeGoogleSignIn();
        initializeUserDatabase();

        userId_edittext = (EditText) findViewById(R.id.myaccount_userId_edittext);
        email_edittext = (EditText) findViewById(R.id.myaccount_email_edittext);
        company_edittext = (EditText) findViewById(R.id.myaccount_company_edittext);

        userId_edittext.setText(MainActivity.CURRENT_USER.getUserId());
        email_edittext.setText(MainActivity.email);
        company_edittext.setText(MainActivity.CURRENT_USER.getCompany());
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        SharedPreferences preferences = getSharedPreferences("prof", MODE_PRIVATE);
        String img_str=preferences.getString("profilephoto", "");
        if (!img_str.equals("")){
            //decode string to image
            String base=img_str;
            byte[] imageAsBytes = Base64.decode(base.getBytes(), Base64.DEFAULT);
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));

        }

        navMenu = navigationView.getMenu();
        navigationView.setNavigationItemSelectedListener(this);
        selectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK);
                pickPhotoIntent.setType("image/*");
                startActivityForResult(pickPhotoIntent, PHOTO);
            }
        });
        companyReviews.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intentCompany = new Intent(HomePage.this,CompanyProfile.class);
                intentCompany.putExtra(CompanyProfile.COMPANY_PROFILE_TARGET_COMPANY, MainActivity.CURRENT_USER.getCompany());

                startActivity(intentCompany);
            }
        });
        myReviews.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //Intent intentCompany = new Intent(HomePage.this,CompanyProfile.class);
                //intentCompany.putExtra(CompanyProfile.COMPANY_PROFILE_TARGET_COMPANY, MainActivity.CURRENT_USER.getCompany());

                //startActivity(intentCompany);
                Intent intentUser = new Intent(HomePage.this,UserReviews.class);
                intentUser.putExtra("userProfile", MainActivity.CURRENT_USER);
                intentUser.putExtra("isPublic",false);
                startActivity(intentUser);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.home_page_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.nav_drawer, menu);
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.search_company) {
            Intent intent = new Intent(this, SearchEngine.class);
            startActivity(intent);
        } else if (id == R.id.my_account) {
            if(MainActivity.sign_in_status == MainActivity.Sign_In_Status.SIGNED_IN) {

            } else {
                Toast.makeText(this, "You must be signed in.", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.my_reviews) {
            if(MainActivity.sign_in_status == MainActivity.Sign_In_Status.SIGNED_IN) {
                Intent intentUser = new Intent(HomePage.this,UserReviews.class);
                intentUser.putExtra("userProfile", MainActivity.CURRENT_USER);

                startActivity(intentUser);
            } else {
                Toast.makeText(this, "You must be signed in.", Toast.LENGTH_LONG).show();
            }
        } else if (id == R.id.sign_in_or_out) {
            if (MainActivity.sign_in_status == MainActivity.Sign_In_Status.SIGNED_IN) {
                signOut();
                Intent intent2 = new Intent(this, MainActivity.class);
                startActivity(intent2);
            } else { //Consider disconnect or onStart
                signIn();
            }
        } else if (id == R.id.about) {
            Intent intent = new Intent(this, AboutPage.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.home_page_drawer_layout);
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
        }else if(requestCode == PHOTO){
            if(resultCode == RESULT_OK){
                try{
                    final Uri imageUri = data.getData();
                    final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                   /* imageView.buildDrawingCache();
                    Bitmap bitmap = imageView.getDrawingCache();*/
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    selectedImage.compress(Bitmap.CompressFormat.PNG, 90, stream);
                    byte[] image = stream.toByteArray();
                    String img_str = Base64.encodeToString(image, 0);

                    SharedPreferences preferences = getSharedPreferences("prof", MODE_PRIVATE);
                    SharedPreferences.Editor edit = preferences.edit();
                    edit.putString("profilephoto",img_str);
                    edit.commit();
                    imageView.setImageBitmap(selectedImage);
                }catch(FileNotFoundException e){
                    e.printStackTrace();
                }
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
                        MainActivity.sign_in_status = MainActivity.Sign_In_Status.SIGNED_OUT;
                        sign_in_or_out.setTitle("Sign In");
                        Toast.makeText(HomePage.this, "You have successfully signed out. ", Toast.LENGTH_LONG).show();
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
                Intent intent = new Intent(HomePage.this, SelectCompanyPopUp.class);
                startActivityForResult(intent, RC_COMPANY_SELECTION);
            } else {
                MainActivity.sign_in_status = MainActivity.Sign_In_Status.SIGNED_IN;
                sign_in_or_out.setTitle("Sign Out");
                Toast.makeText(this, "You are signed in as " + MainActivity.CURRENT_USER.getUserId(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(HomePage.this, "Log in was unsuccessful. ", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
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
