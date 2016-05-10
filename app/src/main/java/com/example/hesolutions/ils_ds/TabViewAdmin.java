package com.example.hesolutions.ils_ds;

import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TabHost;

public class TabViewAdmin extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_view_admin);

        final TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();
        TabHost.TabSpec spec;
        Intent intent;

        // Create an Intent to launch an Activity for the tab (to be reused)

        intent = new Intent().setClass(this, ActivityAdminStack.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        spec = tabHost.newTabSpec("Access")
                .setIndicator("", getResources().getDrawable(R.drawable.accessicon))
                .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, PaintPage.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        spec = tabHost.newTabSpec("Layout")
                .setIndicator("", getResources().getDrawable(R.drawable.layouticon))
                .setContent(intent);
        tabHost.addTab(spec);

        intent = new Intent().setClass(this, EmptyActivity.class);
        spec = tabHost.newTabSpec("Logout")
                .setIndicator("", getResources().getDrawable(R.drawable.logouticon))
                .setContent(intent);
        tabHost.addTab(spec);

        //set tab which one you want open first time 0 or 1 or 2
        tabHost.setCurrentTab(0);
        getTabHost().setOnTabChangedListener(new TabHost.OnTabChangeListener() {
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

    }
}
