package com.example.hesolutions.ils_ds;

/**
 * Created by hesolutions on 2016-04-14.
 */
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 7;
    private final Context context;
    private String mDataBaseName;
    private File sdFile;
    public DatabaseHelper(Context context, String dataBaseName) {
        super(context, dataBaseName, null, DATABASE_VERSION);
        this.context = context;
        this.mDataBaseName = dataBaseName;
        String state;
        state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File root = Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/horizon_ds");
            if (!dir.exists()) {
                dir.mkdir();
            }
            this.sdFile = new File(dir, this.mDataBaseName);
        }
        //this.sdFile = context.getDatabasePath(this.mDataBaseName);
        /*
        if (this.mFile.exists()) {
            this.createDatabase = true;
        }
        */
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        /*
        SQLiteDatabase writableDatabase;
        SQLiteDatabase database = super.getWritableDatabase();

        try {
            database = copyDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        writableDatabase = database;
        return writableDatabase;
        */
        SQLiteDatabase db;
        SQLiteDatabase database = super.getWritableDatabase();
        if(!this.sdFile.exists()){
            try {
                database = copyDatabase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
            database = SQLiteDatabase.openDatabase(this.sdFile.toString(), null, Context.MODE_APPEND);
        }
        db = database;
        return db;

    }

    private SQLiteDatabase copyDatabase() throws IOException {

        InputStream input = this.context.getAssets().open(this. mDataBaseName);
        OutputStream output = new FileOutputStream(this.sdFile);
        copy(input, output);
        input.close();
        output.close();
        SQLiteDatabase database = SQLiteDatabase.openDatabase(this.sdFile.toString(), null, Context.MODE_PRIVATE);
        return database;
    }

    public void onCreate(SQLiteDatabase db) {
        super.onOpen(db);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    private static int copy(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[AccessibilityNodeInfoCompat.ACTION_SCROLL_FORWARD];
        int count = 0;
        while (true) {
            int n = input.read(buffer);
            if (-1 == n) {
                return count;
            }
            output.write(buffer, 0, n);
            count += n;
        }
    }
}
