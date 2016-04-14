package com.example.hesolutions.ils_ds;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by hesolutions on 2016-04-13.
 */
public class TCPConnection extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... sendmessage) {
        //we create a TCPClient object and
        final TCPClient mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
            @Override
            //here the messageReceived method is implemented
            public void messageReceived(String message) {
                //this method calls the onProgressUpdate
                publishProgress(message);
                Log.i("Debug", "Input message: " + message);
            }
        });
        mTcpClient.setMessage(sendmessage[0]);
        mTcpClient.run();
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        Log.i("onProgressUpdate", values[0]);

    }
}
