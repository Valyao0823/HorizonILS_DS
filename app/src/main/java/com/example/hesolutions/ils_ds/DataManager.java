package com.example.hesolutions.ils_ds;

import android.graphics.Bitmap;

/**
 * Created by hesolutions on 2016-04-14.
 */
public class DataManager {

    private static DataManager mInstance;
    public static DataManager getInstance() {
        if (mInstance == null) {
            mInstance = new DataManager();
        }
        return mInstance;
    }

    public Bitmap bitmap;
    public Bitmap getBitmap(){return bitmap;}
    public Bitmap setBitmap(Bitmap bitmap){this.bitmap = bitmap; return bitmap;}

    public String username;
    public String getusername()
    {
        return username;
    }
    public String setusername(String name) {this.username = name;return username;}

    public String colorname;
    public String getcolorname()
    {
        return colorname;
    }
    public String setcolorname(String name) {this.colorname = name;return colorname;}

    public String popactivity;
    public String getactivity()
    {
        return popactivity;
    }
    public String setactivity(String s) {this.popactivity = s;return popactivity;}
}
