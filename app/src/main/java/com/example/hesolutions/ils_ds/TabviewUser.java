package com.example.hesolutions.ils_ds;

import android.app.AlertDialog;
import android.app.LocalActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TabHost;
import android.app.TabActivity;
import android.widget.TabWidget;
import android.widget.TextView;

public class TabviewUser extends TabActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabview_user);

        final TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        TextView textview14 = (TextView)findViewById(R.id.textView14);
        tabHost.setup();
        TabHost.TabSpec spec;
        Intent intent;
        textview14.setText(DataManager.getInstance().getusername());
        // Create an Intent to launch an Activity for the tab (to be reused)

        intent = new Intent().setClass(this, ControlPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        spec = tabHost.newTabSpec("Control")
                .setIndicator("", getResources().getDrawable(R.drawable.zoneicon))
                .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, ActivityStack.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        spec = tabHost.newTabSpec("Calendar")
                .setIndicator("", getResources().getDrawable(R.drawable.scheduleicon))
                .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, EmptyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        spec = tabHost.newTabSpec("Logout")
                .setIndicator("", getResources().getDrawable(R.drawable.logouticon))
                .setContent(intent);
        tabHost.addTab(spec);

        //set tab which one you want open first time 0 or 1 or 2
        tabHost.setCurrentTab(0);
        getTabHost().setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId == "Logout") {
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    finish();
                }
            }
        });
        for (int i = 0; i < tabHost.getTabWidget().getTabCount(); i++) {
            tabHost.getTabWidget().getChildAt(i).getLayoutParams().height = 100;
            tabHost.getTabWidget().getChildAt(i).getLayoutParams().width= 150;

        }

        tabHost.getTabWidget().getChildTabViewAt(2).setOnTouchListener(onClickListener);
        tabHost.getTabWidget().getChildTabViewAt(0).setOnTouchListener(onClickListener);

    }

    private View.OnTouchListener onClickListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                String s = DataManager.getInstance().getactivity();
                if (s != null) {
                    if (s.equals("com.example.hesolutions.horizon.CalendarTask")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(TabviewUser.this);
                        builder.setTitle("Warning");
                        builder.setMessage("Save first!");
                        builder.setCancelable(false);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                        return true;
                    }
                }
            }
            return false;
        }
    };


}
