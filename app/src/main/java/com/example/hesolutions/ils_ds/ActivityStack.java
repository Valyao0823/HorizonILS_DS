package com.example.hesolutions.ils_ds;

/**
 * Created by hesolutions on 2016-04-14.
 */
import android.app.Activity;
import android.app.ActivityGroup;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import java.util.Stack;

/**
 * Created by hesolutions on 2016-02-02.
 */
public class ActivityStack extends ActivityGroup {

    public Stack<String> stack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (stack == null)
            stack = new Stack<String>();
        // start default activity
        push("FirstStackActivity", new Intent(this, GlobalCalendar.class));
    }

    @Override
    public void finishFromChild(Activity child) {
        pop();
    }

    @Override
    public void onBackPressed() {
        pop();
    }

    public void push(String id, Intent intent) {
        Window window = getLocalActivityManager().startActivity(id, intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        if (window != null) {
            stack.push(id);
            setContentView(window.getDecorView());
            setContentView(window.getDecorView());
        }
    }

    public void pop() {
        if (stack.size() == 1)
            finish();
        LocalActivityManager manager = getLocalActivityManager();
        manager.destroyActivity(stack.pop(), true);
        if (stack.size() > 0) {
            Intent lastIntent = manager.getActivity(stack.peek()).getIntent();
            Window newWindow = manager.startActivity(stack.peek(), lastIntent);
            setContentView(newWindow.getDecorView());
        }
    }

    public String popid(){
        String manager = getLocalActivityManager().getActivity(stack.peek()).getComponentName().getClassName();
        return manager;
    }

}
