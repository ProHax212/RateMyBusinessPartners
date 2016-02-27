package seniordesign.ratemybusinesspartners;

/**
 * Created by ceenajac on 2/26/2016.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import java.util.concurrent.TimeUnit;


import javax.net.ssl.HttpsURLConnection;


//I am not sure if I should check about the 8 hour token.
//Would pulling that many times be fine?
public class SearchEngine extends AppCompatActivity {
    private EditText companyEditText;
    private Button searchButton;
    private ArrayList<Response> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_company);
        this.findAllViewsById();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String authToken = doPostRequest();
                doGetRequest(authToken);
            }
        });
        //   String authToken = doPostRequest();
        // doGetRequest(authToken);
    }
    private void findAllViewsById(){
        companyEditText = (EditText)findViewById(R.id.companyEditText);
        searchButton = (Button)findViewById(R.id.searchButton);
    }
    private void displayQuery(CharSequence message){
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    public String compileUrl(){
        this.findAllViewsById();
        String urlString = null;
        String company = companyEditText.getText().toString();
        if(!company.isEmpty()){ /* Ceena - I changed if(company != null) to if(!company.isEmpty()) b/c the original always returns true. */
            urlString = String.format("https://maxcvservices.dnb.com/V6.2/organizations?KeywordText=%s", Uri.encode(company));
        }
        urlString = urlString + "&SearchModeDescription=Basic&findcompany=true";
        return urlString;
    }

    public ArrayList<Response> getResult(ArrayList<Response> result) {
        return result;
    }

    public void doGetRequest(String token){
        HttpsURLConnection urlConnection = null;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String urlString = this.compileUrl();
        Response getResponse = new Response();
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpsURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("AUTHORIZATION", token);
            urlConnection.setRequestProperty("Host", "maxcvservices.dnb.com");
            urlConnection.connect();

            int HttpResult = urlConnection.getResponseCode();
            String HttpString = urlConnection.getResponseMessage();
            System.out.println(HttpResult+ ":" + HttpString);
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            JsonReader jsonReader = new JsonReader(new InputStreamReader(in, "UTF-8"));
            ArrayList<Response> searchResult = getResponse.parseResponse(jsonReader);
            Intent intent = new Intent(this, SearchResults.class);
            intent.putExtra("searchResults", searchResult);
            startActivity(intent);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            urlConnection.disconnect();
        }
    }


    public String doPostRequest(){
        HttpsURLConnection urlConnection = null;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String token=null;

        try {
            URL url = new URL("https://maxcvservices.dnb.com/Authentication/V2.0/");
            urlConnection = (HttpsURLConnection)url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(10000);
            urlConnection.setRequestProperty("Host", "maxcvservices.dnb.com");
            urlConnection.setRequestProperty("x-dnb-user", "P200000B81FD90B010D4C83B9776AAF1");
            urlConnection.setRequestProperty("x-dnb-pwd", "SeniorProject!");

            urlConnection.connect();
            String HttpResult = urlConnection.getResponseMessage();
            System.out.println(HttpResult);
            token = urlConnection.getHeaderField("Authorization");
            String time = urlConnection.getHeaderField("Date");
            this.afterEight(time);
        }catch(Exception e){
            e.printStackTrace();
        }finally{
            urlConnection.disconnect();
            return token;
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
        Date date = simpleDateFormat.parse(sDate,pos);
        return date;

    }
}

