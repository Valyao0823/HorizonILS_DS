package com.example.hesolutions.ils_ds;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by hesolutions on 2016-04-14.
 */
public class DatabaseManager {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final String SRC_DEVICE_DATABASE_NAME = "Horizon.db";
    public DatabaseHelper mDBHelper;
    public SQLiteDatabase mDataBase;
    private static DatabaseManager mInstance;

    public DatabaseManager() {
        this.mDBHelper = null;
        this.mDataBase = null;
    }

    static {
        $assertionsDisabled = !DatabaseManager.class.desiredAssertionStatus() ? true : false;
        mInstance = null;

    }
    public static DatabaseManager getInstance() {
        if (mInstance == null) {
            mInstance = new DatabaseManager();
        }
        return mInstance;
    }


    public void DatabaseInit(Context context) {
        if (this.mDataBase != null) {
            this.mDataBase.close();
            this.mDataBase = null;
        }
        if (this.mDBHelper != null) {
            this.mDBHelper.close();
            this.mDBHelper = null;
        }
        this.mDBHelper = new DatabaseHelper(context, SRC_DEVICE_DATABASE_NAME, SRC_DEVICE_DATABASE_NAME);
        this.mDataBase = this.mDBHelper.getWritableDatabase();
    }

    //=======================================User========================================
    public boolean addUser(String password, String username, String color)
    {
        Object[] objArr = null;
        if (this.mDataBase != null) {
            objArr = new Object[3];
            objArr[0] = String.valueOf(password);
            objArr[1] = String.valueOf(username);
            objArr[2] = String.valueOf(color);
            this.mDataBase.execSQL("INSERT INTO UserInformation VALUES(?,?,?)", objArr);
        }
        return $assertionsDisabled;
    }
    public ArrayList<String> getUserNameList()
    {
        ArrayList<String> usernamelist = new ArrayList<>();
        Cursor cursor = this.mDataBase.rawQuery("SELECT UserName FROM UserInformation", null);
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            usernamelist.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return usernamelist;
    }

    public ArrayList<String> getPasswordList()
    {
        ArrayList<String> passwordlist = new ArrayList<>();
        Cursor cursor = this.mDataBase.rawQuery("SELECT Password FROM UserInformation", null);
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            passwordlist.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return passwordlist;
    }

    public String getUserName(String password)
    {
        Cursor cursor = this.mDataBase.rawQuery("SELECT * FROM UserInformation WHERE Password = ?",  new String[]{String.valueOf(password)});
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        return cursor.getString(1);
    }

    public String getColor(String password)
    {
        Cursor cursor = this.mDataBase.rawQuery("SELECT Color FROM UserInformation WHERE Password = ?",  new String[]{String.valueOf(password)});
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        return cursor.getString(0);
    }
    //========================================Sector=======================================

    public boolean addSector(String sectorname, String username, String devicename)
    {
        Object[] objArr = null;
        if (this.mDataBase != null) {
            objArr = new Object[3];
            objArr[0] = String.valueOf(sectorname);
            objArr[1] = String.valueOf(username);
            objArr[2] = String.valueOf(devicename);
            this.mDataBase.execSQL("INSERT INTO Sector VALUES(?,?,?)", objArr);
        }
        return $assertionsDisabled;
    }

    public ArrayList<String> getSectorList()
    {
        ArrayList<String> sectorlist = new ArrayList<>();
        Cursor cursor = this.mDataBase.rawQuery("SELECT SectorName FROM Sector", null);
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            sectorlist.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return sectorlist;
    }

    //========================================Device=======================================

    public boolean addDevice(String sectorname, String devicename)
    {
        if (this.mDataBase != null) {
            this.mDataBase.execSQL("Update Sector SET DeviceName =? WHERE SectorName = ?", new Object[]{String.valueOf(devicename), String.valueOf(sectorname)});
        }
        return $assertionsDisabled;
    }

    public ArrayList<String> getDeviceList()
    {
        ArrayList<String> devicelist = new ArrayList<>();
        Cursor cursor = this.mDataBase.rawQuery("SELECT DeviceName FROM Sector", null);
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            devicelist.add(cursor.getString(0));
            cursor.moveToNext();
        }
        cursor.close();
        return devicelist;
    }
}

