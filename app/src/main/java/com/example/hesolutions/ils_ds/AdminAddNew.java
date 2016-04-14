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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class AdminAddNew extends Activity {
    EditText MSG,CODE,NameS,NameD;
    Button SAVE, savedevice, savesector, cancel, Apply, Applydevice;
    RelativeLayout addNewUser, addnewsector, addnewdevice, assignuser, assigndevice;

    String userName = "";
    String sectorName = "";
    String result = "";

    ListView assignsector, deviceassign;
    //MyCustomAdapter deviceAdapter =null;
    //MyCustomDeviceAdapter assigning = null;
    int usecase;
    String key , oldcolor;
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
        assignsector = (ListView) findViewById(R.id.assignsector);
        deviceassign = (ListView) findViewById(R.id.deviceassign);
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
        /*
        else if (usecase == 4) {
            assignuser.setVisibility(View.VISIBLE);
            UserName = getIntent().getStringExtra("UserName");
            SectorName = getIntent().getStringExtra("SectorName");
            addNewUser.setVisibility(View.GONE);
            addnewsector.setVisibility(View.GONE);
            addnewdevice.setVisibility(View.GONE);
            assigndevice.setVisibility(View.GONE);
        } else if (usecase == 5) {
            assigndevice.setVisibility(View.VISIBLE);
            assignsector.setVisibility(View.GONE);
            assignuser.setVisibility(View.GONE);
            result = getIntent().getStringExtra("result");
            userName = getIntent().getStringExtra("userName");
            sectorName = getIntent().getStringExtra("sectorName");
            assigndevice.setVisibility(View.GONE);
            boolean boolresu = false;
            if (result != null && result.length() == 7) {
                int devtype;
                int subdevtype = 0;
                if (Integer.parseInt(result.substring(1, 2)) == 2 && Integer.parseInt(result.substring(0, 1)) == 5) {
                    subdevtype = 2;
                    devtype = 5;
                } else if (Integer.parseInt(result.substring(1, 2)) != 1) {
                    devtype = Integer.parseInt(result.substring(0, 2));
                } else {
                    devtype = Integer.parseInt(result.substring(0, 1));
                }
                String deviceaddress = result.substring(2, result.length());
                if (Integer.parseInt(deviceaddress) <= 65500) {

                    mDevice = new Device();
                    if (subdevtype > 0) {
                        try {
                            mDevice.setSubDeviceType((short) subdevtype);
                        } catch (Exception e) {
                            Toast.makeText(this, getResources().getString(R.string.scanerfail), Toast.LENGTH_LONG).show();
                            return;
                        }
                    }
                    mDevice.setChannelInfo((short) 1);
                    mDevice.setDeviceType((short) devtype);
                    mDevice.setDeviceAddress((short) Integer.parseInt(deviceaddress));
                    if (!findDeviceAddress(mDevice.getDeviceAddress())) {
                        assignuser.setVisibility(View.GONE);
                        addNewUser.setVisibility(View.GONE);
                        addnewsector.setVisibility(View.GONE);
                        assigndevice.setVisibility(View.GONE);
                        addnewdevice.setVisibility(View.VISIBLE);
                    } else {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdminAddNew.this.getParent());
                        alertDialog.setTitle("Error");
                        alertDialog.setMessage("This device has been added already");
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
                                dialog.cancel();
                                Intent startNewActivityIntent = new Intent(AdminAddNew.this, AdminPage.class);
                                ActivityAdminStack activityadminStack = (ActivityAdminStack) getParent();
                                activityadminStack.push("Adr", startNewActivityIntent);

                            }
                        });
                        alertDialog.show();
                    }

                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    result = null;
                    boolresu = true;
                }
            }


            if (result != null && !boolresu) {
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
        }else if (usecase == 6) {
            UserName = getIntent().getStringExtra("UserName");
            assignuser.setVisibility(View.GONE);
            assigndevice.setVisibility(View.GONE);
            addNewUser.setVisibility(View.VISIBLE);
            addnewsector.setVisibility(View.GONE);
            addnewdevice.setVisibility(View.GONE);
            MSG.setText(UserName);
            MSG.setEnabled(false);
            key = "";
            BiMap<String, ArrayList> bimap = DataManager.getInstance().getaccount();
            for (Map.Entry<String, ArrayList> entry : bimap.entrySet()) {
                ArrayList<String>  account = entry.getValue();
                if (account.get(0).equals(UserName)){
                    key = entry.getKey();
                    oldcolor = account.get(1);
                }
            }
            if (!key.equals("")) CODE.setText(key);
        }else if (usecase == 7)
        {
            assigndevice.setVisibility(View.VISIBLE);
            assignuser.setVisibility(View.GONE);
            UserName = getIntent().getStringExtra("userName");
            SectorName = getIntent().getStringExtra("sectorName");
            addNewUser.setVisibility(View.GONE);
            addnewsector.setVisibility(View.GONE);
            addnewdevice.setVisibility(View.GONE);
        }

        */
        SAVE.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View v) {
                if (usecase == 6)
                {
                    /*
                    final BiMap<String, ArrayList> bimap = DataManager.getInstance().getaccount();
                    final String Passwords = CODE.getText().toString();
                    if (Passwords.length() != 4 && bimap.get(Passwords) != null || Passwords.isEmpty())
                    {
                        Toast.makeText(AdminAddNew.this, "Missing accounts= or password", Toast.LENGTH_SHORT).show();
                    }else if(bimap.get(Passwords) != null) {
                        Toast.makeText(AdminAddNew.this, "This password already exists" , Toast.LENGTH_SHORT).show();
                    }else if (Passwords.equals("6665")){
                        Toast.makeText(AdminAddNew.this, "The password cannot be the same as for the Admin", Toast.LENGTH_SHORT).show();
                    }else {
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdminAddNew.this.getParent());
                        alertDialog.setTitle("Warning");
                        alertDialog.setCancelable(false);
                        alertDialog.setMessage("Do you want to make the change?");
                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        bimap.remove(key);
                                        ArrayList<String> accout = new ArrayList<String>();
                                        accout.add(UserName);
                                        accout.add(oldcolor);
                                        bimap.put(Passwords, accout);
                                        DataManager.getInstance().setaccount(bimap);
                                        MSG.setText("");
                                        CODE.setText("");
                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                                        usecase = 0;
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
*/
                }else {
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


        savesector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name =  NameS.getText().toString();
                boolean uniquesectorname = true;
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
                        DatabaseManager.getInstance().addSector(name, userName, null);
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        ActivityAdminStack activityAdminStack = (ActivityAdminStack) getParent();
                        activityAdminStack.pop();
                        Toast.makeText(getApplicationContext(), "Data saved successfully!", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


        savedevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = NameD.getText().toString();
                if (name.equals("")) {
                    Toast.makeText(AdminAddNew.this, "Device name cannot be empty", Toast.LENGTH_SHORT).show();
                }else if (name.contains(" ")) {
                    Toast.makeText(AdminAddNew.this, "No spaces allowed", Toast.LENGTH_SHORT).show();
                    NameD.setText("");
                }else{
                    if (DatabaseManager.getInstance().getDeviceList().contains(name)) {
                        Toast.makeText(AdminAddNew.this, "This device name already exists", Toast.LENGTH_SHORT).show();
                    } else {

                        DatabaseManager.getInstance().addDevice(sectorName, name);
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                        Toast.makeText(getApplicationContext(), "Data saved successfully!", Toast.LENGTH_LONG).show();
                        Intent startNewActivityIntent = new Intent(AdminAddNew.this, AdminPage.class);
                        ActivityAdminStack activityadminStack = (ActivityAdminStack) getParent();
                        startNewActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activityadminStack.push("AdminPage", startNewActivityIntent);
                    }
                }
            }
        });

