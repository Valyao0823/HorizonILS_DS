package com.example.hesolutions.ils_ds;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LogoActivity extends Activity {
    TCPConnection tcpConnection = new TCPConnection();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        // Initialize the database
        DatabaseManager.getInstance().DatabaseInit(this);
        //Initialize the TCP connection
        /*
        tcpConnection.execute("");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tcpConnection.doInBackground("AT+TXA=200,<100>");
            }
        });
        */
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(LogoActivity.this, HomePage.class);
        startActivity(intent);
    }
}
