package com.example.android.breweryapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final String QUERY_BREWERYDB_SEARCH = "https://api.brewerydb.com/v2/search?q=%s&type=brewery&key=5f33a523ec77c577d68d0028daf61d16";
    private final String QUERY_BREWERYDB_BREWERY = "https://api.brewerydb.com/v2/brewery/%s/beers?key=5f33a523ec77c577d68d0028daf61d16";
    private final String QUERY_MAPS = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=%s&key=AIzaSyCTGafAV14ZJVXM5NDA4ugpTAi4zVukHtY";
    private final String QUERY_MAPS_PRE = "brewery ";

    private ProgressBar mProgressBar;
    private ListView mBreweriesList;
    private EditText mLocationEditText;
    private Button mSearchButton;
    private int mBreweryPos;
    private Brewery mBrewery;
    private String mBreweryID;

    private String mLocationText;

    private BreweryCatalog mCatalog;

    private final static int BREWERIES_OBTAINED = 1000;
    private final static int ID_OBTAINED = 1001;
    private final static int BEERS_OBTAINED = 1002;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar) findViewById(R.id.pb_progress);

        mLocationEditText = (EditText) findViewById(R.id.et_location);

        // TODO #2 Set up mSearchButton by finding its view, then hooking up a new OnClickListener
        //  which will hide the keyboard, set mLocationText to the text the user has entered in
        //  mLocationEditText, and finally start a Google Maps search thread with the command
        //  'new SearchGoogleMaps().execute();'
        mSearchButton = null;

        mBreweriesList = (ListView) findViewById(R.id.lv_breweries);
        mBreweriesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mBreweryPos = i;
                new RetrieveBreweryIDTask().execute(mCatalog.getBrewery(i).getName());
            }
        });

        mCatalog = BreweryCatalog.getInstance();
    }

    public void hideSoftKeyboard() {
        if(getCurrentFocus()!=null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case BREWERIES_OBTAINED:
                BreweryAdapter adapter = new BreweryAdapter(this, mCatalog.getBreweries());
                mBreweriesList.setAdapter(adapter);
                break;
            case ID_OBTAINED:
                mCatalog.getBrewery(mBreweryPos).setID(mBreweryID);
                new RetrieveBeerListTask().execute();
                break;
            case BEERS_OBTAINED:
                Intent breweryIntent = new Intent(MainActivity.this, BreweryActivity.class);
                breweryIntent.putExtra("breweryID", mBreweryID);
                startActivity(breweryIntent);
                break;
        }
    }

    class SearchGoogleMaps extends AsyncTask<Void, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
            mCatalog.reset();
        }

        protected String doInBackground(Void... urls) {

            try {
                URL url = new URL(String.format(QUERY_MAPS, URLEncoder.encode(QUERY_MAPS_PRE + mLocationText,"UTF-8")));
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            mProgressBar.setVisibility(View.GONE);
            Log.i("INFO", response);

            try {
                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray breweries = object.getJSONArray("results");
                for (int i = 0; i < breweries.length(); i++) {
                    JSONObject brewery = breweries.getJSONObject(i);

                    // TODO #4 Get the name, address, and whether the bar is open from JSON data
                    //  Hint: Try a Postman call using QUERY_MAPS, replacing the '%s%' in QUERY_MAPS
                    //  with something like 'brewery+manhattan+ks' to see how the JSON data is
                    //  organized
                    String name = "Clever Brewery Name";    // Dummy data until fixed
                    String address = "8334 Drunk Dr";       // Dummy data until fixed
                    Boolean open = true;                    // Dummy data until fixed
                    mCatalog.addBrewery(new Brewery(name, address, open));
                }
                onActivityResult(BREWERIES_OBTAINED, -1, null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class RetrieveBreweryIDTask extends AsyncTask<String, Void, String> {

        private Exception exception;


        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(String.format(QUERY_BREWERYDB_SEARCH, URLEncoder.encode(urls[0],"UTF-8")));
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            mProgressBar.setVisibility(View.GONE);
            Log.i("INFO", response);

            try {
                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray breweries = object.getJSONArray("data");
                JSONObject firstBrewery = breweries.getJSONObject(0);
                mBreweryID = firstBrewery.getString("id");
                mBrewery = mCatalog.getBrewery(mBreweryPos);
                mBrewery.setID(mBreweryID);
                mBrewery.setLogoSource(firstBrewery.has("images") ? firstBrewery.getJSONObject("images").getString("large") : null);
                mBrewery.setWebsiteUrl(firstBrewery.has("website") ? firstBrewery.getString("website") : "");
                mBrewery.setDescription(firstBrewery.has("description") ? firstBrewery.getString("description") : "");
                onActivityResult(ID_OBTAINED, -1, null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class RetrieveBeerListTask extends AsyncTask<String, Void, String> {

        private Exception exception;

        protected void onPreExecute() {
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(String.format(QUERY_BREWERYDB_BREWERY, URLEncoder.encode(mBreweryID,"UTF-8")));
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                try {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }
                    bufferedReader.close();
                    return stringBuilder.toString();
                }
                finally{
                    urlConnection.disconnect();
                }
            }
            catch(Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }

        protected void onPostExecute(String response) {
            if(response == null) {
                response = "THERE WAS AN ERROR";
            }
            mProgressBar.setVisibility(View.GONE);
            Log.i("INFO", response);

            try {
                JSONObject object = (JSONObject) new JSONTokener(response).nextValue();
                JSONArray beerList = object.getJSONArray("data");
                for (int i = 0; i < beerList.length(); i++) {
                    JSONObject beer = beerList.getJSONObject(i);
                    mCatalog.addBeer(mBreweryID, new Beer(
                            beer.has("name") ? beer.getString("name") : "",
                            beer.has("labels") ? beer.getJSONObject("labels").getString("medium") : null,
                            beer.has("abv") ? beer.getString("abv") : "",
                            beer.has("description") ? beer.getString("description") : "",
                            beer.has("nameDisplay") ? beer.getString("nameDisplay") : "",
                            beer.getString("id")
                    ));
                }
                onActivityResult(BEERS_OBTAINED, -1, null);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
