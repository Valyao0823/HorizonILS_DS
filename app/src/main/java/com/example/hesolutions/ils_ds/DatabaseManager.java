package com.example.hesolutions.ils_ds;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.mylibrary.WeekView;
import com.mylibrary.WeekViewEvent;

import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
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
        this.mDBHelper = new DatabaseHelper(context, SRC_DEVICE_DATABASE_NAME);
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

    public String getPassword(String username)
    {
        Cursor cursor = this.mDataBase.rawQuery("SELECT Password FROM UserTable WHERE UserName = ?",  new String[]{String.valueOf(username)});
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        return cursor.getString(0);
    }
    public boolean updateUser(String username, String password) {
        try {
            if (this.mDataBase != null) {
                this.mDataBase.execSQL("UPDATE UserTable SET Password = ? WHERE UserName = ?", new Object[]{String.valueOf(password), String.valueOf(username)});
                return true;
            }
        } catch (Exception e) {
        }
        return $assertionsDisabled;
    }

    public boolean removeUser(String username) {
        try {
            if (this.mDataBase != null) {
                this.mDataBase.execSQL("DELETE FROM UserTable WHERE UserName = ?", new String[]{String.valueOf(username)});
                return true;
            }
        } catch (Exception e) {
        }
        return $assertionsDisabled;
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

    public boolean removeSector(String sectorname)
    {
        try {
            if (this.mDataBase != null) {
                this.mDataBase.execSQL("DELETE FROM SectorTable WHERE SectorName = ?", new Object[]{String.valueOf(sectorname)});
                return true;
            }
        } catch (Exception e) {
        }
        return $assertionsDisabled;
    }
    //========================================Device=======================================

    public boolean addDevice(String devicename, int node, String sectorname, String companyname, String location, String devicetype, String devicedetail)
    {
        Object[] objArr = null;
        if (this.mDataBase != null) {
            objArr = new Object[10];
            objArr[0] = String.valueOf(devicename);
            objArr[1] = Integer.valueOf(node);
            objArr[2] = Integer.valueOf((short)0); // defalut value of intensity
            objArr[3] = Integer.valueOf((short)0); // defalut value of control
            objArr[4] = String.valueOf(sectorname);
            objArr[5] = String.valueOf(companyname);
            objArr[6] = String.valueOf(location);
            objArr[7] = String.valueOf(devicetype);
            objArr[8] = String.valueOf(devicedetail);
            objArr[9] = Integer.valueOf((short)0); // defalut value of feedback
            this.mDataBase.execSQL("INSERT INTO DeviceTable VALUES(?,?,?,?,?,?,?,?,?,?)", objArr);
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

    public ArrayList<Integer> getDeviceNodeList()
    {
        ArrayList<Integer> devicelist = new ArrayList<>();
        Cursor cursor = this.mDataBase.rawQuery("SELECT DeviceNode FROM DeviceTable", null);
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            devicelist.add(cursor.getInt(0));
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

    public Integer getDeviceFeedBack(String devicename)
    {
        int feedback = 0;
        Cursor cursor = this.mDataBase.rawQuery("SELECT FeedBack FROM DeviceTable WHERE DeviceName = ?", new String[]{String.valueOf(devicename)});
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        feedback = cursor.getInt(0);
        cursor.close();
        return  feedback;
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

    public Integer getDeviceNode(String devicename)
    {
        int node = 0;
        Cursor cursor = this.mDataBase.rawQuery("SELECT DeviceNode FROM DeviceTable WHERE DeviceName = ?", new String[]{String.valueOf(devicename)});
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        node = cursor.getInt(0);
        cursor.close();
        return  node;
    }

    public boolean updateDevicefromSector(String devicename, String sectorname)
    {
        try {
            if (this.mDataBase != null) {
                this.mDataBase.execSQL("UPDATE DeviceTable SET SectorName = ? WHERE DeviceName = ?", new String[]{String.valueOf(sectorname),String.valueOf(devicename)});
                return true;
            }
        } catch (Exception e) {
        }
        return $assertionsDisabled;
    }

    public boolean updateDeviceIntensity(String devicename, int intensity, int control)
    {
        try {
            if (this.mDataBase != null) {
                this.mDataBase.execSQL("UPDATE DeviceTable SET Intensity = ?, Control = ? WHERE DeviceName = ?", new Object[]{Integer.valueOf(intensity),Integer.valueOf(control),String.valueOf(devicename)});
                return true;
            }
        } catch (Exception e) {
        }
        return $assertionsDisabled;
    }


    public boolean updateDeviceFeedBack(int node, int feedback)
    {
        try {
            if (this.mDataBase != null) {
                this.mDataBase.execSQL("UPDATE DeviceTable SET FeedBack = ? WHERE DeviceNode = ?", new Object[]{Integer.valueOf(feedback),Integer.valueOf(node)});
                return true;
            }
        } catch (Exception e) {
        }
        return $assertionsDisabled;
    }

    public boolean changeDeviceName(String olddevicename, String newdevicename)
    {
        try {
            if (this.mDataBase != null) {
                this.mDataBase.execSQL("UPDATE DeviceTable SET DeviceName = ? WHERE DeviceName = ?", new Object[]{String.valueOf(newdevicename),String.valueOf(olddevicename)});
                return true;
            }
        } catch (Exception e) {
        }
        return $assertionsDisabled;
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

    public ArrayList<String> showSectors()
    {
        ArrayList<String> sectorlist = new ArrayList<>();
        Cursor cursor = this.mDataBase.rawQuery("SELECT SectorName FROM MappingTable", null);
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

    public boolean removeSectorforuser(String username, String sectorname)
    {
        try {
            if (this.mDataBase != null) {
                this.mDataBase.execSQL("DELETE FROM MappingTable WHERE UserName = ? AND SectorName = ?", new Object[]{String.valueOf(username), String.valueOf(sectorname)});
                return true;
            }
        } catch (Exception e) {
        }
        return $assertionsDisabled;
    }

    public boolean removeUserfromMapping(String username)
    {
        try {
            if (this.mDataBase != null) {
                this.mDataBase.execSQL("DELETE FROM MappingTable WHERE UserName = ?", new Object[]{String.valueOf(username)});
                return true;
            }
        } catch (Exception e) {
        }
        return $assertionsDisabled;
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
            WeekViewEvent weekViewEvent = new WeekViewEvent(cursor.getInt(0), cursor.getString(4), startTime, finishTime,
                    cursor.getInt(5), cursor.getString(3), cursor.getInt(6));
            eventslist.add(weekViewEvent);
            cursor.moveToNext();
        }
        cursor.close();
        return eventslist;
    }

    public boolean removeEvent(WeekViewEvent event)
    {
        try {
            if (this.mDataBase != null) {
                this.mDataBase.execSQL("DELETE FROM EventsTable WHERE ID = ?", new Object[]{Long.valueOf(event.getId())});
                return true;
            }
        } catch (Exception e) {
        }
        return $assertionsDisabled;
    }

    public boolean removeEventforuser(String username)
    {
        try {
            if (this.mDataBase != null) {
                this.mDataBase.execSQL("DELETE FROM EventsTable WHERE Name = ?", new Object[]{String.valueOf(username)});
                return true;
            }
        } catch (Exception e) {
        }
        return $assertionsDisabled;
    }

    public Calendar getEventStart(WeekViewEvent event)
    {
        Calendar startTime = Calendar.getInstance();
        Cursor cursor = this.mDataBase.rawQuery("SELECT StartTime FROM EventsTable WHERE ID = ?", new String[]{String.valueOf(event.getId())});
        cursor.moveToFirst();
        Date st = new Date(cursor.getLong(0));
        startTime.setTime(st);
        cursor.close();
        return startTime;
    }

    public Calendar getEventFinish(WeekViewEvent event)
    {
        Calendar finishTime = Calendar.getInstance();
        Cursor cursor = this.mDataBase.rawQuery("SELECT FinishTime FROM EventsTable WHERE ID = ?", new String[]{String.valueOf(event.getId())});
        cursor.moveToFirst();
        Date ft = new Date(cursor.getLong(0));
        finishTime.setTime(ft);
        cursor.close();
        return finishTime;
    }

    public String getEventSector(WeekViewEvent event)
    {
        String sectorlist;
        Cursor cursor = this.mDataBase.rawQuery("SELECT SectorList FROM EventsTable WHERE ID = ?", new String[]{String.valueOf(event.getId())});
        cursor.moveToFirst();
        sectorlist = cursor.getString(0);
        cursor.close();
        return sectorlist;
    }

    public Integer getEventIntensity(WeekViewEvent event)
    {
        Integer intensity;
        Cursor cursor = this.mDataBase.rawQuery("SELECT Intensity FROM EventsTable WHERE ID = ?", new String[]{String.valueOf(event.getId())});
        cursor.moveToFirst();
        intensity = cursor.getInt(0);
        cursor.close();
        return intensity;
    }

    public boolean updateEvent(long startTime, long finishtTime, String sectorlist, int intensity, long id)
    {
        try {
            if (this.mDataBase != null) {
                this.mDataBase.execSQL("UPDATE EventsTable SET StartTime = ?, FinishTime = ?, SectorList = ?, Intensity = ? WHERE ID = ?", new Object[]{String.valueOf(startTime), String.valueOf(finishtTime),
                        String.valueOf(sectorlist), String.valueOf(intensity), String.valueOf(id)});
                return true;
            }
        } catch (Exception e) {
        }
        return $assertionsDisabled;
    }

    public boolean removeAllevents()
    {
        try {
            if (this.mDataBase != null) {
                this.mDataBase.execSQL("DELETE FROM EventsTable");
                return true;
            }
        } catch (Exception e) {
        }
        return $assertionsDisabled;
    }

    //=============================================Record =======================================================
    public boolean addRecord(long time, String action)
    {
        Object[] objArr = null;
        if (this.mDataBase != null) {
            objArr = new Object[2];
            objArr[0] = Long.valueOf(time);
            objArr[1] = String.valueOf(action);
            this.mDataBase.execSQL("INSERT INTO RecordTable VALUES(?,?)", objArr);
        }
        return $assertionsDisabled;
    }

    public ArrayList showRecord()
    {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        ArrayList<String> recordlist = new ArrayList<>();
        String record = "";
        Cursor cursor = this.mDataBase.rawQuery("SELECT * FROM RecordTable", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Date displaytime = new Date(cursor.getLong(0));
            record =  sdf.format(displaytime) + ": " +cursor.getString(1) + "\n";
            recordlist.add(record);
            cursor.moveToNext();
        }
        cursor.close();
        return recordlist;
    }

    public boolean deleteRecord()
    {
        try {
            if (this.mDataBase != null) {
                this.mDataBase.execSQL("DELETE FROM RecordTable");
                return true;
            }
        } catch (Exception e) {
        }
        return $assertionsDisabled;
    }
}

