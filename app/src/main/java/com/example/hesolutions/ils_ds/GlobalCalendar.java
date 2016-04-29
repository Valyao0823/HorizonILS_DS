package com.example.hesolutions.ils_ds;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.os.Bundle;
import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.mylibrary.WeekViewEvent;
import com.mylibrary.WeekView;
import com.mylibrary.MonthLoader;
import com.zxing.activity.CaptureActivity;

import java.util.ArrayList;
import java.util.List;

public class GlobalCalendar extends Activity{
    Button addevent;
    Button today;
    Button oneday;
    Button threedays;
    Button sevendays;
    private WeekView mWeekView;
    Handler myHandler;
    Runnable myRunnable;
    String loginname;
    MonthLoader.MonthChangeListener mMonthChangeListener = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_calendar);
        addevent =(Button)findViewById(R.id.addevent);
        today = (Button)findViewById(R.id.today);
        oneday = (Button)findViewById(R.id.oneday);
        threedays = (Button)findViewById(R.id.threedays);
        sevendays = (Button)findViewById(R.id.sevendays);
        mWeekView = (WeekView) findViewById(R.id.weekView);
        loginname = DataManager.getInstance().getusername();
        myHandler = new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(GlobalCalendar.this, ScreenSaver.class);
                myHandler.removeCallbacks(myRunnable);
                startActivity(intent);
            }
        };
        myHandler.postDelayed(myRunnable, 3*60*1000);
        mMonthChangeListener = new MonthLoader.MonthChangeListener() {
            @Override
            public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                ArrayList<WeekViewEvent> events = DatabaseManager.getInstance().loadEvent();
                return events;
            }

        };
        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(mMonthChangeListener);


        WeekView.EventLongPressListener mEventLongPressListener = new WeekView.EventLongPressListener() {
            @Override
            public void onEventLongPress(final WeekViewEvent event, RectF eventRect) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.eventdialog, null, true);
                AlertDialog.Builder builder = new AlertDialog.Builder(GlobalCalendar.this.getParent())
                        .setView(layout);
                final AlertDialog alertDialog = builder.create();
                Button editevent = (Button) layout.findViewById(R.id.editevent);
                editevent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (event.getName().equals(loginname)) {
                            View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                            Bitmap bitmap = getScreenShot(rootView);
                            DataManager.getInstance().setBitmap(bitmap);
                            DataManager.getInstance().setEvent(event);
                            Intent startNewActivityIntent = new Intent(GlobalCalendar.this, EditEvent.class);
                            startNewActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            ActivityStack activityStack = (ActivityStack) getParent();
                            activityStack.push("SecondActivity", startNewActivityIntent);
                            alertDialog.dismiss();

                        } else {
                            Toast.makeText(GlobalCalendar.this, "Do not have permission!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                Button Deleteevent = (Button) layout.findViewById(R.id.deleteevent);
                Deleteevent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        if (event.getName().equals(loginname))
                        {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(GlobalCalendar.this.getParent());
                            alertDialog.setTitle("Warning");
                            alertDialog.setMessage("Do you want to remove this event?");
                            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                    DatabaseManager.getInstance().removeEvent(event);
                                    dialog.cancel();
                                    Intent startNewActivityIntent = new Intent(getIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                    ActivityStack activityStack = (ActivityStack) getParent();
                                    activityStack.push("RemoveEvent", startNewActivityIntent);
                                    dialog.cancel();

                                }
                            });
                            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                            alertDialog.show();
                        }else
                        {
                            Toast.makeText(GlobalCalendar.this, "Do not have permission!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alertDialog.show();

            }
        };

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(mEventLongPressListener);

        WeekView.EventClickListener mEventClickListener = new WeekView.EventClickListener()
        {
            @Override
            public void onEventClick(final WeekViewEvent event, RectF eventRect)
            {
                Toast.makeText(GlobalCalendar.this, "Created by " + event.getName() + "\nStarting at "
                        + event.getStartTime().getTime()+
                        "\nFinishing at " + event.getEndTime().getTime()
                        +"\nName is: " + event.getName() + "\nSectors included: " + event.getdeviceList().toString(), Toast.LENGTH_SHORT).show();
            }

        };
        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(mEventClickListener);

        addevent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                Bitmap bitmap = getScreenShot(rootView);
                DataManager.getInstance().setBitmap(bitmap);


                ArrayList<String> sectorinformation = DatabaseManager.getInstance().showSectorforuser(loginname);
                if (sectorinformation!=null && !sectorinformation.isEmpty()) {
                    Intent startNewActivityIntent = new Intent(GlobalCalendar.this, CalendarTask.class);
                    startNewActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    ActivityStack activityStack = (ActivityStack) getParent();
                    activityStack.push("SecondActivity", startNewActivityIntent);
                    DataManager.getInstance().setactivity(activityStack.popid());
                }else{
                    Toast.makeText(GlobalCalendar.this, "You have not been assigned to any sector yet.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWeekView.goToToday();
            }
        });
        oneday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                oneday.setBackground(getResources().getDrawable(R.drawable.buttonclicked));
                threedays.setBackground(getResources().getDrawable(R.drawable.buttonunclick));
                sevendays.setBackground(getResources().getDrawable(R.drawable.buttonunclick));
                mWeekView.setNumberOfVisibleDays(1);
                mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));
                mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));
            }
        });
        threedays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                threedays.setBackground(getResources().getDrawable(R.drawable.buttonclicked));
                oneday.setBackground(getResources().getDrawable(R.drawable.buttonunclick));
                sevendays.setBackground(getResources().getDrawable(R.drawable.buttonunclick));
                mWeekView.setNumberOfVisibleDays(3);
                mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));
                mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));
            }
        });
        sevendays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sevendays.setBackground(getResources().getDrawable(R.drawable.buttonclicked));
                oneday.setBackground(getResources().getDrawable(R.drawable.buttonunclick));
                threedays.setBackground(getResources().getDrawable(R.drawable.buttonunclick));
                mWeekView.setNumberOfVisibleDays(7);
                mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));
                mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics()));

            }
        });

    }
    public static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache(),0,100,600,
                screenView.getDrawingCache().getHeight()-100);
        screenView.setDrawingCacheEnabled(false);

        return bitmap;
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
        myHandler.postDelayed(myRunnable, 6*30*1000);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        myHandler.removeCallbacks(myRunnable);
        mWeekView.setMonthChangeListener(mMonthChangeListener);
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
