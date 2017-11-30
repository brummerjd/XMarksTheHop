package com.example.android.breweryapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class BreweryAdapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Brewery> mBreweries;

    public BreweryAdapter(Context context, ArrayList<Brewery> breweries) {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mBreweries = breweries;
    }

    public int getCount() {
        return mBreweries.size();
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        View rowView = mInflater.inflate(R.layout.list_item_brewery, viewGroup, false);

        Brewery brewery = (Brewery) getItem(i);

        TextView nameTextView = (TextView) rowView.findViewById(R.id.tv_name);
        nameTextView.setText(brewery.getName());

        TextView addressTextView = (TextView) rowView.findViewById(R.id.tv_address);
        addressTextView.setText(brewery.getAddress());

        TextView openTextView = (TextView) rowView.findViewById(R.id.tv_open);
        openTextView.setText(brewery.getOpen());

        return rowView;
    }

    public Object getItem(int i) { return mBreweries.get(i); }
    public long getItemId(int i) { return i; }
}