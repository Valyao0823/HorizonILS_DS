package com.example.hesolutions.ils_ds;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.mylibrary.WeekView;
import com.mylibrary.WeekViewEvent;

import java.io.File;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by hesolutions on 2016-04-14.
 */
public class DatabaseManager {
    static final /* synthetic */ boolean $assertionsDisabled;
    private static final String SRC_DEVICE_DATABASE_NAME = "horizon_ils_dd.db";
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
            objArr[0] = String.valueOf(username);
            objArr[1] = String.valueOf(password);
            objArr[2] = String.valueOf(color);
            this.mDataBase.execSQL("INSERT INTO UserTable VALUES(?,?,?)", objArr);
        }
        return $assertionsDisabled;
    }
    public ArrayList<String> getUserNameList()
    {
        ArrayList<String> usernamelist = new ArrayList<>();
        Cursor cursor = this.mDataBase.rawQuery("SELECT UserName FROM UserTable", null);
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
        Cursor cursor = this.mDataBase.rawQuery("SELECT Password FROM UserTable", null);
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
        Cursor cursor = this.mDataBase.rawQuery("SELECT UserName FROM UserTable WHERE Password = ?",  new String[]{String.valueOf(password)});
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        return cursor.getString(0);
    }

    public String getColor(String password)
    {
        Cursor cursor = this.mDataBase.rawQuery("SELECT Color FROM UserTable WHERE Password = ?",  new String[]{String.valueOf(password)});
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        return cursor.getString(0);
    }
    //========================================Sector=======================================

    public boolean addSector(String sectorname)
    {
        Object[] objArr = null;
        if (this.mDataBase != null) {
            objArr = new Object[1];
            objArr[0] = String.valueOf(sectorname);
            this.mDataBase.execSQL("INSERT INTO SectorTable VALUES(?)", objArr);
        }
        return $assertionsDisabled;
    }

    public ArrayList<String> getSectorList()
    {
        ArrayList<String> sectorlist = new ArrayList<>();
        Cursor cursor = this.mDataBase.rawQuery("SELECT SectorName FROM SectorTable", null);
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

    public boolean addDevice(String devicename, long nude, String sectorname)
    {
        Object[] objArr = null;
        if (this.mDataBase != null) {
            objArr = new Object[5];
            objArr[0] = String.valueOf(devicename);
            objArr[1] = Long.valueOf(nude);
            objArr[2] = Short.valueOf((short)0); // defalut value of intensity
            objArr[3] = Short.valueOf((short)0); // defalut value of control
            objArr[4] = String.valueOf(sectorname);
            this.mDataBase.execSQL("INSERT INTO DeviceTable VALUES(?,?,?,?,?)", objArr);
        }
        return $assertionsDisabled;
    }

    public ArrayList<String> getDeviceList()
    {
        ArrayList<String> devicelist = new ArrayList<>();
        Cursor cursor = this.mDataBase.rawQuery("SELECT DeviceName FROM DeviceTable", null);
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

    public ArrayList<String> showDeviceforsector(String sectorname)
    {
        ArrayList<String> devicelist = new ArrayList<>();
        Cursor cursor = this.mDataBase.rawQuery("SELECT DeviceName FROM DeviceTable WHERE SectorName = ?", new String[]{String.valueOf(sectorname)});
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

    public Integer getDeviceIntensity(String devicename)
    {
        int intensity = 0;
        Cursor cursor = this.mDataBase.rawQuery("SELECT Intensity FROM DeviceTable WHERE DeviceName = ?", new String[]{String.valueOf(devicename)});
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        intensity = cursor.getInt(0);
        cursor.close();
        return  intensity;
    }

    public Integer getDeviceControl(String devicename)
    {
        int control = 0;
        Cursor cursor = this.mDataBase.rawQuery("SELECT Control FROM DeviceTable WHERE DeviceName = ?", new String[]{String.valueOf(devicename)});
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        control = cursor.getInt(0);
        cursor.close();
        return  control;
    }
    //============================================Mapping===========================================

    public boolean assignSector(String username, String sectorname)
    {
        Object[] objArr = null;
        if (this.mDataBase != null) {
            objArr = new Object[2];
            objArr[0] = String.valueOf(username);
            objArr[1] = String.valueOf(sectorname);
            this.mDataBase.execSQL("INSERT INTO MappingTable VALUES(?,?)", objArr);
        }
        return $assertionsDisabled;
    }

    public ArrayList<String> showSectorforuser(String username)
    {
        ArrayList<String> sectorlist = new ArrayList<>();
        Cursor cursor = this.mDataBase.rawQuery("SELECT SectorName FROM MappingTable WHERE UserName = ?", new String[]{String.valueOf(username)});
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

    //===============================================Events===========================================================
    public boolean addEvents(long id, long startTime, long finishTime, String sectorlist, String eventname, Integer color, int intensity)
    {
        Object[] objArr = null;
        if (this.mDataBase != null) {
            objArr = new Object[7];
            objArr[0] = Long.valueOf(id);
            objArr[1] = Long.valueOf(startTime);
            objArr[2] = Long.valueOf(finishTime);
            objArr[3] = String.valueOf(sectorlist);
            objArr[4] = String.valueOf(eventname);
            objArr[5] = Integer.valueOf(color);
            objArr[6] = Integer.valueOf(intensity);
            System.out.println("*********** starttime " + startTime);
            this.mDataBase.execSQL("INSERT INTO EventsTable VALUES(?,?,?,?,?,?,?)", objArr);
        }
        return $assertionsDisabled;
    }

    public Long getMaxEventId()
    {
        long MaxId = 0;
        Cursor cursor = this.mDataBase.rawQuery("SELECT MAX(ID) FROM EventsTable", null);
        if (cursor == null) {
            return MaxId;
        }
        cursor.moveToFirst();
        MaxId = cursor.getLong(0);
        cursor.close();
        return MaxId;
    }

    public ArrayList<WeekViewEvent> loadEvent()
    {
        ArrayList<WeekViewEvent> eventslist = new ArrayList<>();
        long today = Calendar.getInstance().getTimeInMillis();
        long past30 = today - 30 * 60 * 60 * 24 * 1000;
        long future30 = today + 30 * 60 * 60 * 24 * 1000;
        Cursor cursor = this.mDataBase.rawQuery("SELECT * FROM EventsTable WHERE StartTime BETWEEN ? AND ?", new String[]{String.valueOf(future30), String.valueOf(past30)});
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Calendar startTime = Calendar.getInstance();
            Date st = new Date(cursor.getLong(1));
            startTime.setTime(st);
            Calendar finishTime = Calendar.getInstance();
            Date ft = new Date(cursor.getLong(2));
            finishTime.setTime(ft);
            System.out.println("******************* " + startTime.getTime());
            WeekViewEvent weekViewEvent = new WeekViewEvent(cursor.getInt(0), cursor.getString(4), startTime, finishTime,
                    cursor.getInt(5), cursor.getString(3), cursor.getInt(6));
            eventslist.add(weekViewEvent);
            cursor.moveToNext();
        }
        cursor.close();
        return eventslist;
    }
}

