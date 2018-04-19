package com.example.android.breweryapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class BreweryActivity extends AppCompatActivity {

    private ExpandableAdapter mAdapter;
    private TextView mName;
    private ImageView mLogo;
    private TextView mDescription;
    private TextView mWebsite;
    private ExpandableListView mBeerListView;
    private Brewery mBrewery;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brewery);
        String breweryID = (String) getIntent().getExtras().get("breweryID");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mBrewery = BreweryCatalog.getInstance().getBrewery(breweryID);

        mAdapter = new ExpandableAdapter(this, mBrewery.getBeers());

        mBeerListView = (ExpandableListView) findViewById(R.id.expandable_beer_list);
        mBeerListView.setAdapter(mAdapter);

        View header = getLayoutInflater().inflate(R.layout.header, null);

        mName = (TextView) header.findViewById(R.id.brewery_name);
        mName.setText(mBrewery.getName());

        mLogo = (ImageView) header.findViewById(R.id.brewery_logo);

        Picasso.with(this).setLoggingEnabled(true);
        // TODO #5 Use Picasso to load the brewery image URL
        //  Hint: you will need to do something like 'Picasso.with(this)._____._____._____;'. The
        //  first blank will tell what image URL to LOAD, the second blank should set
        //  'R.mipmap.ic_launcher' as a PLACEHOLDER, and the third blank should indicate what
        //  ImageView resource everything should be put INTO

        mDescription = (TextView) header.findViewById(R.id.brewery_description);
        mDescription.setText(mBrewery.getDescription());

        mWebsite = (TextView) header.findViewById(R.id.brewery_website);
        mWebsite.setText(mBrewery.getWebsiteURL());

        mBeerListView.addHeaderView(header);
    }
}