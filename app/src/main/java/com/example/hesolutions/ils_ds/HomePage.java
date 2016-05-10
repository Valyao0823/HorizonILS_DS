package com.example.hesolutions.ils_ds;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
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
import com.google.zxing.common.StringUtils;
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
    Button radioButton1, radioButton2, radioButton3, radioButton4, record, clear;
    boolean jump = false;
    boolean emergency = false;
    Switch switch1;
    ImageView emergencypic;
    Handler myHandler;
    Runnable myRunnable;
    TCPClient mTcpClient;
    WifiManager.WifiLock lock;
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
        clear = (Button) findViewById(R.id.clear);
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
        myHandler.postDelayed(myRunnable, 60 * 3 * 1000);

        final GridView gridView = (GridView) findViewById(R.id.gridView);

        String[] numbers = new String[]{"1", "2", "3",
                "4", "5", "6",
                "7", "8", "9",
                "", "0", ""};

        final ArrayAdapter adapter = new CustomPinCodeAdapter(this, R.layout.homeadpter, numbers);

        gridView.setAdapter(adapter);

        Calendar today = Calendar.getInstance();

        final Timer maintimer = new Timer();
        maintimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                // if the gateway is not four-faith, reconnect;
                if (!CheckSSID()){
                    ConnectServer();
                }else {
                    // after getting the right gw
                    // case 3: normal situation
                    if (mTcpClient != null && mTcpClient.isConnected()) {
                        if (emergency == false) {
                            MakeAlert();
                        } else {
                            ArrayList<String> devicelist = DatabaseManager.getInstance().getDeviceList();
                            for (String devicename : devicelist) {
                                // emergency is on: intensity = 100, control = 0
                                int modulenumber = DatabaseManager.getInstance().getDeviceNode(devicename);
                                mTcpClient.sendMessage("AT+TXA=" + modulenumber + "<100>");
                            }
                        }
                    } else if (mTcpClient != null && !mTcpClient.isConnected()) {
                        // case2: after timeout or any internet error, try to reconnect
                        mTcpClient.stopClient();
                        new TCPConnection().execute("");

                    } else {
                        // case1: first connection
                        new TCPConnection().execute("");
                    }
                }
            }
        }, today.getTime(), 1000 * 5);

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
                        int modulenumber = DatabaseManager.getInstance().getDeviceNode(devicename);
                        mTcpClient.sendMessage("AT+TXA=" + modulenumber + "<100>");
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
                if (recorddata.getVisibility() == View.VISIBLE) {
                    recorddata.setVisibility(View.INVISIBLE);
                } else {
                    ArrayList<String> data = DatabaseManager.getInstance().showRecord();
                    recorddata.setVisibility(View.VISIBLE);
                    recorddata.setText(data.toString());
                    recorddata.setMovementMethod(new ScrollingMovementMethod());
                }
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseManager.getInstance().deleteRecord();
                DatabaseManager.getInstance().removeAllevents();
                if (recorddata.getVisibility() == View.VISIBLE) {recorddata.setText("");}
            }
        });
    }

    public void MakeAlert() {
        Calendar cal = Calendar.getInstance();
        ArrayList<WeekViewEvent> events = DatabaseManager.getInstance().loadEvent();
        ArrayList<String> devicelist = DatabaseManager.getInstance().getDeviceList();
        if (events.size() > 0) {
            for (WeekViewEvent event : events) {
                Calendar eventstart = DatabaseManager.getInstance().getEventStart(event);
                Calendar eventfinish = DatabaseManager.getInstance().getEventFinish(event);
                if (cal.before(eventfinish) && cal.after(eventstart)) {
                    String sectorlist = DatabaseManager.getInstance().getEventSector(event);
                    for (String sub : sectorlist.split(",")) {
                        ArrayList<String> devicefromsector = DatabaseManager.getInstance().showDeviceforsector(sub);
                        for (String dev : devicefromsector) {
                            if (devicelist.contains(dev)) devicelist.remove(dev);
                            int control = DatabaseManager.getInstance().getDeviceControl(dev);
                            int node = DatabaseManager.getInstance().getDeviceNode(dev);
                            if (control == 1) {
                                int intensity = DatabaseManager.getInstance().getDeviceIntensity(dev);
                                //System.out.println("****** controlled " + "AT+TXA=" + node + ",<" +intensity + ">");
                                mTcpClient.sendMessage("AT+TXA=" + node + ",<" + intensity + ">" + "\n");
                            } else {
                                int intensity = DatabaseManager.getInstance().getEventIntensity(event);
                                //System.out.println("****** not controlled " + "AT+TXA=" + node + ",<" +intensity + ">");
                                mTcpClient.sendMessage("AT+TXA=" + node + ",<" + intensity + ">" + "\n");
                            }
                        }
                    }
                }
            }

            for (String device : devicelist) {
                int modulenumber = DatabaseManager.getInstance().getDeviceNode(device);
                int intensity = DatabaseManager.getInstance().getDeviceIntensity(device);
                int control = DatabaseManager.getInstance().getDeviceControl(device);
                if (control == 1 && intensity > 0) {
                    //System.out.println("****** not controlled here 1 " + "AT+TXA=" + modulenumber + ",<"+ intensity + ">");
                    mTcpClient.sendMessage("AT+TXA=" + modulenumber + ",<" + intensity + ">" + "\n");
                } else {
                    //System.out.println("****** not controlled here 2 " + "AT+TXA=" + modulenumber + ",<0>");
                    mTcpClient.sendMessage("AT+TXA=" + modulenumber + ",<0>" + "\n");
                }
            }

        } else {
            for (String device : devicelist) {
                int modulenumber = DatabaseManager.getInstance().getDeviceNode(device);
                int intensity = DatabaseManager.getInstance().getDeviceIntensity(device);
                int control = DatabaseManager.getInstance().getDeviceControl(device);
                if (control == 1 && intensity > 0) {
                    //System.out.println("****** not event controlled here 1 " + "AT+TXA=" + modulenumber + ",<"+ intensity + ">");
                    mTcpClient.sendMessage("AT+TXA=" + modulenumber + ",<" + intensity + ">" + "\n");
                } else {
                    //System.out.println("****** not event controlled here 2 " + "AT+TXA=" + modulenumber + ",<0>");
                    mTcpClient.sendMessage("AT+TXA=" + modulenumber + ",<0>" + "\n");
                }
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
                    if (code.equals("0000")) {
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
        @Override
        protected String doInBackground(String... sendmessage) {
            //we create a TCPClient object and
            mTcpClient = new TCPClient(new TCPClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            });
            mTcpClient.run();
            return null;
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            String message = values[0];
            /* for the second stage - first configration
            if (message.length()<=12 && message.length()>=10 && message.contains(",")) {
                String[] sub = message.split(",", 2);
                try {
                    if (sub[0].substring(0, 5).equals("+RCV:") && Integer.parseInt(sub[1]) >= 0 && Integer.parseInt(sub[1]) <= 100) {
                        Integer node = Integer.parseInt(sub[0].substring(5, 8));
                        Integer feedback = Integer.parseInt(sub[1]);
                        if (DatabaseManager.getInstance().getDeviceNodeList().contains(node)) {
                            DatabaseManager.getInstance().updateDeviceFeedBack(node, feedback);
                            ControlPage.RefreshList();
                        }
                    }
                }catch (Exception e) {
                    Log.e("FeedBack", "S: Error", e);
                }
            }
            */
            // for the second configration
            if (message.contains(",")){
                String[] sub = message.split(",",2);
                try {
                    if (sub[0].substring(0, 5).equals("+RCV:") && sub[1].contains("-")) {
                        Integer node = Integer.parseInt(sub[0].substring(5, 8));
                        int count = 0;
                        String[] feedback = new String[5];
                        for (String subdiv: sub[1].split("-") )
                        {
                            switch(count)
                            {
                                case 0: break;
                                case 1: feedback[0] = subdiv; break; // companyname
                                case 2: feedback[1] = subdiv; break; // motion
                                case 3: feedback[2] = subdiv; break; // temp
                                case 4: feedback[3] = subdiv; break; // humi
                                case 5: feedback[4] = subdiv; break; // lightintensity
                            }
                            count++;
                        }

                        int intensity = Integer.parseInt(feedback[4]);
                        if (DatabaseManager.getInstance().getDeviceNodeList().contains(node)) {
                            System.out.println("*******" + node + " :" +intensity);
                            DatabaseManager.getInstance().updateDeviceFeedBack(node, intensity);
                            ControlPage.RefreshList();
                        }
                    }
                }catch (Exception e) {
                    Log.e("FeedBack", "S: Error", e);
                }
            }


        }
    }

    public boolean CheckSSID()
    {
        WifiManager wifi = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifi.getConnectionInfo();
        String ssid = wifiInfo.getSSID();
        if (ssid.equals("\"Four-Faith\"") && lock==null){
            lock = wifi.createWifiLock(WifiManager.WIFI_MODE_FULL, "LockTag");
            lock.acquire();
            return true;
        }else if (ssid.equals("\"Four-Faith\"") && lock.isHeld())
        {
            return true;}
        else {return false;}
    }

    public boolean ConnectServer()
    {
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        // setup a wifi configuration
        WifiConfiguration wc = new WifiConfiguration();
        wc.SSID = "\"Four-Faith\"";
        wc.status = WifiConfiguration.Status.ENABLED;
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        wc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        wc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        wc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        wc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
// connect to and enable the connection
        int netId = wifiManager.addNetwork(wc);
        wifiManager.enableNetwork(netId, true);
        WifiManager.WifiLock lock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, "LockTag");
        lock.acquire();
        wifiManager.setWifiEnabled(true);
        return true;
    }

}


