package com.example.hesolutions.ils_ds;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.app.Activity;
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
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.zxing.activity.CaptureActivity;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Random;

public class AdminAddNew extends Activity {
    EditText MSG,CODE,NameS,NameD;
    Button SAVE, savedevice, savesector, cancel, Apply, Applydevice;
    RelativeLayout addNewUser, addnewsector, addnewdevice, assignuser, assigndevice;
    String userName = "";
    String sectorName = "";
    String deviceName = "";
    String result = "";
    String[] deviceinformation;
    ListView sharesector, sharedevice;
    ArrayList<Group> usernames, devicenames;
    MyCustomAdapter sectoradapter =null, deviceadapter = null;
    int usecase = 0;
    Handler myHandler;
    Runnable myRunnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_new);
        SAVE = (Button) findViewById(R.id.SAVE);
        MSG = (EditText) findViewById(R.id.MSG);
        CODE = (EditText) findViewById(R.id.CODE);
        NameS = (EditText) findViewById(R.id.NameS);
        NameD = (EditText) findViewById(R.id.NameD);
        savedevice = (Button) findViewById(R.id.savedevice);
        savesector = (Button) findViewById(R.id.savesector);
        cancel = (Button) findViewById(R.id.cancel);
        addNewUser = (RelativeLayout) findViewById(R.id.addNewUser);
        addnewsector = (RelativeLayout) findViewById(R.id.addnewsector);
        addnewdevice = (RelativeLayout) findViewById(R.id.addnewdevice);
        assignuser = (RelativeLayout) findViewById(R.id.assignuser);
        assigndevice = (RelativeLayout) findViewById(R.id.assigndevice);
        sharesector = (ListView) findViewById(R.id.assignsector);
        sharedevice = (ListView) findViewById(R.id.deviceassign);
        Apply = (Button) findViewById(R.id.Apply);
        Applydevice = (Button) findViewById(R.id.Applydevice);

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
                Intent intent = new Intent(AdminAddNew.this, ScreenSaver.class);
                myHandler.removeCallbacks(myRunnable);
                startActivity(intent);
            }
        };
        myHandler.postDelayed(myRunnable, 60* 3 * 1000);

        usecase = getIntent().getIntExtra("Case", 0);
        if (usecase == 1) {
            MSG.requestFocus();
            addNewUser.setVisibility(View.VISIBLE);
            addnewsector.setVisibility(View.GONE);
            addnewdevice.setVisibility(View.GONE);
            assignuser.setVisibility(View.GONE);
            assigndevice.setVisibility(View.GONE);
        } else if (usecase == 2) {
            NameS.requestFocus();
            addnewsector.setVisibility(View.VISIBLE);
            addNewUser.setVisibility(View.GONE);
            userName = getIntent().getStringExtra("userName");
            addnewdevice.setVisibility(View.GONE);
            assignuser.setVisibility(View.GONE);
            assigndevice.setVisibility(View.GONE);
        }
        else if (usecase == 3) {
            assignuser.setVisibility(View.VISIBLE);
            userName = getIntent().getStringExtra("userName");
            sectorName = getIntent().getStringExtra("sectorName");
            addNewUser.setVisibility(View.GONE);
            addnewsector.setVisibility(View.GONE);
            addnewdevice.setVisibility(View.GONE);
            assigndevice.setVisibility(View.GONE);
        }else if (usecase == 4) {
            userName = getIntent().getStringExtra("UserName");
            assignuser.setVisibility(View.GONE);
            assigndevice.setVisibility(View.GONE);
            addNewUser.setVisibility(View.VISIBLE);
            addnewsector.setVisibility(View.GONE);
            addnewdevice.setVisibility(View.GONE);
            MSG.setText(userName);
            MSG.setEnabled(false);
            String password = DatabaseManager.getInstance().getPassword(userName);
            CODE.setText(password);
        } else if (usecase == 5) {

            result = getIntent().getStringExtra("result");
            userName = getIntent().getStringExtra("userName");
            sectorName = getIntent().getStringExtra("sectorName");

            boolean boolresult = true;
            if (result != null && result.length() == 20 && result.contains("_")) {
                deviceinformation = new String[5];
                int count = 0;
                for (String substring: result.split("_")){
                    switch(count)
                    {
                        case 0: deviceinformation[0] = substring; break; // companyname
                        case 1: deviceinformation[1] = substring; break; // location
                        case 2: deviceinformation[2] = substring; break; // devicetype
                        case 3: deviceinformation[3] = substring; break; // devicedetail
                        case 4: deviceinformation[4] = substring; break; // devicenude
                    }
                    count++;
                }
                if (deviceinformation[0].length()==2 && deviceinformation[1].length()==2 && deviceinformation[2].length()==2
                        && deviceinformation[3].length()==7 && deviceinformation[4].length()==3)
                {
                    assignuser.setVisibility(View.GONE);
                    addNewUser.setVisibility(View.GONE);
                    addnewsector.setVisibility(View.GONE);
                    assigndevice.setVisibility(View.GONE);
                    addnewdevice.setVisibility(View.VISIBLE);
                }else{boolresult = false;}
            }else {boolresult = false;}
            if (boolresult==false) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdminAddNew.this.getParent());
                alertDialog.setTitle("Error");
                alertDialog.setMessage("QR code error");
                alertDialog.setPositiveButton("Scan Another", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent startNewActivityIntent = new Intent(AdminAddNew.this, CaptureActivity.class);
                        ActivityAdminStack activityadminStack = (ActivityAdminStack) getParent();
                        startNewActivityIntent.putExtra("userName", userName);
                        startNewActivityIntent.putExtra("sectorName", sectorName);
                        activityadminStack.push("Scanner", startNewActivityIntent);

                    }
                });
                alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent startNewActivityIntent = new Intent(AdminAddNew.this, AdminPage.class);
                        ActivityAdminStack activityadminStack = (ActivityAdminStack) getParent();
                        activityadminStack.push("Admin", startNewActivityIntent);
                    }
                });
                alertDialog.show();
            }
        } else if (usecase == 6)
        {
            assigndevice.setVisibility(View.VISIBLE);
            assignuser.setVisibility(View.GONE);
            userName = getIntent().getStringExtra("userName");
            sectorName = getIntent().getStringExtra("sectorName");
            addNewUser.setVisibility(View.GONE);
            addnewsector.setVisibility(View.GONE);
            addnewdevice.setVisibility(View.GONE);
        }else if (usecase == 7)
        {
            assigndevice.setVisibility(View.GONE);
            assignuser.setVisibility(View.GONE);
            userName = getIntent().getStringExtra("userName");
            sectorName = getIntent().getStringExtra("sectorName");
            deviceName = getIntent().getStringExtra("deviceName");
            addNewUser.setVisibility(View.GONE);
            addnewsector.setVisibility(View.GONE);
            addnewdevice.setVisibility(View.VISIBLE);
        }


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startNewActivityIntent = new Intent(AdminAddNew.this, AdminPage.class);
                ActivityAdminStack activityadminStack = (ActivityAdminStack) getParent();
                startNewActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activityadminStack.push("Admin", startNewActivityIntent);
            }
        });

        // case 1 and case 4
        SAVE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (usecase == 4)
                {
                    String password = DatabaseManager.getInstance().getPassword(userName);
                    final String Passwords = CODE.getText().toString();
                    if (Passwords.equals(password)) {
                        Toast.makeText(AdminAddNew.this, "The new password is the same as the old password.", Toast.LENGTH_SHORT).show();
                    }else if (DatabaseManager.getInstance().getPasswordList().contains(Passwords)) {
                        Toast.makeText(getApplicationContext(), "This account password already exists", Toast.LENGTH_LONG).show();
                        CODE.setText("");
                    }else if (Passwords.length() != 4){
                        Toast.makeText(getApplicationContext(), "The password must be 4 digits", Toast.LENGTH_LONG).show();
                        CODE.setText("");
                    }else if (Passwords.equals("6665")) {
                        Toast.makeText(AdminAddNew.this, "The password cannot be the same as for the Admin", Toast.LENGTH_SHORT).show();
                        CODE.setText("");
                    }else if (Passwords.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Missing password", Toast.LENGTH_LONG).show();
                    }else{
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdminAddNew.this.getParent());
                        alertDialog.setTitle("Warning");
                        alertDialog.setCancelable(false);
                        alertDialog.setMessage("Do you want to make the change?");
                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        DatabaseManager.getInstance().updateUser(userName, Passwords);
                                        MSG.setText("");
                                        CODE.setText("");
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                                        dialog.cancel();
                                        ActivityAdminStack activityAdminStack = (ActivityAdminStack) getParent();
                                        activityAdminStack.pop();
                                        Toast.makeText(getApplicationContext(), "Data saved successfully!", Toast.LENGTH_LONG).show();
                                    }
                                }
                        );
                        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        alertDialog.show();
                        usecase = 0;
                    }
                }else {
                    // case 1 add user
                    String Accounts = MSG.getText().toString();    //value
                    String Passwords = CODE.getText().toString();  //key
                    String[] arr = {"#59dbe0", "#f57f68", "#87d288", "#f8b552", "#39add1", "#3079ab", "#c25975", "#e15258",
                            "#f9845b", "#838cc7", "#7d669e", "#53bbb4", "#51b46d", "#e0ab18", "#f092b0", "#b7c0c7"};
                    Random random = new Random();
                    int select = random.nextInt(arr.length);
                    String color = arr[select];

                    if (DatabaseManager.getInstance().getUserNameList().contains(Accounts))
                    {
                        Toast.makeText(getApplicationContext(), "This account name already exists", Toast.LENGTH_LONG).show();
                        MSG.setText("");
                        CODE.setText("");
                    }else if (DatabaseManager.getInstance().getPasswordList().contains(Passwords)) {
                        Toast.makeText(getApplicationContext(), "This account password already exists", Toast.LENGTH_LONG).show();
                        MSG.setText("");
                        CODE.setText("");
                    }else if (Passwords.length() != 4){
                        Toast.makeText(getApplicationContext(), "The password must be 4 digits", Toast.LENGTH_LONG).show();
                        CODE.setText("");
                    }else if (Passwords.equals("6665")) {
                        Toast.makeText(AdminAddNew.this, "The password cannot be the same as for the Admin", Toast.LENGTH_SHORT).show();
                        MSG.setText("");
                        CODE.setText("");
                    } else if (Accounts.contains(" ")){
                        Toast.makeText(AdminAddNew.this, "No spaces allowed", Toast.LENGTH_SHORT).show();
                        MSG.setText("");
                    }else if (Accounts.isEmpty() || Passwords.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Missing account or password", Toast.LENGTH_LONG).show();
                    }else{
                        DatabaseManager.getInstance().addUser(Passwords, Accounts, color);
                        MSG.setText("");
                        CODE.setText("");
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        ActivityAdminStack activityAdminStack = (ActivityAdminStack) getParent();
                        activityAdminStack.pop();
                        Toast.makeText(getApplicationContext(), "Data saved successfully!", Toast.LENGTH_LONG).show();
                    }
                }
                //addNewUser.setVisibility(View.INVISIBLE);
            }
        });

        // case 2 add sector
        savesector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name =  NameS.getText().toString();
                if (name.equals("")) {
                    Toast.makeText(AdminAddNew.this, "Sector name cannot be empty", Toast.LENGTH_SHORT).show();
                }else if (name.contains(" ")){
                    Toast.makeText(AdminAddNew.this, "No spaces allowed", Toast.LENGTH_SHORT).show();
                    NameS.setText("");
                }else {
                    ArrayList<String> sector = DatabaseManager.getInstance().getSectorList();
                    if (sector.contains(name))
                    {
                        NameS.setText("");
                        Toast.makeText(AdminAddNew.this, "This sector name already exists", Toast.LENGTH_SHORT).show();
                    }else{
                        DatabaseManager.getInstance().addSector(name);
                        DatabaseManager.getInstance().assignSector(userName, name);
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        ActivityAdminStack activityAdminStack = (ActivityAdminStack) getParent();
                        activityAdminStack.pop();
                        Toast.makeText(getApplicationContext(), "Data saved successfully!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        // case 5 and case 7
        savedevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = NameD.getText().toString();
                if (usecase == 7){
                    if (name.equals("")) {
                        Toast.makeText(AdminAddNew.this, "Device name cannot be empty", Toast.LENGTH_SHORT).show();
                    } else if (name.contains(" ")) {
                        Toast.makeText(AdminAddNew.this, "No spaces allowed", Toast.LENGTH_SHORT).show();
                        NameD.setText("");
                    } else {
                        if (name.equals(deviceName)) {
                            Toast.makeText(AdminAddNew.this, "This new device name is the same as the old name.", Toast.LENGTH_SHORT).show();
                        } else {
                            //get the nude of the module
                            DatabaseManager.getInstance().changeDeviceName(deviceName, name);
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            Toast.makeText(getApplicationContext(), "Data saved successfully!", Toast.LENGTH_LONG).show();
                            Intent startNewActivityIntent = new Intent(AdminAddNew.this, AdminPage.class);
                            ActivityAdminStack activityadminStack = (ActivityAdminStack) getParent();
                            startNewActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            activityadminStack.push("AdminPage", startNewActivityIntent);
                        }
                    }
                }else {
                    if (name.equals("")) {
                        Toast.makeText(AdminAddNew.this, "Device name cannot be empty", Toast.LENGTH_SHORT).show();
                    } else if (name.contains(" ")) {
                        Toast.makeText(AdminAddNew.this, "No spaces allowed", Toast.LENGTH_SHORT).show();
                        NameD.setText("");
                    } else {
                        if (DatabaseManager.getInstance().getDeviceList().contains(name)) {
                            Toast.makeText(AdminAddNew.this, "This device name already exists", Toast.LENGTH_SHORT).show();
                        } else {
                            //get the nude of the module
                            String companyname = deviceinformation[0];
                            String location = deviceinformation[1];
                            String devicetype = deviceinformation[2];
                            String devicedetail = deviceinformation[3];
                            Integer devicenude = Integer.parseInt(deviceinformation[4]);
                            DatabaseManager.getInstance().addDevice(name, devicenude, sectorName, companyname, location, devicetype, devicedetail);
                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            Toast.makeText(getApplicationContext(), "Data saved successfully!", Toast.LENGTH_LONG).show();
                            Intent startNewActivityIntent = new Intent(AdminAddNew.this, AdminPage.class);
                            ActivityAdminStack activityadminStack = (ActivityAdminStack) getParent();
                            startNewActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            activityadminStack.push("AdminPage", startNewActivityIntent);
                        }
                    }
                }
            }
        });

        // case 3 - share sector
        ArrayList<String> allusernames = DatabaseManager.getInstance().getUserNameList();
        allusernames.remove(userName);
        usernames = new ArrayList<Group>();
        if (!allusernames.isEmpty()) {
            for (String username:allusernames)
            {
                Group group = new Group(username, false);
                usernames.add(group);
            }
        }
        sectoradapter = new MyCustomAdapter(this, R.layout.devicelistadmin, usernames);
        sharesector.setAdapter(sectoradapter);
        Apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> chooseuser = new ArrayList<String>();
                if (chooseuser.isEmpty()) {
                    ArrayList<Group> choosegrouplist = sectoradapter.getlist();
                    for (int i = 0; i < choosegrouplist.size(); i++) {
                        Group group = choosegrouplist.get(i);
                        if (group.getSelected() == true) {
                            chooseuser.add(group.getName());
                        }
                    }
                }

                if (chooseuser.isEmpty() || chooseuser == null) {
                    Toast.makeText(AdminAddNew.this, "At least one user should be selected", Toast.LENGTH_SHORT).show();
                } else {
                    for (String selectedusername: chooseuser) {
                        DatabaseManager.getInstance().assignSector(selectedusername, sectorName);
                    }
                    Intent startNewActivityIntent = new Intent(AdminAddNew.this, AdminPage.class);
                    ActivityAdminStack activityadminStack = (ActivityAdminStack) getParent();
                    startNewActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activityadminStack.push("AdminPage", startNewActivityIntent);
                }
            }
        });
        // case 6 load old devices
        ArrayList<String> alldevices = DatabaseManager.getInstance().getDeviceList();
        devicenames = new ArrayList<>();
        if (!alldevices.isEmpty()) {
            for (String devicename:alldevices)
            {
                Group group = new Group(devicename, false);
                devicenames.add(group);
            }
        }
        deviceadapter = new MyCustomAdapter(this, R.layout.deviceswitch, devicenames);
        sharedevice.setAdapter(deviceadapter);
        Applydevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<String> choosedevice = new ArrayList<String>();
                if (choosedevice.isEmpty()) {
                    ArrayList<Group> choosegrouplist = deviceadapter.getlist();
                    for (int i = 0; i < choosegrouplist.size(); i++) {
                        Group group = choosegrouplist.get(i);
                        if (group.getSelected() == true) {
                            choosedevice.add(group.getName());
                        }
                    }
                }

                if (choosedevice.isEmpty() || choosedevice == null) {
                    Toast.makeText(AdminAddNew.this, "At least one device should be selected", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdminAddNew.this.getParent());
                    alertDialog.setTitle("Warning");
                    alertDialog.setMessage("This action will remove the device from the old sector.");
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            for (String device : choosedevice) {
                                DatabaseManager.getInstance().updateDevicefromSector(device,sectorName);
                            }
                            Intent startNewActivityIntent = new Intent(AdminAddNew.this, AdminPage.class);
                            ActivityAdminStack activityadminStack = (ActivityAdminStack) getParent();
                            startNewActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            activityadminStack.push("AdminPage", startNewActivityIntent);
                        }
                    });
                    alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    alertDialog.show();
                }
            }
        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        userName = "";
        sectorName = "";
        result = "";
        usecase = 0;
        myHandler.removeCallbacks(myRunnable);
        deviceinformation = new String[1];
        usernames = new ArrayList<>();
        sectoradapter =null;
        deviceadapter = null;
        usecase = 0;
    }

    private class MyCustomAdapter extends ArrayAdapter<Group> {
        ArrayList<Group> arrayList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Group> arrayList) {
            super(context, textViewResourceId, arrayList);
            this.arrayList = arrayList;
        }

        public ArrayList<Group> getlist(){return this.arrayList;}
        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.sectorswitch, null);

            }
            Group group = arrayList.get(position);
            TextView name = (TextView) convertView.findViewById(R.id.textView);
            final EnhancedSwitch checked = (EnhancedSwitch) convertView.findViewById(R.id.switchid);
            name.setText(group.getName());
            checked.setCheckedProgrammatically(group.getSelected());
            checked.setTag(group);

            checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Group group = (Group) buttonView.getTag();
                    if (checked.isChecked() == true) {
                        group.setSelected(true);
                    } else {
                        group.setSelected(false);
                    }
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }

    }

    private class Group
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
            return this.ischecked;
        }
        public void setSelected(boolean ischecked)
        {
            this.ischecked = ischecked;
        }
        public String getName() {
            return this.name;
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
        myHandler.postDelayed(myRunnable, 6 * 30 * 1000);
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


}
