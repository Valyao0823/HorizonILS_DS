package com.example.hesolutions.ils_ds;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.mylibrary.WeekViewEvent;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class CalendarTask extends Activity {

    TextView startdate;
    TextView starttime;
    TextView finishdate;
    TextView finishtime,Intensitynum;
    Button Apply;
    Button cancelTOcalendar;
    Switch switch1;
    EditText weeknumber;
    TextView textView4,textView5;
    Integer weeks;
    RelativeLayout layout1, progresslayout;
    CheckBox Sunday, Monday, Tuesday, Wednesday, Thursday, Friday, Saturday;
    Integer day;
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
        setContentView(R.layout.activity_calendar_task);

        startdate = (TextView)findViewById(R.id.startdate);
        starttime = (TextView)findViewById(R.id.starttime);
        finishdate = (TextView)findViewById(R.id.finishdate);
        finishtime = (TextView)findViewById(R.id.finishtime);
        Apply = (Button)findViewById(R.id.Apply);
        cancelTOcalendar = (Button)findViewById(R.id.cancelTOcalendar);
        switch1 =(Switch)findViewById(R.id.switch1);
        weeknumber = (EditText)findViewById(R.id.weeknumber);
        textView4 = (TextView)findViewById(R.id.textView4);
        textView5 = (TextView)findViewById(R.id.textView5);
        layout1 = (RelativeLayout)findViewById(R.id.layout1);
        Sunday = (CheckBox)findViewById(R.id.Sunday);
        Monday = (CheckBox)findViewById(R.id.Monday);
        Tuesday = (CheckBox)findViewById(R.id.Tuesday);
        Wednesday = (CheckBox)findViewById(R.id.Wednesday);
        Thursday = (CheckBox)findViewById(R.id.Thursday);
        Friday = (CheckBox)findViewById(R.id.Friday);
        Saturday = (CheckBox)findViewById(R.id.Saturday);
        sectorlistView = (ListView)findViewById(R.id.sectorlistView);
        seekBar = (SeekBar)findViewById(R.id.seekBar);
        Intensitynum = (TextView)findViewById(R.id.Intensitynum);
        progresslayout = (RelativeLayout)findViewById(R.id.progresslayout);
        final SimpleDateFormat ddf = new SimpleDateFormat("MMM dd, yyyy");
        final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        String currentdate = ddf.format(new java.util.Date());
        startdate.setText(currentdate);
        finishdate.setText(currentdate);
        String currenttime = sdf.format(new java.util.Date());
        starttime.setText(currenttime);
        finishtime.setText(currenttime);
        final Calendar startTime = Calendar.getInstance();
        final Calendar finishTime = Calendar.getInstance();
        day =startTime.get(Calendar.DAY_OF_WEEK)-1;
        weeknumber.setFilters(new InputFilter[]{new InputFiletMinMax("1","52")});
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
                Intent intent = new Intent(CalendarTask.this, ScreenSaver.class);
                myHandler.removeCallbacks(myRunnable);
                startActivity(intent);
            }
        };
        myHandler.postDelayed(myRunnable, 3 * 60 * 1000);

//========================================Loading the sector info
        final String username = DataManager.getInstance().getusername();
        final ArrayList<String> sectornames = DatabaseManager.getInstance().showSectorforuser(username);
        if (sectornames !=null && !sectornames.isEmpty()) {
            ArrayList<Group> sectoradapter = new ArrayList<Group>();
            for (String sectorname: sectornames)
            {
                Group group = new Group(sectorname, false);
                sectoradapter.add(group);
            }
            Group selected = new Group("Select All", false);
            sectoradapter.add(selected);
            deviceAdapter = new MyCustomAdapter(this, sectoradapter);
            sectorlistView.setAdapter(deviceAdapter);
        }

