package com.example.hesolutions.ils_ds;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mylibrary.WeekViewEvent;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class EditEvent extends Activity {
    TextView starttime, startdate, finishdate;
    TextView finishtime,Intensitynum;
    Button Apply;
    Button cancelTOcalendar;
    ListView sectorlistView;
    MyCustomAdapter deviceAdapter = null;
    int intensity = 100;
    SeekBar seekBar;
    Handler myHandler;
    Runnable myRunnable;
    String sectornamelist = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);
        starttime = (TextView)findViewById(R.id.starttime);
        finishtime = (TextView)findViewById(R.id.finishtime);
        startdate = (TextView)findViewById(R.id.startdate);
        finishdate = (TextView)findViewById(R.id.finishdate);
        Apply = (Button)findViewById(R.id.Apply);
        cancelTOcalendar = (Button)findViewById(R.id.cancelTOcalendar);
        sectorlistView = (ListView)findViewById(R.id.sectorlistView);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        Intensitynum = (TextView)findViewById(R.id.Intensitynum);
        final SimpleDateFormat ddf = new SimpleDateFormat("MMM dd, yyyy");
        final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        ImageView homescreenBgImage = (ImageView) findViewById(R.id.imageView);

        Bitmap cachedBitmap = DataManager.getInstance().getBitmap();
        if (cachedBitmap != null) {
            Bitmap blurredBitmap = BlurBuilder.blur(this, cachedBitmap);
            homescreenBgImage.setBackground(new BitmapDrawable(getResources(), blurredBitmap));
        }

        setupUI(findViewById(R.id.parent));

        myHandler = new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(EditEvent.this, ScreenSaver.class);
                myHandler.removeCallbacks(myRunnable);
                startActivity(intent);
            }
        };
        myHandler.postDelayed(myRunnable, 3 * 60 * 1000);

        final WeekViewEvent event = DataManager.getInstance().getEvent();
        long ST = event.getStartTime().getTime().getTime();
        long FT = event.getEndTime().getTime().getTime();
        final String sectorlist = event.getdeviceList();
        final int eventintensity = event.getIntensity();
        // ========================================= set up the intensity ====================================
        seekBar.setProgress(eventintensity);
        Intensitynum.setText(String.valueOf(eventintensity) + "%");
// =============================================== set up the list ==================================
        ArrayList<String > sectorinevent = new ArrayList<>();
        for (String sd: sectorlist.split(","))
        {
            sectorinevent.add(sd);
        }
        ArrayList<String> belongedsector = DatabaseManager.getInstance().showSectorforuser(event.getName());
        ArrayList<Group> sectoradapter = new ArrayList<Group>();
        for (String sector: belongedsector)
        {
            if (sectorinevent.contains(sector)) {
                Group group = new Group(sector, true);
                sectoradapter.add(group);
            }else{
                Group group = new Group(sector, false);
                sectoradapter.add(group);
            }
        }

        boolean selecteall = true;
        for (Group group: sectoradapter)
        {
            if (group.getSelected()==false) selecteall=false;
        }
        Group selected = new Group("Select All", selecteall);
        sectoradapter.add(selected);
        deviceAdapter = new MyCustomAdapter(this, sectoradapter);
        sectorlistView.setAdapter(deviceAdapter);