/*
        BiMap<String, ArrayList> nameset = DataManager.getInstance().getaccount();
        ArrayList<Group> names = new ArrayList<>();
        if (!nameset.isEmpty()) {
            names.clear();
            for (Map.Entry<String, ArrayList> entry : nameset.entrySet()) {
                String name = (String) entry.getValue().get(0);
                if (!name.equals(UserName)) {
                    Group group = new Group(name, false);
                    names.add(group);
                }
            }
        }

        deviceAdapter = new MyCustomAdapter(this, R.layout.devicelist, names);
        assignsector.setAdapter(deviceAdapter);


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startNewActivityIntent = new Intent(AdminAddNew.this, AdminPage.class);
                ActivityAdminStack activityadminStack = (ActivityAdminStack) getParent();
                startNewActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activityadminStack.push("Admin", startNewActivityIntent);
            }
        });

        Apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<Group> choosedevice = new ArrayList<Group>();
                if (choosedevice.isEmpty()) {
                    ArrayList<Group> choosegrouplist = deviceAdapter.arrayList;
                    for (int i = 0; i < choosegrouplist.size(); i++) {
                        Group group = choosegrouplist.get(i);
                        if (group.getSelected() == true) {
                            choosedevice.add(group);
                        }
                    }
                }
                sector = DataManager.getInstance().getsector();
                sectordetail = sector.get(UserName);
                final ArrayList<Device> list = sectordetail.get(SectorName);
                HashMap<String, ArrayList<Device>> newassignsector = new HashMap<String, ArrayList<Device>>();
                newassignsector.put(SectorName, list);

                if (choosedevice.isEmpty() || choosedevice == null) {
                    Toast.makeText(AdminAddNew.this, "At least one user should be selected", Toast.LENGTH_SHORT).show();
                } else {
                    for (int k = 0; k < choosedevice.size(); k++) {
                        final HashMap<String, ArrayList<Device>> selectedsectordetail = sector.get(choosedevice.get(k).getName());
                        final String selectedusername = choosedevice.get(k).getName();
                        // case 1: there has already been sectors assigned to the selected user
                        if (selectedsectordetail != null && !selectedsectordetail.isEmpty()) {
                            // case1.1: it alreay has this assigned sector
                            // case 1.2: it does not have the assigned sector
                            selectedsectordetail.put(SectorName, list);
                            sector.put(selectedusername, selectedsectordetail);
                            DataManager.getInstance().setsector(sector);
                        }else
                        {
                            //case 2: no sectors has been assigned to this sector
                            sector.put(choosedevice.get(k).getName(), newassignsector);
                            DataManager.getInstance().setsector(sector);
                        }
                    }

                    Intent startNewActivityIntent = new Intent(AdminAddNew.this, AdminPage.class);
                    ActivityAdminStack activityadminStack = (ActivityAdminStack) getParent();
                    startNewActivityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activityadminStack.push("AdminPage", startNewActivityIntent);
                }
            }
        });

        final ArrayList<Device> deviceArrayList = DatabaseManager.getInstance().LoadDeviceList("devicelist");
        ArrayList<DeviceGroup> devicenames = new ArrayList<>();
        if (!deviceArrayList.isEmpty()) {
            for (Device device:deviceArrayList)
            {
                String name = device.getDeviceName();
                DeviceGroup group = new DeviceGroup(name,device,false);
                devicenames.add(group);
            }
        }
        assigning = new MyCustomDeviceAdapter(this, R.layout.devicelist, devicenames);
        deviceassign.setAdapter(assigning);
        Applydevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ArrayList<Device> choosedevice = new ArrayList<Device>();
                if (choosedevice.isEmpty()) {
                    ArrayList<DeviceGroup> choosegrouplist = assigning.arrayList;
                    for (int i = 0; i < choosegrouplist.size(); i++) {
                        DeviceGroup group = choosegrouplist.get(i);
                        if (group.getSelected() == true) {
                            choosedevice.add(group.getDevice());
                        }
                    }
                }
                sector = DataManager.getInstance().getsector();
                sectordetail = sector.get(UserName);
                final ArrayList<Device> list = sectordetail.get(SectorName);

                if (choosedevice.isEmpty() || choosedevice == null) {
                    Toast.makeText(AdminAddNew.this, "At least one device should be selected", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdminAddNew.this.getParent());
                    alertDialog.setTitle("Warning");
                    alertDialog.setMessage("This action will remove the device from the old sector.");
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            for (Device device : choosedevice) {
                                for (Map.Entry<String, HashMap> entry : sector.entrySet()) {
                                    HashMap<String, ArrayList<Device>> sectorinfo = entry.getValue();
                                    for (Map.Entry<String, ArrayList<Device>> deviceentry : sectorinfo.entrySet()) {
                                        ArrayList<Device> deviceArrayList = deviceentry.getValue();
                                        if (deviceArrayList != null) {
                                            Iterator<Device> deviceIterator = deviceArrayList.iterator();
                                            while (deviceIterator.hasNext()) {
                                                if (device.getDeviceName().equals(deviceIterator.next().getDeviceName())) {
                                                    deviceIterator.remove();
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            if (list!=null)
                            {
                                list.addAll(choosedevice);
                                for (Map.Entry<String, HashMap> sectorinfo : sector.entrySet()) {
                                    HashMap<String, ArrayList<Device>> details = sectorinfo.getValue();
                                    for (Map.Entry<String, ArrayList<Device>> singlesectorinfo : details.entrySet())
                                    {
                                        if (singlesectorinfo.getKey().equals(SectorName))
                                        {
                                            details.put(SectorName, list);
                                            sector.put(sectorinfo.getKey(), details);
                                        }
                                    }
                                }
                            }else{
                                sectordetail.put(SectorName, choosedevice);
                                for (Map.Entry<String, HashMap> sectorinfo : sector.entrySet()) {
                                    HashMap<String, ArrayList<Device>> details = sectorinfo.getValue();
                                    for (Map.Entry<String, ArrayList<Device>> singlesectorinfo : details.entrySet())
                                    {
                                        if (singlesectorinfo.getKey().equals(SectorName))
                                        {
                                            details.put(SectorName, choosedevice);
                                            sector.put(sectorinfo.getKey(), details);
                                        }
                                    }
                                }
                            }
                            DataManager.getInstance().setsector(sector);
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
*/
    }

