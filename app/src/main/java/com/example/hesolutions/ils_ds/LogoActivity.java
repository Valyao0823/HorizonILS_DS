package com.example.hesolutions.ils_ds;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LogoActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        // Initialize the database
        DatabaseManager.getInstance().DatabaseInit(this);
        //Initialize the TCP connection
        //new TCPConnection().execute("AT+DM1=209\n");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(LogoActivity.this, HomePage.class);
        startActivity(intent);
    }
}
