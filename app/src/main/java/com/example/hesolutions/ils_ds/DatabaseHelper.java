package com.example.hesolutions.ils_ds;

/**
 * Created by hesolutions on 2016-04-14.
 */
import android.content.Context;
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
    private boolean createDatabase;
    private String mDataBaseName;
    public final File mFile;
    private String mSrcDataBaseName;

    public DatabaseHelper(Context context, String dataBaseName, String srcDataBaseName) {
        super(context, dataBaseName, null, DATABASE_VERSION);
        this.createDatabase = false;
        this.context = context;
        this.mDataBaseName = dataBaseName;
        this.mSrcDataBaseName = srcDataBaseName;

        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/horizon_ds");
        File file = new File(dir, this.mSrcDataBaseName);
        this.mFile = file;

        //this.mFile = context.getDatabasePath(this.mDataBaseName);
        /*
        if (!this.mFile.exists()) {
            this.createDatabase = true;
        }
        */
    }

    public synchronized SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase writableDatabase;
        SQLiteDatabase database = super.getWritableDatabase();
        this.createDatabase = true;
        if (this.createDatabase) {
            this.createDatabase = false;
            try {
                database = copyDatabase();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        writableDatabase = database;
        return writableDatabase;
    }

    private SQLiteDatabase copyDatabase() throws IOException {
        InputStream input = this.context.getAssets().open(this.mSrcDataBaseName);
        OutputStream output = new FileOutputStream(this.mFile);
        copy(input, output);
        input.close();
        output.close();
        return super.getWritableDatabase();
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
