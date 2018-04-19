package com.example.android.breweryapp;

public class Beer {
    private String mName;
    private String mImageURL;
    private String mABV;
    private String mDescription;
    private String mStyle;
    private String mID;

    public Beer(String name, String imageURL, String ABV, String description, String style, String ID) {
        mName = name;
        mImageURL = imageURL;
        mABV = ABV;
        mDescription = description;
        mStyle = style;
        mID = ID;
    }

    // TODO #3 Get all of our getters getting right
    public String getName() {return "Magic Sauce";}
    public String getImageURL() {return "";}
    public String getABV() {return "";}
    public String getDescription() {return "It's like, malty, and there's hops and stuff";}
    public String getStyle() {return "Uhhh, it's beer";}
    public String getID() {return "";}

    @Override
    public String toString() {
        return mName;
    }
}