//=======================================start date and time===============================================
        startdate.setOnClickListener(new View.OnClickListener() {

            DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    startTime.set(Calendar.YEAR, year);
                    startTime.set(Calendar.MONTH, monthOfYear);
                    startTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    startdate.setText(ddf.format(startTime.getTime()));
                    day = startTime.get(Calendar.DAY_OF_WEEK) - 1;

                    finishTime.set(Calendar.YEAR, year);
                    finishTime.set(Calendar.MONTH, monthOfYear);
                    finishTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    finishdate.setText(ddf.format(startTime.getTime()));

                }

            };

            @Override
            public void onClick(View v) {
                Context context = getParent();
                new DatePickerDialog(context, date, startTime
                        .get(Calendar.YEAR), startTime.get(Calendar.MONTH),
                        startTime.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

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

//=================================finish date time==============================

        finishdate.setOnClickListener(new View.OnClickListener() {
            DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    finishTime.set(Calendar.YEAR, year);
                    finishTime.set(Calendar.MONTH, monthOfYear);
                    finishTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    finishdate.setText(ddf.format(finishTime.getTime()));
                }

            };


            @Override
            public void onClick(View v) {
                Context context = getParent();
                new DatePickerDialog(context, date, finishTime
                        .get(Calendar.YEAR), finishTime.get(Calendar.MONTH),
                        finishTime.get(Calendar.DAY_OF_MONTH)).show();

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
        //===================================================================================================

        cancelTOcalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ActivityStack activityStack = (ActivityStack) getParent();
                activityStack.pop();
                DataManager.getInstance().setactivity("nothing");

            }
        });

        Apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                final String cname = DataManager.getInstance().getusername();
                final String colorName = DataManager.getInstance().getcolorname();
                final int colorname = Color.parseColor(colorName);
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
                    Toast.makeText(CalendarTask.this, "At least one group should be selected", Toast.LENGTH_SHORT).show();
                } else {
                    // Repetition
                    if (switch1.isChecked()) {
                        if (!weeknumber.getText().toString().isEmpty()) {
                            weeks = Integer.parseInt(weeknumber.getText().toString());
                            if (weeks > 0) {
                                if ((finishTime.after(startTime))) {

                                    new AsyncTask<Void, Void, Void>()
                                    {
                                        @Override
                                        protected void onPreExecute() {
                                            ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
                                            progresslayout.setClickable(true);
                                            progressBar.setVisibility(View.VISIBLE);

                                        }
                                        @Override
                                        protected Void doInBackground(Void... params) {
                                            long oldid = DatabaseManager.getInstance().getMaxEventId();
                                            final AtomicLong counter = new AtomicLong(oldid);
                                            Thread[] threads = new Thread[7];
                                            for (int i = 0; i < weeks; i++) {
                                                final int j = i;
                                                threads[0] = new Thread() {
                                                    public void run() {
                                                        if (Monday.isChecked()) {
                                                            long id = counter.incrementAndGet();
                                                            Calendar MonSt = Calendar.getInstance(), MonFi = Calendar.getInstance();
                                                            MonSt.set(Calendar.YEAR, startTime.get(Calendar.YEAR));
                                                            MonSt.set(Calendar.MONTH, startTime.get(Calendar.MONTH));
                                                            MonSt.set(Calendar.DAY_OF_MONTH, startTime.get(Calendar.DAY_OF_MONTH) + 7 * j);
                                                            MonSt.set(Calendar.HOUR_OF_DAY, startTime.get(Calendar.HOUR_OF_DAY));
                                                            MonSt.set(Calendar.MINUTE, startTime.get(Calendar.MINUTE));
                                                            MonSt.set(Calendar.SECOND, 0);
                                                            MonFi.set(Calendar.YEAR, finishTime.get(Calendar.YEAR));
                                                            MonFi.set(Calendar.MONTH, finishTime.get(Calendar.MONTH));
                                                            MonFi.set(Calendar.DAY_OF_MONTH, finishTime.get(Calendar.DAY_OF_MONTH) + 7 * j);
                                                            MonFi.set(Calendar.HOUR_OF_DAY, finishTime.get(Calendar.HOUR_OF_DAY));
                                                            MonFi.set(Calendar.MINUTE, finishTime.get(Calendar.MINUTE));
                                                            MonFi.set(Calendar.SECOND, 0);
                                                            Integer date = 1 - day;
                                                            MonSt.add(Calendar.DAY_OF_MONTH, date);
                                                            MonFi.add(Calendar.DAY_OF_MONTH, date);
                                                            DatabaseManager.getInstance().addEvents(id, MonSt.getTimeInMillis(), MonFi.getTimeInMillis(),
                                                                    sectornamelist, cname, colorname, intensity);
                                                        }
                                                    }
                                                };
                                                threads[0].start();
                                                threads[1] = new Thread() {
                                                    public void run() {
                                                        if (Tuesday.isChecked()) {
                                                            long id = counter.incrementAndGet();
                                                            Calendar TueSt = Calendar.getInstance(), TusFi = Calendar.getInstance();
                                                            TueSt.set(Calendar.YEAR, startTime.get(Calendar.YEAR));
                                                            TueSt.set(Calendar.MONTH, startTime.get(Calendar.MONTH));
                                                            TueSt.set(Calendar.DAY_OF_MONTH, startTime.get(Calendar.DAY_OF_MONTH) + 7 * j);
                                                            TueSt.set(Calendar.HOUR_OF_DAY, startTime.get(Calendar.HOUR_OF_DAY));
                                                            TueSt.set(Calendar.MINUTE, startTime.get(Calendar.MINUTE));
                                                            TueSt.set(Calendar.SECOND, 0);
                                                            TusFi.set(Calendar.YEAR, finishTime.get(Calendar.YEAR));
                                                            TusFi.set(Calendar.MONTH, finishTime.get(Calendar.MONTH));
                                                            TusFi.set(Calendar.DAY_OF_MONTH, finishTime.get(Calendar.DAY_OF_MONTH) + 7 * j);
                                                            TusFi.set(Calendar.HOUR_OF_DAY, finishTime.get(Calendar.HOUR_OF_DAY));
                                                            TusFi.set(Calendar.MINUTE, finishTime.get(Calendar.MINUTE));
                                                            TusFi.set(Calendar.SECOND, 0);
                                                            Integer date = 2 - day;
                                                            TueSt.add(Calendar.DAY_OF_MONTH, date);
                                                            TusFi.add(Calendar.DAY_OF_MONTH, date);
                                                            DatabaseManager.getInstance().addEvents(id, TueSt.getTimeInMillis(), TusFi.getTimeInMillis(),
                                                                    sectornamelist, cname, colorname, intensity);
                                                        }
                                                    }
                                                };
                                                threads[1].start();


                                                threads[2] = new Thread() {
                                                    public void run() {
                                                        if (Wednesday.isChecked()) {
                                                            long id = counter.incrementAndGet();
                                                            Calendar WedSt = Calendar.getInstance(), WedFi = Calendar.getInstance();
                                                            WedSt.set(Calendar.YEAR, startTime.get(Calendar.YEAR));
                                                            WedSt.set(Calendar.MONTH, startTime.get(Calendar.MONTH));
                                                            WedSt.set(Calendar.DAY_OF_MONTH, startTime.get(Calendar.DAY_OF_MONTH) + 7 * j);
                                                            WedSt.set(Calendar.HOUR_OF_DAY, startTime.get(Calendar.HOUR_OF_DAY));
                                                            WedSt.set(Calendar.MINUTE, startTime.get(Calendar.MINUTE));
                                                            WedSt.set(Calendar.SECOND, 0);
                                                            WedFi.set(Calendar.YEAR, finishTime.get(Calendar.YEAR));
                                                            WedFi.set(Calendar.SECOND, 0);
                                                            WedFi.set(Calendar.MONTH, finishTime.get(Calendar.MONTH));
                                                            WedFi.set(Calendar.DAY_OF_MONTH, finishTime.get(Calendar.DAY_OF_MONTH) + 7 * j);
                                                            WedFi.set(Calendar.HOUR_OF_DAY, finishTime.get(Calendar.HOUR_OF_DAY));
                                                            WedFi.set(Calendar.MINUTE, finishTime.get(Calendar.MINUTE));
                                                            Integer date = 3 - day;
                                                            WedSt.add(Calendar.DAY_OF_MONTH, date);
                                                            WedFi.add(Calendar.DAY_OF_MONTH, date);
                                                            DatabaseManager.getInstance().addEvents(id, WedSt.getTimeInMillis(), WedFi.getTimeInMillis(),
                                                                    sectornamelist, cname, colorname, intensity);

                                                        }
                                                    }
                                                };
                                                threads[2].start();

                                                threads[3] = new Thread() {
                                                    public void run() {
                                                        if (Thursday.isChecked()) {
                                                            long id = counter.incrementAndGet();
                                                            Calendar ThuSt = Calendar.getInstance(), ThuFi = Calendar.getInstance();
                                                            ThuSt.set(Calendar.YEAR, startTime.get(Calendar.YEAR));
                                                            ThuSt.set(Calendar.MONTH, startTime.get(Calendar.MONTH));
                                                            ThuSt.set(Calendar.DAY_OF_MONTH, startTime.get(Calendar.DAY_OF_MONTH) + 7 * j);
                                                            ThuSt.set(Calendar.HOUR_OF_DAY, startTime.get(Calendar.HOUR_OF_DAY));
                                                            ThuSt.set(Calendar.MINUTE, startTime.get(Calendar.MINUTE));
                                                            ThuSt.set(Calendar.SECOND, 0);
                                                            ThuFi.set(Calendar.YEAR, finishTime.get(Calendar.YEAR));
                                                            ThuFi.set(Calendar.MONTH, finishTime.get(Calendar.MONTH));
                                                            ThuFi.set(Calendar.DAY_OF_MONTH, finishTime.get(Calendar.DAY_OF_MONTH) + 7 * j);
                                                            ThuFi.set(Calendar.HOUR_OF_DAY, finishTime.get(Calendar.HOUR_OF_DAY));
                                                            ThuFi.set(Calendar.MINUTE, finishTime.get(Calendar.MINUTE));
                                                            ThuFi.set(Calendar.SECOND, 0);
                                                            Integer date = 4 - day;
                                                            ThuSt.add(Calendar.DAY_OF_MONTH, date);
                                                            ThuFi.add(Calendar.DAY_OF_MONTH, date);
                                                            DatabaseManager.getInstance().addEvents(id, ThuSt.getTimeInMillis(), ThuFi.getTimeInMillis(),
                                                                    sectornamelist, cname, colorname, intensity);
                                                        }
                                                    }
                                                };
                                                threads[3].start();


                                                threads[4] = new Thread() {
                                                    public void run() {
                                                        if (Friday.isChecked()) {
                                                            long id = counter.incrementAndGet();
                                                            Calendar FriSt = Calendar.getInstance(), FriFi = Calendar.getInstance();
                                                            FriSt.set(Calendar.YEAR, startTime.get(Calendar.YEAR));
                                                            FriSt.set(Calendar.MONTH, startTime.get(Calendar.MONTH));
                                                            FriSt.set(Calendar.DAY_OF_MONTH, startTime.get(Calendar.DAY_OF_MONTH) + 7 * j);
                                                            FriSt.set(Calendar.HOUR_OF_DAY, startTime.get(Calendar.HOUR_OF_DAY));
                                                            FriSt.set(Calendar.MINUTE, startTime.get(Calendar.MINUTE));
                                                            FriSt.set(Calendar.SECOND, 0);
                                                            FriFi.set(Calendar.YEAR, finishTime.get(Calendar.YEAR));
                                                            FriFi.set(Calendar.MONTH, finishTime.get(Calendar.MONTH));
                                                            FriFi.set(Calendar.DAY_OF_MONTH, finishTime.get(Calendar.DAY_OF_MONTH) + 7 * j);
                                                            FriFi.set(Calendar.HOUR_OF_DAY, finishTime.get(Calendar.HOUR_OF_DAY));
                                                            FriFi.set(Calendar.MINUTE, finishTime.get(Calendar.MINUTE));
                                                            FriFi.set(Calendar.SECOND, 0);
                                                            Integer date = 5 - day;
                                                            FriSt.add(Calendar.DAY_OF_MONTH, date);
                                                            FriFi.add(Calendar.DAY_OF_MONTH, date);
                                                            DatabaseManager.getInstance().addEvents(id, FriSt.getTimeInMillis(), FriFi.getTimeInMillis(),
                                                                    sectornamelist, cname, colorname, intensity);
                                                        }
                                                    }
                                                };
                                                threads[4].start();

                                                threads[5] = new Thread() {
                                                    public void run() {
                                                        if (Saturday.isChecked()) {
                                                            long id = counter.incrementAndGet();
                                                            Calendar SatSt = Calendar.getInstance(), SatFi = Calendar.getInstance();
                                                            SatSt.set(Calendar.YEAR, startTime.get(Calendar.YEAR));
                                                            SatSt.set(Calendar.MONTH, startTime.get(Calendar.MONTH));
                                                            SatSt.set(Calendar.DAY_OF_MONTH, startTime.get(Calendar.DAY_OF_MONTH) + 7 * j);
                                                            SatSt.set(Calendar.HOUR_OF_DAY, startTime.get(Calendar.HOUR_OF_DAY));
                                                            SatSt.set(Calendar.MINUTE, startTime.get(Calendar.MINUTE));
                                                            SatSt.set(Calendar.SECOND, 0);
                                                            SatFi.set(Calendar.YEAR, finishTime.get(Calendar.YEAR));
                                                            SatFi.set(Calendar.MONTH, finishTime.get(Calendar.MONTH));
                                                            SatFi.set(Calendar.DAY_OF_MONTH, finishTime.get(Calendar.DAY_OF_MONTH) + 7 * j);
                                                            SatFi.set(Calendar.HOUR_OF_DAY, finishTime.get(Calendar.HOUR_OF_DAY));
                                                            SatFi.set(Calendar.MINUTE, finishTime.get(Calendar.MINUTE));
                                                            SatFi.set(Calendar.SECOND, 0);
                                                            Integer date = 6 - day;
                                                            SatSt.add(Calendar.DAY_OF_MONTH, date);
                                                            SatFi.add(Calendar.DAY_OF_MONTH, date);
                                                            DatabaseManager.getInstance().addEvents(id, SatSt.getTimeInMillis(), SatFi.getTimeInMillis(),
                                                                    sectornamelist, cname, colorname, intensity);
                                                        }
                                                    }
                                                };
                                                threads[5].start();


                                                threads[6] = new Thread() {
                                                    public void run() {
                                                        if (Sunday.isChecked()) {
                                                            long id = counter.incrementAndGet();
                                                            Calendar SunSt = Calendar.getInstance(), SunFi = Calendar.getInstance();
                                                            SunSt.set(Calendar.YEAR, startTime.get(Calendar.YEAR));
                                                            SunSt.set(Calendar.MONTH, startTime.get(Calendar.MONTH));
                                                            SunSt.set(Calendar.DAY_OF_MONTH, startTime.get(Calendar.DAY_OF_MONTH) + 7 * j);
                                                            SunSt.set(Calendar.HOUR_OF_DAY, startTime.get(Calendar.HOUR_OF_DAY));
                                                            SunSt.set(Calendar.MINUTE, startTime.get(Calendar.MINUTE));
                                                            SunSt.set(Calendar.SECOND, 0);
                                                            SunFi.set(Calendar.YEAR, finishTime.get(Calendar.YEAR));
                                                            SunFi.set(Calendar.MONTH, finishTime.get(Calendar.MONTH));
                                                            SunFi.set(Calendar.DAY_OF_MONTH, finishTime.get(Calendar.DAY_OF_MONTH) + 7 * j);
                                                            SunFi.set(Calendar.HOUR_OF_DAY, finishTime.get(Calendar.HOUR_OF_DAY));
                                                            SunFi.set(Calendar.MINUTE, finishTime.get(Calendar.MINUTE));
                                                            SunFi.set(Calendar.SECOND, 0);
                                                            Integer date = 0 - day;
                                                            SunSt.add(Calendar.DAY_OF_MONTH, date);
                                                            SunFi.add(Calendar.DAY_OF_MONTH, date);
                                                            DatabaseManager.getInstance().addEvents(id, SunSt.getTimeInMillis(), SunFi.getTimeInMillis(),
                                                                    sectornamelist, cname, colorname, intensity);
                                                        }
                                                    }
                                                };
                                                threads[6].start();
                                            }
                                            return null;
                                        }
                                        @Override
                                        protected void onPostExecute(Void res) {
                                            progresslayout.setClickable(false);
                                            ActivityStack activityStack = (ActivityStack) getParent();
                                            activityStack.pop();
                                            DataManager.getInstance().setactivity("nothing");
                                        }
                                    }.execute();

                                } else {
                                    runOnUiThread(new Runnable() {
                                        public void run() {
                                            Toast.makeText(CalendarTask.this, "Invalid time", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            } else {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        Toast.makeText(CalendarTask.this, "Enter a valid week number (at least 1)", Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(CalendarTask.this, "Please enter a number", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }

                    // not repetition
                    if (switch1.isChecked() == false) {
                        if ((finishTime.after(startTime))) {

                            new AsyncTask<Void, Void ,Void>()
                            {
                                @Override
                                protected void onPreExecute() {
                                    ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
                                    progresslayout.setClickable(true);
                                    progressBar.setVisibility(View.VISIBLE);
                                }
                                @Override
                                protected Void doInBackground(Void... params) {
                                    long id = DatabaseManager.getInstance().getMaxEventId() + 1;
                                    startTime.set(Calendar.SECOND,0);
                                    finishTime.set(Calendar.SECOND,0);
                                    DatabaseManager.getInstance().addEvents(id, startTime.getTimeInMillis(), finishTime.getTimeInMillis(),
                                            sectornamelist, cname, colorname, intensity);
                                    return null;
                                }
                                @Override
                                protected void onPostExecute(Void res) {
                                    progresslayout.setClickable(false);
                                    ActivityStack activityStack = (ActivityStack) getParent();
                                    activityStack.pop();
                                    DataManager.getInstance().setactivity("nothing");
                                }
                            }.execute();

                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(CalendarTask.this, "Invalid Time", Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                    }

                }
            }
        });

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {

                    startdate.setEnabled(false);
                    startdate.setTextColor(getResources().getColor(R.color.gray));
                    finishdate.setEnabled(false);
                    finishdate.setTextColor(getResources().getColor(R.color.gray));
                    starttime.setEnabled(false);
                    starttime.setTextColor(getResources().getColor(R.color.gray));
                    finishtime.setEnabled(false);
                    finishtime.setTextColor(getResources().getColor(R.color.gray));

                    for (int i = 0; i < layout1.getChildCount(); i++) {
                        View child = layout1.getChildAt(i);
                        child.setEnabled(true);
                    }


                    if (day.equals(1)) {
                        Monday.setChecked(true);
                        Monday.setEnabled(false);
                    } else if (day.equals(2)) {
                        Tuesday.setChecked(true);
                        Tuesday.setEnabled(false);
                    } else if (day.equals(3)) {
                        Wednesday.setChecked(true);
                        Wednesday.setEnabled(false);
                    } else if (day.equals(4)) {
                        Thursday.setChecked(true);
                        Thursday.setEnabled(false);
                    } else if (day.equals(5)) {
                        Friday.setChecked(true);
                        Friday.setEnabled(false);
                    } else if (day.equals(6)) {
                        Saturday.setChecked(true);
                        Saturday.setEnabled(false);
                    } else {
                        Sunday.setChecked(true);
                        Sunday.setEnabled(false);
                    }


                } else {
                    for (int i = 0; i < layout1.getChildCount(); i++) {
                        View child = layout1.getChildAt(i);
                        child.setEnabled(false);
                    }

                    startdate.setEnabled(true);
                    startdate.setTextColor(getResources().getColor(R.color.wordscolor));
                    finishdate.setEnabled(true);
                    finishdate.setTextColor(getResources().getColor(R.color.wordscolor));
                    starttime.setEnabled(true);
                    starttime.setTextColor(getResources().getColor(R.color.wordscolor));
                    finishtime.setEnabled(true);
                    finishtime.setTextColor(getResources().getColor(R.color.wordscolor));

                    Monday.setChecked(false);
                    Tuesday.setChecked(false);
                    Wednesday.setChecked(false);
                    Thursday.setChecked(false);
                    Friday.setChecked(false);
                    Saturday.setChecked(false);
                    Sunday.setChecked(false);


                }
            }
        });


        seekBar.setProgress(100);
        Intensitynum.setText("100%");

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                intensity = progress;
                Intensitynum.setText(Integer.toString(progress) + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

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
