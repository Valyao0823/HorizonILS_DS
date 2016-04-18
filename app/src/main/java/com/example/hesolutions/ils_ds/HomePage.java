package com.example.hesolutions.ils_ds;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


public class HomePage extends Activity {
    final SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy HH mm");
    TextView CODE1, CODE2, CODE3, CODE4;
    GridView gridView;
    Button radioButton1, radioButton2, radioButton3, radioButton4;
    boolean jump = false;
    boolean emergency = false;
    Switch switch1;
    ImageView emergencypic;
    AlertDialog.Builder builder = null;
    AlertDialog alertDialog;
    Handler myHandler;
    Runnable myRunnable;
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


        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Calendar today = Calendar.getInstance();
        Calendar newtoday = Calendar.getInstance();
        newtoday.set(Calendar.SECOND, 0);
        newtoday.set(Calendar.MINUTE, 0);
        newtoday.set(Calendar.HOUR_OF_DAY, 0);
        newtoday.add(Calendar.DATE, 1);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                GetNewEvent();
            }
        }, newtoday.getTime(), 1000 * 60 * 60 * 12);

/*
        final Timer maintimer = new Timer();
        maintimer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if (gateway != null) {
                    if (alertDialog != null && alertDialog.isShowing()) alertDialog.dismiss();
                    if (emergency == false) {
                        System.out.println("*********** main thread");
                        MakeAlert();
                    } else {
                        ArrayList<Device> deviceArrayList = DatabaseManager.getInstance().getDeviceList().getmDeviceList();
                        for (Device device : deviceArrayList) {
                            byte[] data;
                            data = new byte[]{(byte) 17, (byte) 100, (byte) 0, (byte) 0, (byte) 0};
                            DeviceSocket.getInstance().send(Message.createMessage((byte) 4, DevicePacket.createPacket((byte) 4,
                                            device.getDeviceAddress(), (short) 0, data), device.getGatewayMacAddr(), device.getGatewayPassword(),
                                    device.getGatewaySSID(), HomePage.this));
                        }
                    }
                } else {
                    //maintimer.cancel();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (alertDialog != null && alertDialog.isShowing()) {
                            } else {
                                builder = new AlertDialog.Builder(HomePage.this);
                                builder.setTitle("Error");
                                builder.setMessage("Gateway Error, please connect the wifi and press OK");
                                builder.setCancelable(false);
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent i = getBaseContext().getPackageManager()
                                                .getLaunchIntentForPackage(getBaseContext().getPackageName());
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        int mPendingIntentId = 3;
                                        PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, i, PendingIntent.FLAG_CANCEL_CURRENT);
                                        AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                                        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, mPendingIntent);
                                        System.exit(0);
                                    }
                                });
                                alertDialog = builder.create();
                                alertDialog.show();
                            }
                        }
                    });
                }
            }
        }, today.getTime(), 1000 * 20);
*/

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                /*
                if (switch1.isChecked() == true) {
                    emergency = true;
                    emergencypic.setVisibility(View.VISIBLE);
                    ArrayList<Device> deviceArrayList = DatabaseManager.getInstance().getDeviceList().getmDeviceList();
                    switch1.setText("Emergency ON  ");
                    gridView.setAdapter(null);
                    Gateway gateway = SysApplication.getInstance().getCurrGateway(HomePage.this);
                    if (gateway != null) {
                        myHandler.removeCallbacks(myRunnable);
                        for (Device device : deviceArrayList) {
                            byte[] data;
                            data = new byte[]{(byte) 17, (byte) 100, (byte) 0, (byte) 0, (byte) 0};
                            DeviceSocket.getInstance().send(Message.createMessage((byte) 4, DevicePacket.createPacket((byte) 4,
                                            device.getDeviceAddress(), (short) 0, data), device.getGatewayMacAddr(), device.getGatewayPassword(),
                                    device.getGatewaySSID(), HomePage.this));
                        }
                    } else {
                        myHandler.postDelayed(myRunnable, 60 * 3 * 1000);
                        if (alertDialog != null && alertDialog.isShowing()) {
                        } else {
                            builder = new AlertDialog.Builder(HomePage.this);
                            builder.setTitle("Error");
                            builder.setMessage("Gateway Error, please connect the wifi and press OK");
                            builder.setCancelable(false);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = getBaseContext().getPackageManager()
                                            .getLaunchIntentForPackage(getBaseContext().getPackageName());
                                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    int mPendingIntentId = 3;
                                    PendingIntent mPendingIntent = PendingIntent.getActivity(getApplicationContext(), mPendingIntentId, i, PendingIntent.FLAG_CANCEL_CURRENT);
                                    AlarmManager mgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                                    mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, mPendingIntent);
                                    System.exit(0);
                                }
                            });
                            alertDialog = builder.create();
                            alertDialog.show();
                        }
                    }
                } else {
                    emergency = false;
                    gridView.setAdapter(adapter);
                    switch1.setText("Emergency OFF  ");
                    emergencypic.setVisibility(View.GONE);
                }
                */
            }

        });
    }

    public void MakeAlert() {
        /*
        List<WeekViewEvent> events;
        Calendar cal = Calendar.getInstance();
        events = DataManager.getInstance().getnewevents();
        HashMap<String, HashMap<String, ArrayList<Device>>> sector = DataManager.getInstance().getsector();
        ArrayList<Device> arrayList = DatabaseManager.getInstance().LoadDeviceList("devicelist");
        if (events != null && events.size()> 0) {
            Iterator<WeekViewEvent> eventIterator = events.iterator();
            while (eventIterator.hasNext()) {
                WeekViewEvent event = eventIterator.next();
                ArrayList<String> sectorsname = event.getdeviceList();
                Calendar start = event.getStartTime();
                Calendar finish = event.getEndTime();
                String username = event.getName();
                int intensity = event.getIntensity();

                if (cal.before(finish) && cal.after(start)) {
                    Iterator<String> stringIterator = sectorsname.iterator();
                    while (stringIterator.hasNext()) {
                        String sectorname = stringIterator.next();
                        if (sector.get(username).containsKey(sectorname)) {
                            ArrayList<Device> deviceArrayList = sector.get(username).get(sectorname);
                            if (deviceArrayList != null) {
                                for (Device device : deviceArrayList) {
                                    Iterator<Device> deviceiterator = arrayList.iterator();
                                    while (deviceiterator.hasNext()) {
                                        Device device1 = deviceiterator.next();
                                        if (device1.getDeviceName().equals(device.getDeviceName())) {
                                            deviceiterator.remove();
                                        }
                                    }
                                    Device thedevice = DatabaseManager.getInstance().getDeviceInforName(device.getDeviceName());
                                    if (thedevice != null) {
                                        if (thedevice.getChannelMark() != 5)
                                        // if controled by control page then dont use schedule
                                        {
                                            byte[] data;
                                            data = new byte[]{(byte) 17, (byte) intensity, (byte) 0, (byte) 0, (byte) 0};
                                            DeviceSocket.getInstance().send(Message.createMessage((byte) 4, DevicePacket.createPacket((byte) 4,
                                                            thedevice.getDeviceAddress(), (short) 0, data), thedevice.getGatewayMacAddr(), thedevice.getGatewayPassword(),
                                                    thedevice.getGatewaySSID(), HomePage.this));
                                            thedevice.setCurrentParams(data);
                                            DatabaseManager.getInstance().updateDevice(thedevice);
                                        }else
                                        {
                                            byte[] data;
                                            data = thedevice.getCurrentParams();
                                            DeviceSocket.getInstance().send(Message.createMessage((byte) 4, DevicePacket.createPacket((byte) 4,
                                                            thedevice.getDeviceAddress(), (short) 0, data), thedevice.getGatewayMacAddr(), thedevice.getGatewayPassword(),
                                                    thedevice.getGatewaySSID(), HomePage.this));
                                        }
                                    }
                                }
                            } else {
                            }
                        } else {
                            stringIterator.remove();
                        }
                    }
                }
            }

            if (arrayList != null) {
                Iterator<Device> deviceiterator = arrayList.iterator();
                while (deviceiterator.hasNext()) {
                    Device device = deviceiterator.next();
                    Device thedevice = DatabaseManager.getInstance().getDeviceInforName(device.getDeviceName());
                    if (thedevice != null) {
                        if (thedevice.getChannelMark() != 5) {
                            byte[] data;
                            data = new byte[]{(byte) 17, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
                            DeviceSocket.getInstance().send(Message.createMessage((byte) 4, DevicePacket.createPacket((byte) 4,
                                            thedevice.getDeviceAddress(), (short) 0, data), thedevice.getGatewayMacAddr(), thedevice.getGatewayPassword(),
                                    thedevice.getGatewaySSID(), HomePage.this));
                            thedevice.setCurrentParams(data);
                            DatabaseManager.getInstance().updateDevice(thedevice);
                        } else {
                            byte[] data = thedevice.getCurrentParams();
                            DeviceSocket.getInstance().send(Message.createMessage((byte) 4, DevicePacket.createPacket((byte) 4,
                                            thedevice.getDeviceAddress(), (short) 0, data), thedevice.getGatewayMacAddr(), thedevice.getGatewayPassword(),
                                    thedevice.getGatewaySSID(), HomePage.this));
                        }
                    }
                }
            }
        }else{
            Iterator<Device> deviceiterator = arrayList.iterator();
            if (deviceiterator != null) {
                while (deviceiterator.hasNext()) {
                    Device device = deviceiterator.next();
                    Device thedevice = DatabaseManager.getInstance().getDeviceInforName(device.getDeviceName());
                    if (thedevice != null) {
                        if (thedevice.getChannelMark() != 5) {
                            byte[] data;
                            data = new byte[]{(byte) 17, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
                            DeviceSocket.getInstance().send(Message.createMessage((byte) 4, DevicePacket.createPacket((byte) 4,
                                            thedevice.getDeviceAddress(), (short) 0, data), thedevice.getGatewayMacAddr(), thedevice.getGatewayPassword(),
                                    thedevice.getGatewaySSID(), HomePage.this));
                            thedevice.setCurrentParams(data);
                            DatabaseManager.getInstance().updateDevice(thedevice);
                        } else {
                            byte[] data = thedevice.getCurrentParams();
                            DeviceSocket.getInstance().send(Message.createMessage((byte) 4, DevicePacket.createPacket((byte) 4,
                                            thedevice.getDeviceAddress(), (short) 0, data), thedevice.getGatewayMacAddr(), thedevice.getGatewayPassword(),
                                    thedevice.getGatewaySSID(), HomePage.this));
                        }
                    }
                }
            }
        }
        */
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

    public void GetNewEvent() {
        /*
        List<WeekViewEvent> events = DataManager.getInstance().getevents();
        List<WeekViewEvent> newevents = DataManager.getInstance().getnewevents();
        long today = Calendar.getInstance().getTimeInMillis();
        if (events != null) {
            Iterator<WeekViewEvent> eventIterator = events.iterator();
            while (eventIterator.hasNext()) {
                WeekViewEvent event = eventIterator.next();
                long eventday = event.getStartTime().getTimeInMillis();
                long mills = eventday - today;
                long days = mills / (1000 * 60 * 60 * 24);
                if (days <= 30) {
                    newevents.add(event);
                    eventIterator.remove();
                }
            }
        }
        if (newevents!=null)
        {
            Iterator<WeekViewEvent> neweventIterator = newevents.iterator();
            while (neweventIterator.hasNext()) {
                WeekViewEvent event = neweventIterator.next();
                long eventday = event.getStartTime().getTimeInMillis();
                long mills = today - eventday;
                long days = mills / (1000 * 60 * 60 * 24);
                if (days >= 30) {
                    neweventIterator.remove();
                }
            }
        }
        DataManager.getInstance().setnewevents(newevents);
        DataManager.getInstance().setevents(events);
        */
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
}