/*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        userName = "";
        UserName = "";
        SectorName = "";
        sectorName = "";
        result = "";
        mDevice = new Device();
        deviceAdapter =null;
        usecase = 0;
        key = "";
        oldcolor= "";
        sectordetail = null;
        sector = null;
        myHandler.removeCallbacks(myRunnable);
    }

    private boolean findDeviceName(String deviceName) {
        ArrayList<Device> check = DatabaseManager.getInstance().LoadDeviceList("devicelist");
        if (check!=null) {
            for (Device device: check)
            {
                if (device.getDeviceName().equals(deviceName)) return true;
            }
        }
        return false;
    }

    private boolean findDeviceAddress(short deviceAddress) {
        ArrayList<Device> check = DatabaseManager.getInstance().LoadDeviceList("devicelist");
        if (check!=null) {
            for (Device device:check)
            {
                if (device.getDeviceAddress()== deviceAddress) return true;
            }
        }
        return false;
    }
    private class MyCustomAdapter extends ArrayAdapter<Group> {
        ArrayList<Group> arrayList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<Group> arrayList) {
            super(context, textViewResourceId, arrayList);
            this.arrayList = arrayList;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.devicelist, null);

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


    private class MyCustomDeviceAdapter extends ArrayAdapter<DeviceGroup> {
        ArrayList<DeviceGroup> arrayList;

        public MyCustomDeviceAdapter(Context context, int textViewResourceId,
                                     ArrayList<DeviceGroup> arrayList) {
            super(context, textViewResourceId, arrayList);
            this.arrayList = arrayList;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = vi.inflate(R.layout.devicelist, null);

            }

            DeviceGroup group = arrayList.get(position);
            TextView name = (TextView) convertView.findViewById(R.id.name);
            final EnhancedSwitch checked = (EnhancedSwitch) convertView.findViewById(R.id.checked);
            name.setText(group.getName());
            checked.setCheckedProgrammatically(group.getSelected());
            checked.setTag(group);

            checked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    DeviceGroup group = (DeviceGroup) buttonView.getTag();
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

    public class DeviceGroup
    {
        String name;
        Device device;
        boolean ischecked;

        public DeviceGroup(String name, Device device, boolean ischecked)
        {
            this.name = name;
            this.device = device;
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
        public Device getDevice()
        {
            return device;
        }
        public void setDevice(Device device)
        {
            this.device = device;
        }

    }

    public void Restart(){
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdminAddNew.this.getParent());
        alertDialog.setTitle("Error");
        alertDialog.setMessage("Gateway error, please check the gateway and then try again.");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        runOnUiThread(new Runnable() {
            public void run() {
                alertDialog.show();
            }
        });

    }
*/
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
