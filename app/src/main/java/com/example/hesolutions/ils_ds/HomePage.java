package com.example.hesolutions.ils_ds;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.mylibrary.WeekViewEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class HomePage extends Activity {
    TextView CODE1, CODE2, CODE3, CODE4, recorddata;
    GridView gridView;
    Button radioButton1, radioButton2, radioButton3, radioButton4, record;
    boolean jump = false;
    boolean emergency = false;
    Switch switch1;
    ImageView emergencypic;
    Handler myHandler;
    Runnable myRunnable;
    TCPConnection tcpConnection = new TCPConnection();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        CODE1 = (TextView) findViewById(R.id.CODE1);
        CODE2 = (TextView) findViewById(R.id.CODE2);
        CODE3 = (TextView) findViewById(R.id.CODE3);
        CODE4 = (TextView) findViewById(R.id.CODE4);
        gridView = (GridView) findViewById(R.id.gridView);
        radioButton1 = (Button) findViewById(R.id.radioButton1);
        radioButton2 = (Button) findViewById(R.id.radioButton2);
        radioButton3 = (Button) findViewById(R.id.radioButton3);
        radioButton4 = (Button) findViewById(R.id.radioButton4);
        recorddata = (TextView)findViewById(R.id.recorddata);
        record = (Button) findViewById(R.id.record);
        switch1 = (Switch) findViewById(R.id.switch1);
        emergencypic = (ImageView) findViewById(R.id.emergencypic);

        myHandler = new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(HomePage.this, ScreenSaver.class);
                myHandler.removeCallbacks(myRunnable);
                startActivity(intent);
            }
        };
        myHandler.postDelayed(myRunnable, 60*3* 1000);

        final GridView gridView = (GridView) findViewById(R.id.gridView);

        String[] numbers = new String[]{"1", "2", "3",
                "4", "5", "6",
                "7", "8", "9",
                "", "0", ""};

        final ArrayAdapter adapter = new CustomPinCodeAdapter(this, R.layout.homeadpter, numbers);

        gridView.setAdapter(adapter);

        //Initialize the TCP connection

        tcpConnection.execute("");

        Calendar today = Calendar.getInstance();

        final Timer maintimer = new Timer();
        maintimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                    if (emergency == false) {
                        MakeAlert();
                    } else {
                        ArrayList<String> devicelist = DatabaseManager.getInstance().getDeviceList();
                        for (String devicename : devicelist) {
                            // emergency is on: intensity = 100, control = 0
                            int modulenumber = DatabaseManager.getInstance().getDeviceNude(devicename);
                            tcpConnection.doInBackground("AT+TXA="+modulenumber+"<100>");
                        }
                    }
            }
        }, today.getTime(), 1000 * 20);


        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (switch1.isChecked() == true) {
                    emergency = true;
                    emergencypic.setVisibility(View.VISIBLE);
                    ArrayList<String> devicelist = DatabaseManager.getInstance().getDeviceList();
                    switch1.setText("Emergency ON  ");
                    gridView.setAdapter(null);
                    myHandler.removeCallbacks(myRunnable);
                    // emergency is on: intensity = 100, control = 0
                    for (String devicename : devicelist) {
                        int modulenumber = DatabaseManager.getInstance().getDeviceNude(devicename);
                        tcpConnection.doInBackground("AT+TXA=" + modulenumber + "<100>");
                    }
                } else {
                    emergency = false;
                    gridView.setAdapter(adapter);
                    switch1.setText("Emergency OFF  ");
                    emergencypic.setVisibility(View.GONE);
                }
            }

        });

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recorddata.getVisibility() == View.VISIBLE)
                {
                    recorddata.setVisibility(View.INVISIBLE);
                }else {
                    ArrayList<String> data = DatabaseManager.getInstance().showRecord();
                    recorddata.setVisibility(View.VISIBLE);
                    recorddata.setText(data.toString());
                    recorddata.setMovementMethod(new ScrollingMovementMethod());
                }
            }
        });
    }

    public void MakeAlert() {
        Calendar cal = Calendar.getInstance();
        ArrayList<WeekViewEvent> events = DatabaseManager.getInstance().loadEvent();
        ArrayList<String> devicelist = DatabaseManager.getInstance().getDeviceList();
        if (events.size()> 0) {
            for (WeekViewEvent event: events) {
                Calendar eventstart = DatabaseManager.getInstance().getEventStart(event);
                Calendar eventfinish = DatabaseManager.getInstance().getEventFinish(event);
                if (cal.before(eventfinish) && cal.after(eventstart)) {
                    String sectorlist = DatabaseManager.getInstance().getEventSector(event);
                    for (String sub: sectorlist.split(","))
                    {
                        ArrayList<String> devicefromsector = DatabaseManager.getInstance().showDeviceforsector(sub);
                        for (String dev: devicefromsector)
                        {
                            if (devicelist.contains(dev))devicelist.remove(dev);
                            int control = DatabaseManager.getInstance().getDeviceControl(dev);
                            int nude = DatabaseManager.getInstance().getDeviceNude(dev);
                            if (control==1) {
                                int intensity = DatabaseManager.getInstance().getDeviceIntensity(dev);
                                tcpConnection.doInBackground("AT+TXA=" + nude + "<" +intensity + ">");
                            }else
                            {
                                int intensity = DatabaseManager.getInstance().getEventIntensity(event);
                                tcpConnection.doInBackground("AT+TXA=" + nude + "<" +intensity + ">");
                            }
                        }
                    }
                }
            }
            for (String device : devicelist)
            {
                int modulenumber = DatabaseManager.getInstance().getDeviceNude(device);
                tcpConnection.doInBackground("AT+TXA=" + modulenumber + "<0>");
            }
        }else
        {
            for (String device : devicelist)
            {
                int modulenumber = DatabaseManager.getInstance().getDeviceNude(device);
                tcpConnection.doInBackground("AT+TXA=" + modulenumber + "<0>");
            }
        }

    }

    public void clickHandler(View v) {
        if (!((Button) v).getText().toString().equals(" ")) {
            if (CODE1.getText().length() == 0) {
                CODE1.setText(((Button) v).getText());
                radioButton1.setBackground(getResources().getDrawable(R.drawable.circledotsclicked));
            } else if (CODE2.getText().length() == 0) {
                CODE2.setText(((Button) v).getText());
                radioButton2.setBackground(getResources().getDrawable(R.drawable.circledotsclicked));
            } else if (CODE3.getText().length() == 0) {
                CODE3.setText(((Button) v).getText());
                radioButton3.setBackground(getResources().getDrawable(R.drawable.circledotsclicked));
            } else if (CODE4.getText().length() == 0) {
                CODE4.setText(((Button) v).getText());
                radioButton4.setBackground(getResources().getDrawable(R.drawable.circledotsclicked));
                jump = true;
            }
        }

        if (jump == true) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String code1 = CODE1.getText().toString();
                    String code2 = CODE2.getText().toString();
                    String code3 = CODE3.getText().toString();
                    String code4 = CODE4.getText().toString();
                    String code = code1 + code2 + code3 + code4;
                    ArrayList<String> Passwordlist = DatabaseManager.getInstance().getPasswordList();
                    //TODO: make sure this check will be removed in final version :)
                    if (code.equals("6665")) {
                        Intent startNewActivityIntent = new Intent(HomePage.this, TabViewAdmin.class);
                        startNewActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        clearPinCode();
                        code = "";
                        jump = false;
                        startActivity(startNewActivityIntent);
                    } else if (Passwordlist.contains(code)) {
                        String username = DatabaseManager.getInstance().getUserName(code);
                        String color = DatabaseManager.getInstance().getColor(code);
                        code = "";
                        jump = false;
                        Intent startNewActivityIntent = new Intent(HomePage.this, TabviewUser.class);
                        startNewActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        DataManager.getInstance().setusername(username);
                        DataManager.getInstance().setcolorname(color);
                        clearPinCode();
                        startActivity(startNewActivityIntent);

                    } else {
                        Animation shake = AnimationUtils.loadAnimation(HomePage.this, R.anim.shake_animation);
                        findViewById(R.id.radioButton1).startAnimation(shake);
                        findViewById(R.id.radioButton2).startAnimation(shake);
                        findViewById(R.id.radioButton3).startAnimation(shake);
                        findViewById(R.id.radioButton4).startAnimation(shake);
                        clearPinCode();
                        code = "";
                        jump = false;
                    }

                }
            }, 300);


        }

    }

    public void clearPinCode() {

        CODE1.setText("");
        CODE2.setText("");
        CODE3.setText("");
        CODE4.setText("");
        radioButton1.setBackground(getResources().getDrawable(R.drawable.circledots));
        radioButton2.setBackground(getResources().getDrawable(R.drawable.circledots));
        radioButton3.setBackground(getResources().getDrawable(R.drawable.circledots));
        radioButton4.setBackground(getResources().getDrawable(R.drawable.circledots));
    }

    @Override
    public void onUserInteraction()
    {
        super.onUserInteraction();
        myHandler.removeCallbacks(myRunnable);
        myHandler.postDelayed(myRunnable,3 * 60* 1000);

    }

    @Override
    public void onResume() {
        super.onResume();
        myHandler.postDelayed(myRunnable, 3* 60 * 1000);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        myHandler.removeCallbacks(myRunnable);
    }
    @Override
    public void onBackPressed()
    {
        // super.onBackPressed(); // Comment this super call to avoid calling finish()
    }



    public class CustomPinCodeAdapter extends ArrayAdapter {
        private final Activity context;
        private final String[] zoneList;

        public CustomPinCodeAdapter(Activity unlockScreen, int arrayadapter, String[] numbers) {
            super(unlockScreen, R.layout.homeadpter, numbers);
            this.context = unlockScreen;
            this.zoneList = numbers;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.homeadpter, null, true);
            if (zoneList[position].isEmpty()) {
                rowView.setVisibility(View.INVISIBLE);
            }
            TextView txtTitle = (TextView) rowView.findViewById(R.id.passcodeBtn);
            txtTitle.setText(zoneList[position]);
            return rowView;
        }
    }

    public class TCPConnection extends AsyncTask<String, String, String> {
        TCPClient mTcpClient;
        @Override
        protected String doInBackground(String... sendmessage) {
            //we create a TCPClient object and

            if (mTcpClient!=null && mTcpClient.isConnected())
            {
                mTcpClient.sendMessage(sendmessage[0]);
            }else {
                mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                    @Override
                    //here the messageReceived method is implemented
                    public void messageReceived(String message) {
                        //this method calls the onProgressUpdate
                        RetrieveMessage(message);
                    }
                });
                mTcpClient.run();
            }
            return null;
        }

    }

    public void RetrieveMessage(String message)
    {
        System.out.println("***********" + message);
        if (message.length()<=12 && message.length()>=10 && message.contains(",")) {
            String[] sub = message.split(",", 2);
            if (sub[0].substring(1, 5).equals("+RCV:") && Integer.parseInt(sub[1]) >= 100 && Integer.parseInt(sub[1]) <= 100) {
                Integer nude = Integer.parseInt(sub[0].substring(6, 8));
                Integer intensity = Integer.parseInt(sub[1]);
                DatabaseManager.getInstance().updateDeviceFeedBack(nude, intensity);
            }
        }
    }

}