// ======================================== set up the time==========================================
        final Calendar startTime = Calendar.getInstance();
        Date sT = new Date(ST);
        startTime.setTime(sT);

        final Calendar finishTime = Calendar.getInstance();
        Date fT = new Date(FT);
        finishTime.setTime(fT);

        starttime.setText(sdf.format(startTime.getTime()));
        startdate.setText(ddf.format(startTime.getTime()));
        startdate.setTextColor(getResources().getColor(R.color.gray));
        finishtime.setText(sdf.format(finishTime.getTime()));
        finishdate.setText(ddf.format(finishTime.getTime()));
        finishdate.setTextColor(getResources().getColor(R.color.gray));
        starttime.setOnClickListener(new View.OnClickListener() {

            TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int Hour, int Minute) {
                    startTime.set(Calendar.HOUR_OF_DAY, Hour);
                    startTime.set(Calendar.MINUTE, Minute);
                    startTime.set(Calendar.SECOND, 0);
                    starttime.setText(sdf.format(startTime.getTime()));

                    finishTime.set(Calendar.HOUR_OF_DAY, Hour);
                    finishTime.set(Calendar.MINUTE, Minute);
                    finishTime.set(Calendar.SECOND, 0);
                    finishtime.setText(sdf.format(startTime.getTime()));
                }
            };

            @Override
            public void onClick(View v) {
                Context context = getParent();
                new TimePickerDialog(context, time, startTime.get(Calendar.HOUR_OF_DAY), startTime.get(Calendar.MINUTE), true).show();

            }
        });
        finishtime.setOnClickListener(new View.OnClickListener() {

            TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener()
            {
                @Override
                public void onTimeSet(TimePicker view, int Hour, int Minute)
                {
                    finishTime.set(Calendar.HOUR_OF_DAY, Hour);
                    finishTime.set(Calendar.MINUTE, Minute);
                    finishTime.set(Calendar.SECOND,0);
                    finishtime.setText(sdf.format(finishTime.getTime()));
                }
            };
            @Override
            public void onClick(View v) {
                Context context = getParent();
                new TimePickerDialog(context, time, finishTime.get(Calendar.HOUR_OF_DAY),finishTime.get(Calendar.MINUTE),true).show();
            }
        });

        cancelTOcalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ActivityStack activityStack = (ActivityStack) getParent();
                activityStack.pop();
                DataManager.getInstance().setactivity("nothing");

            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private int finalprogress = eventintensity;
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                finalprogress = progress;
                Intensitynum.setText(Integer.toString(progress) + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                intensity = finalprogress;
            }
        });


        Apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deviceAdapter!=null) {
                    ArrayList<Group> choosegrouplist = deviceAdapter.arrayList;
                    for (int i = 0; i < choosegrouplist.size(); i++) {
                        Group group = choosegrouplist.get(i);
                        if (!group.getName().equals("Select All")){
                            if (group.getSelected() == true) {
                                sectornamelist += String.valueOf(group.getName()) + ",";
                            }
                        }
                    }
                }
                if (sectornamelist.equals("")) {
                    Toast.makeText(EditEvent.this, "At least one group should be selected", Toast.LENGTH_SHORT).show();
                } else {
                    if ((finishTime.after(startTime))) {

                        long id = event.getId();
                        DatabaseManager.getInstance().updateEvent(startTime.getTimeInMillis(), finishTime.getTimeInMillis(), sectornamelist, intensity, id);
                        ActivityStack activityStack = (ActivityStack) getParent();
                        activityStack.pop();
                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(EditEvent.this, "Invalid Time", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }

            }
        });

    }

    private class MyCustomAdapter extends ArrayAdapter<Group> {
        private final Activity context;
        ArrayList<Group> arrayList;

        public MyCustomAdapter(Activity context,
                               ArrayList<Group> arrayList) {
            super(context,R.layout.eventsector,arrayList);
            this.context = context;
            this.arrayList = arrayList;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.eventsector, null);

            }

            Group group = arrayList.get(position);
            TextView name = (TextView) convertView.findViewById(R.id.name);
            final EnhancedSwitch checked = (EnhancedSwitch) convertView.findViewById(R.id.checked);
            checked.setSwitchMinWidth(300);
            name.setPadding(50,0,0,0);
            name.setText(group.getName());
            checked.setCheckedProgrammatically(group.getSelected());
            checked.setTag(group);

            checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Group group = (Group) buttonView.getTag();
                    if (checked.isChecked() == true) {
                        if (group.getName().equals("Select All")) {
                            for (int k = 0; k < arrayList.size(); k++) {
                                Group every = arrayList.get(k);
                                every.setSelected(true);
                            }
                        } else {
                            group.setSelected(true);

                        }
                    } else {
                        if (group.getName().equals("Select All")) {
                            for (int k = 0; k < arrayList.size(); k++) {
                                Group every = arrayList.get(k);
                                every.setSelected(false);
                            }
                        } else {
                            group.setSelected(false);
                        }

                    }

                    boolean check = true;
                    for (int m = 0; m <arrayList.size()-1; m++)
                    {
                        if (arrayList.get(m).getSelected() == true){}
                        else{check = false;}
                    }
                    if (check == true)
                    {
                        arrayList.get(arrayList.size()-1).setSelected(true);
                    }else
                    {
                        arrayList.get(arrayList.size()-1).setSelected(false);
                    }

                    notifyDataSetChanged();
                }
            });


            return convertView;

        }

    }

    public class Group
    {

        String name;
        boolean ischecked;

        public Group(String name, boolean ischecked)
        {
            this.name = name;
            this.ischecked = ischecked;
        }
        public boolean getSelected()
        {
            return ischecked;
        }
        public void setSelected(boolean ischecked)
        {
            this.ischecked = ischecked;
        }
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }

    public void setupUI(View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if(!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {

                public boolean onTouch(View v, MotionEvent event) {
                    if(getCurrentFocus() != null) {
                        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                    }
                    return false;
                }

            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    @Override
    public void onUserInteraction()
    {
        super.onUserInteraction();
        myHandler.removeCallbacks(myRunnable);
        myHandler.postDelayed(myRunnable,3*60*1000);

    }
    @Override
    public void onResume() {
        super.onResume();
        myHandler.postDelayed(myRunnable, 6*30 * 1000);
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

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        myHandler.removeCallbacks(myRunnable);
    }

}


