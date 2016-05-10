package com.example.hesolutions.ils_ds;

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
public class ActivityAdminStack extends ActivityGroup {

    private Stack<String> stack;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (stack == null)
            stack = new Stack<String>();
        // start default activity
        push("FirstStackActivity", new Intent(this, AdminPage.class));
    }

    @Override
    public void finishFromChild(Activity child) {
        pop();
    }

    @Override
    public void onBackPressed() {
        // super.onBackPressed(); // Comment this super call to avoid calling finish()
    }

    public void push(String id, Intent intent) {
        Window window = getLocalActivityManager().startActivity(id, intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        if (window != null) {
            stack.push(id);
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

}
