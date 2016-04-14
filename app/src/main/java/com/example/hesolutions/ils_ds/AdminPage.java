package com.example.hesolutions.ils_ds;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.BiMap;
import com.zxing.activity.CaptureActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminPage extends Activity {

    ArrayList<String> usernamelist, sectornamelist, devicenamelist;
    TextView inforsumuser,inforsumsector,inforsumdevice;
    Button adduser, addsector, adddevice;
    RelativeLayout userlistlayout, sectorlistlayout, devicelistlayout, infor, progresslayout;
    String userName = "";
    String sectorName = "";
    String deviceName = "";
    UserCustomListAdapter useradapter;
    SectorCustomListAdapter sectoradapter;
    DeviceCustomListAdapter deviceadapter;
    final ArrayList<String> names = new ArrayList<>();
    int Selected_User = -1;
    int Selected_Sector = -1;
    int Selected_Device = -1;
    Handler myHandler;
    Runnable myRunnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_page);
        progresslayout = (RelativeLayout)findViewById(R.id.progresslayout);
        userlistlayout = (RelativeLayout)findViewById(R.id.userlistlayout);
        sectorlistlayout = (RelativeLayout)findViewById(R.id.sectorlistlayout);
        devicelistlayout = (RelativeLayout)findViewById(R.id.devicelistlayout);
        adduser = (Button)findViewById(R.id.adduser);
        addsector= (Button)findViewById(R.id.addsector);
        adddevice= (Button)findViewById(R.id.adddevice);
        infor = (RelativeLayout)findViewById(R.id.infor);
        inforsumuser = (TextView)findViewById(R.id.inforsumuser);
        inforsumsector = (TextView)findViewById(R.id.inforsumsector);
        inforsumdevice = (TextView)findViewById(R.id.inforsumdevice);
        myHandler = new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(AdminPage.this, ScreenSaver.class);
                myHandler.removeCallbacks(myRunnable);
                startActivity(intent);
            }
        };
        myHandler.postDelayed(myRunnable, 3*60*1000);
        LoadUserList();
        adduser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                Bitmap bitmap = getScreenShot(rootView);
                DataManager.getInstance().setBitmap(bitmap);
                Intent startNewActivityIntent = new Intent(AdminPage.this, AdminAddNew.class);
                startNewActivityIntent.putExtra("Case", 1);
                ActivityAdminStack activityadminStack = (ActivityAdminStack) getParent();
                activityadminStack.push("AdminAddNew", startNewActivityIntent);

            }
        });


        addsector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                Bitmap bitmap = getScreenShot(rootView);
                DataManager.getInstance().setBitmap(bitmap);
                Intent startNewActivityIntent = new Intent(AdminPage.this, AdminAddNew.class);
                startNewActivityIntent.putExtra("Case", 2);
                startNewActivityIntent.putExtra("userName", userName);
                ActivityAdminStack activityadminStack = (ActivityAdminStack) getParent();
                activityadminStack.push("AdminAddNew", startNewActivityIntent);

            }
        });

        adddevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layout = inflater.inflate(R.layout.devicedialog, null, true);
                AlertDialog.Builder builder = new AlertDialog.Builder(AdminPage.this.getParent())
                        .setView(layout);
                final AlertDialog alertDialog = builder.create();
                Button loaddevice = (Button) layout.findViewById(R.id.loaddevice);
                loaddevice.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ArrayList<String> devices = DatabaseManager.getInstance().getDeviceList();
                        if (devices==null || devices.isEmpty())
                        {
                            Toast.makeText(AdminPage.this, "No device has been scanned yet.", Toast.LENGTH_SHORT).show();
                        }else {
                            View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                            Bitmap bitmap = getScreenShot(rootView);
                            DataManager.getInstance().setBitmap(bitmap);
                            Intent startNewActivityIntent = new Intent(AdminPage.this, AdminAddNew.class);
                            startNewActivityIntent.putExtra("Case", 7);
                            startNewActivityIntent.putExtra("userName", userName);
                            startNewActivityIntent.putExtra("sectorName", sectorName);
                            ActivityAdminStack activityadminStack = (ActivityAdminStack) getParent();
                            activityadminStack.push("AdminAddNew", startNewActivityIntent);
                        }

                        alertDialog.dismiss();
                    }
                });

                Button scannerpage = (Button) layout.findViewById(R.id.scannerpage);
                scannerpage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                        Bitmap bitmap = getScreenShot(rootView);
                        DataManager.getInstance().setBitmap(bitmap);
                        Intent startNewActivityIntent = new Intent(AdminPage.this, CaptureActivity.class);
                        ActivityAdminStack activityadminStack = (ActivityAdminStack) getParent();
                        startNewActivityIntent.putExtra("userName", userName);
                        startNewActivityIntent.putExtra("sectorName", sectorName);
                        activityadminStack.push("Scanner", startNewActivityIntent);
                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();

            }
        });

        GetSummary();
    }

    public void LoadUserList()
    {
        sectorlistlayout.setVisibility(View.INVISIBLE);
        devicelistlayout.setVisibility(View.INVISIBLE);
        ListView userlist = (ListView) findViewById(R.id.userlist);
        usernamelist = DatabaseManager.getInstance().getUserNameList();
        if (usernamelist!=null && !usernamelist.isEmpty()) {
            useradapter = new UserCustomListAdapter(this, usernamelist);
            userlist.setAdapter(useradapter);
            registerForContextMenu(userlist);
            userlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    clickEvent(view);
                    Selected_User = position;
                    Selected_Sector = -1;
                    Selected_Device = -1;
                    useradapter.notifyDataSetChanged();
                }
            });

        }else
        {
            userlist.setAdapter(null);
        }

    }
    public class UserCustomListAdapter extends ArrayAdapter<String> {

        private Activity context;
        private ArrayList<String> userlist;

        public UserCustomListAdapter(Activity adminPage,ArrayList<String> nameslist) {
            super(adminPage, R.layout.devicelistadmin, nameslist);
            this.userlist = nameslist;
            this.context = adminPage;
        }
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.devicelistadmin, null, true);
            TextView txtTitle = (TextView) rowView.findViewById(R.id.textView);
            txtTitle.setText(userlist.get(position));

            if (position==Selected_User)
            {
                txtTitle.setBackground(getResources().getDrawable(R.drawable.buttonclicked));
            }else
            {
                txtTitle.setBackground(getResources().getDrawable(R.drawable.buttonunclick));
            }
            return rowView;
        }

    }

    public void clickEvent(View v) {
        userName = ((TextView) v).getText().toString();
        sectorlistlayout.setVisibility(View.VISIBLE);
        addsector.setVisibility(View.VISIBLE);
        devicelistlayout.setVisibility(View.INVISIBLE);
        ListView sectorlist = (ListView) findViewById(R.id.sectorlist);
        sectornamelist = DatabaseManager.getInstance().getSectorList();
        if (sectornamelist!=null && !sectornamelist.isEmpty())
        {
            sectoradapter = new SectorCustomListAdapter(this, sectornamelist);
            sectorlist.setVisibility(View.VISIBLE);
            sectorlist.setAdapter(sectoradapter);
            registerForContextMenu(sectorlist);
            sectorlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    showDevice(view);
                    Selected_Sector = position;
                    Selected_Device = -1;
                    sectoradapter.notifyDataSetChanged();
                }
            });
        }else{
            sectorlist.setAdapter(null);
        }
    }
    public class SectorCustomListAdapter extends ArrayAdapter<String> {

        private final Activity context;
        private final ArrayList<String> sectolist;

        public SectorCustomListAdapter(Activity context, ArrayList<String> sectolist) {
            super(context, R.layout.devicelistadmin, sectolist);
            this.context = context;
            this.sectolist = sectolist;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.devicelistadmin, null);
            TextView txtTitle = (TextView) rowView.findViewById(R.id.textView);
            final String sectorname1 = sectolist.get(position);
            txtTitle.setText(sectorname1);

            if (position==Selected_Sector)
            {
                txtTitle.setBackground(getResources().getDrawable(R.drawable.buttonclicked));
            }else
            {
                txtTitle.setBackground(getResources().getDrawable(R.drawable.buttonunclick));
            }
            return rowView;
        }
    }

    public void showDevice(View v)
    {
        sectorName = ((TextView) v).getText().toString();
        devicelistlayout.setVisibility(View.VISIBLE);
        adddevice.setVisibility(View.VISIBLE);
        ListView deviceList = (ListView) findViewById(R.id.devicelist);
        devicenamelist = DatabaseManager.getInstance().getDeviceList();
        if (devicenamelist.contains("null"))devicenamelist.remove("null");
        if  (devicenamelist!=null && !devicenamelist.isEmpty())
        {
            deviceadapter = new DeviceCustomListAdapter(this, devicenamelist);
            deviceList.setVisibility(View.VISIBLE);
            registerForContextMenu(deviceList);
            deviceList.setAdapter(deviceadapter);
            deviceList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Selected_Device = position;
                    deviceadapter.notifyDataSetChanged();
                }
            });
        }else{
            deviceList.setAdapter(null);
        }
    }
    public class DeviceCustomListAdapter extends ArrayAdapter<String> {

        private final Activity context;
        private final ArrayList<String> devicenamelist;

        public DeviceCustomListAdapter(Activity context, ArrayList<String> zoneList) {
            super(context, R.layout.devicelistadmin, zoneList);
            this.context = context;
            this.devicenamelist = zoneList;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.devicelistadmin, null);
            TextView txtTitle = (TextView) rowView.findViewById(R.id.textView);
            String devicename1 = devicenamelist.get(position);
            txtTitle.setText(devicename1);
            if (position==Selected_Device)
            {
                txtTitle.setBackground(getResources().getDrawable(R.drawable.buttonclicked));
            }else
            {
                txtTitle.setBackground(getResources().getDrawable(R.drawable.buttonunclick));
            }
            return rowView;
        }

    }


    /*
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.sectorlist) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            sectorName = sectorArray.get(info.position);
            menu.setHeaderTitle(sectorName);
            menu.add(0, 0, 0, "Share");
            menu.add(0, 1, 0, "Remove");
        }
        if (v.getId() == R.id.userlist) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            userName = names.get(info.position);
            menu.setHeaderTitle(userName);
            menu.add(0, 2, 0, "Delete");
            menu.add(0, 4, 0, "Change Password");
        }
        if (v.getId() == R.id.devicelist)
        {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            deviceName = deviceAdptername.get(info.position);
            menu.setHeaderTitle(deviceName);
            menu.add(0, 3, 0, "Delete");
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        sector = DataManager.getInstance().getsector();
        if (menuItemIndex == 1)
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdminPage.this.getParent());
            alertDialog.setTitle("Warning");
            alertDialog.setMessage("Do you want to remove the sector?");
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    ArrayList<Device> array = (ArrayList<Device>) sector.get(userName).get(sectorName);
                    //// delete the lights if this sector is the last one contains device information
                    ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
                    progresslayout.setClickable(true);
                    progressBar.setVisibility(View.VISIBLE);

                    Runnable r = new Runnable() {
                        public void run() {
                            RemoveEvents(userName, sectorName);
                        }
                    };

                    new Thread(r).start();

                    boolean deletedevice = true;
                    HashMap<String, HashMap> sector = DataManager.getInstance().getsector();
                    if (!sector.isEmpty()) {
                        for (Map.Entry<String, HashMap> entry : sector.entrySet()) {
                            String name = (String) entry.getKey();
                            if (!name.equals(userName)) {
                                //check if any other user contains the sector
                                HashMap<String, ArrayList<Device>> sectorinfo = entry.getValue();
                                for (Map.Entry<String, ArrayList<Device>> entrys : sectorinfo.entrySet()) {
                                    if (entrys.getKey().equals(sectorName)) {
                                        deletedevice = false;
                                    }
                                }
                            }
                        }
                    }
                    if (deletedevice == true) {
                        if (array!=null) {
                            Iterator<Device> deviceIterator = array.iterator();
                            while (deviceIterator.hasNext()) {
                                // remove from the devicelist file
                                Device device = deviceIterator.next();
                                // remove from the SQL table
                                byte[] data;
                                data = new byte[]{(byte) 17, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
                                DeviceSocket.getInstance().send(Message.createMessage((byte) 4, DevicePacket.createPacket((byte) 4,
                                                device.getDeviceAddress(), (short) 0, data), device.getGatewayMacAddr(), device.getGatewayPassword(),
                                        device.getGatewaySSID(), AdminPage.this));
                                device.setCurrentParams(data);
                                device.setChannelMark((short) 0);
                                DatabaseManager.getInstance().updateDevice(device);
                                deviceIterator.remove();
                            }
                        }

                        Bitmap bitmap = dataupdate(sectorName+".png");
                        if (bitmap!=null)
                        {
                            File root = Environment.getExternalStorageDirectory();
                            File dir = new File(root.getAbsolutePath() + "/Horizon/Bitmap");
                            File file = new File(dir, sectorName+".png");
                            file.delete();
                        }
                    }else
                    {
                        if (array!=null) {
                            array.clear();
                        }
                    }
                    ListView deviceList = (ListView) findViewById(R.id.devicelist);
                    deviceList.setAdapter(null);
                    sectordetail.remove(sectorArray.get(info.position));
                    sector.put(userName, sectordetail);
                    DataManager.getInstance().setsector(sector);
                    sectorArray.remove(info.position);
                    sectoradapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.INVISIBLE);
                    adddevice.setVisibility(View.INVISIBLE);
                    GetSummary();
                }
            });
            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
        }else if (menuItemIndex == 0)
        {
            BiMap<String, ArrayList> account = DataManager.getInstance().getaccount();
            ArrayList<String> usernumber = new ArrayList<>();
            if (account!=null) {
                for (Map.Entry<String, ArrayList> map : account.entrySet()) {
                    usernumber.add(map.getKey());
                }
            }
            if (usernumber!=null && usernumber.size() > 1) {
                View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                Bitmap bitmap = getScreenShot(rootView);
                DataManager.getInstance().setBitmap(bitmap);
                Intent startNewActivityIntent = new Intent(AdminPage.this, AdminAddNew.class);
                startNewActivityIntent.putExtra("Case", 4);
                startNewActivityIntent.putExtra("UserName", userName);
                startNewActivityIntent.putExtra("SectorName", sectorArray.get(info.position));
                ActivityAdminStack activityadminStack = (ActivityAdminStack) getParent();
                activityadminStack.push("AdminAddNew", startNewActivityIntent);
            }else{
                Toast.makeText(AdminPage.this, "There are no available users to share the sector.", Toast.LENGTH_SHORT).show();
            }
        }else if (menuItemIndex == 2)
        {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdminPage.this.getParent());
            alertDialog.setTitle("Warning");
            alertDialog.setMessage("Do you want to delete the user?");
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    boolean deletedevice = true;
                    //// delete the lights if this sector is the last one contains device information
                    HashMap<String, ArrayList<Device>> sectorinformation = sector.get(userName);
                    if (sectorinformation!=null) {
                        for (Map.Entry<String, ArrayList<Device>> selfloop : sectorinformation.entrySet()) {
                            String deleteselfsectorname = selfloop.getKey();
                            Bitmap bitmap = dataupdate(deleteselfsectorname+".png");
                            if (bitmap!=null)
                            {
                                File root = Environment.getExternalStorageDirectory();
                                File dir = new File(root.getAbsolutePath() + "/Horizon/Bitmap");
                                File file = new File(dir, deleteselfsectorname+".png");
                                file.delete();
                            }


                            for (Map.Entry<String, HashMap> entry : sector.entrySet()) {
                                String name = (String) entry.getKey();
                                if (!name.equals(userName)) {
                                    //check if any other user contains the sector
                                    HashMap<String, ArrayList<Device>> sectorinfo = entry.getValue();
                                    for (Map.Entry<String, ArrayList<Device>> entrys : sectorinfo.entrySet()) {
                                        if (entrys.getKey().equals(deleteselfsectorname)) {
                                            deletedevice = false;
                                        }
                                    }
                                }
                            }

                            if (deletedevice == true) {
                                ArrayList<Device> array = (ArrayList<Device>) sector.get(userName).get(deleteselfsectorname);
                                if (array != null) {
                                    Iterator<Device> deviceIterator = array.iterator();
                                    while (deviceIterator.hasNext()) {
                                        Device device = deviceIterator.next();
                                        byte[] data;
                                        data = new byte[]{(byte) 17, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
                                        DeviceSocket.getInstance().send(Message.createMessage((byte) 4, DevicePacket.createPacket((byte) 4,
                                                        device.getDeviceAddress(), (short) 0, data), device.getGatewayMacAddr(), device.getGatewayPassword(),
                                                device.getGatewaySSID(), AdminPage.this));
                                        device.setChannelMark((short)0);
                                        device.setCurrentParams(data);
                                        DatabaseManager.getInstance().updateDevice(device);
                                        deviceIterator.remove();
                                    }

                                }
                            }
                        }
                    }

                    for (Map.Entry<String, ArrayList> entry : nameset.entrySet()) {
                        ArrayList<String>  account = entry.getValue();
                        String passwords = entry.getKey();
                        if (account.get(0).equals(userName))
                        {
                            nameset.remove(passwords);
                            DataManager.getInstance().setaccount(nameset);
                            break;
                        }
                    }

                    ProgressBar progressBar = (ProgressBar)findViewById(R.id.progressBar);
                    progresslayout.setClickable(true);
                    progressBar.setVisibility(View.VISIBLE);
                    Runnable r = new Runnable() {
                        public void run() {
                            RemoveEventsUser();
                        }
                    };

                    new Thread(r).start();

                    sector.remove(userName);
                    DataManager.getInstance().setsector(sector);
                    names.remove(userName);
                    useradapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.INVISIBLE);
                    ListView deviceList = (ListView) findViewById(R.id.devicelist);
                    deviceList.setAdapter(null);
                    ListView sectorlist = (ListView) findViewById(R.id.sectorlist);
                    sectorlist.setAdapter(null);
                    GetSummary();
                }
            });
            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();
        }else if (menuItemIndex == 3)
        {
            // delete single device
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdminPage.this.getParent());
            alertDialog.setTitle("Warning");
            alertDialog.setMessage("Do you want to delete the device?");
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    HashMap<String, HashMap<String, ArrayList<Device>>> sector = DataManager.getInstance().getsector();
                    HashMap<String, ArrayList<Device>> sectioninformation = sector.get(userName);
                    ArrayList<Device> devicelist = sectioninformation.get(sectorName);
                    if (!sector.isEmpty()) {
                        Iterator<Device> deviceIterator = devicelist.iterator();
                        while (deviceIterator.hasNext()){
                            Device device = deviceIterator.next();
                            if (device.getDeviceName().equals(deviceName)) {
                                byte[] data;
                                data = new byte[]{(byte) 17, (byte) 0, (byte) 0, (byte) 0, (byte) 0};
                                DeviceSocket.getInstance().send(Message.createMessage((byte) 4, DevicePacket.createPacket((byte) 4,
                                                device.getDeviceAddress(), (short) 0, data), device.getGatewayMacAddr(), device.getGatewayPassword(),
                                        device.getGatewaySSID(), AdminPage.this));
                                device.setCurrentParams(data);
                                device.setChannelMark((short) 0);
                                DatabaseManager.getInstance().updateDevice(device);
                                deviceIterator.remove();
                            }
                        }

                        for (Map.Entry<String, HashMap<String, ArrayList<Device>>> sectorinfo : sector.entrySet()) {
                            HashMap<String, ArrayList<Device>> details = sectorinfo.getValue();
                            for (Map.Entry<String, ArrayList<Device>> singlesectorinfo : details.entrySet())
                            {
                                if (singlesectorinfo.getKey().equals(sectorName))
                                {
                                    sectioninformation = sector.get(sectorinfo.getKey());
                                    sectioninformation.put(sectorName, devicelist);
                                    sector.put(sectorinfo.getKey(), sectioninformation);
                                }
                            }
                        }
                        DataManager.getInstance().setsector(sector);
                        deviceAdptername.remove(info.position);
                        deviceadapter.notifyDataSetChanged();
                        GetSummary();
                    }
                }
            });
            alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            alertDialog.show();


        }else if (menuItemIndex == 4)
        {
            View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
            Bitmap bitmap = getScreenShot(rootView);
            DataManager.getInstance().setBitmap(bitmap);
            Intent startNewActivityIntent = new Intent(AdminPage.this, AdminAddNew.class);
            startNewActivityIntent.putExtra("Case", 6);
            startNewActivityIntent.putExtra("UserName",userName);
            ActivityAdminStack activityadminStack = (ActivityAdminStack) getParent();
            activityadminStack.push("AdminAddNew", startNewActivityIntent);
        }

        return true;
    }




    public void RemoveEvents(String belongeduser, String deletethissector){
        List<WeekViewEvent> events = DataManager.getInstance().getnewevents();
        if (events.size() != 0) {
            Iterator<WeekViewEvent> eventIterator = events.iterator();
            while (eventIterator.hasNext()) {
                WeekViewEvent event = eventIterator.next();
                ArrayList<String> sectorsname = event.getdeviceList();
                if (event.getName().equals(belongeduser)&&sectorsname.contains(deletethissector)){
                    sectorsname.remove(deletethissector);
                }
                if (sectorsname.size()==0)eventIterator.remove();
            }
        }
        DataManager.getInstance().setnewevents(events);
        List<WeekViewEvent> futureevents = DataManager.getInstance().getevents();
        if (events.size() != 0) {
            Iterator<WeekViewEvent> eventIterator = futureevents.iterator();
            while (eventIterator.hasNext()) {
                WeekViewEvent event = eventIterator.next();
                ArrayList<String> sectorsname = event.getdeviceList();
                if (event.getName().equals(belongeduser) && sectorsname.contains(deletethissector)){
                    sectorsname.remove(deletethissector);
                }
                if (sectorsname.size()==0)eventIterator.remove();
            }
        }
        DataManager.getInstance().setevents(futureevents);


    }

    public void RemoveEventsUser() {
        Gateway gateway = SysApplication.getInstance().getCurrGateway(AdminPage.this);
        if (gateway!=null) {
            List<WeekViewEvent> events = DataManager.getInstance().getnewevents();
            if (events.size() != 0 && events!=null) {
                Iterator<WeekViewEvent> eventIterator = events.iterator();
                while (eventIterator.hasNext()) {
                    WeekViewEvent event = eventIterator.next();
                    if (event.getName().equals(userName))eventIterator.remove();
                }
            }
            DataManager.getInstance().setnewevents(events);

            List<WeekViewEvent> comingevents = DataManager.getInstance().getevents();
            if (comingevents.size() != 0 & comingevents!=null) {
                Iterator<WeekViewEvent> eventIterator = comingevents.iterator();
                while (eventIterator.hasNext()) {
                    WeekViewEvent event = eventIterator.next();
                    if (event.getName().equals(userName))eventIterator.remove();
                }
            }
            DataManager.getInstance().setevents(comingevents);
        }
    }
    public static Bitmap dataupdate(String filename) {
        File root = Environment.getExternalStorageDirectory();
        File dir = new File(root.getAbsolutePath() + "/Horizon/Bitmap");
        File file = new File(dir, filename);
        if (file.exists()) {
            try {
                FileInputStream streamIn = new FileInputStream(file);
                Bitmap bitmap = BitmapFactory.decodeStream(streamIn);
                return bitmap;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }
*/
    public void GetSummary(){

        ArrayList<String> usernumberlist = DatabaseManager.getInstance().getUserNameList();
        ArrayList<String> sectornumberlist = DatabaseManager.getInstance().getSectorList();
        ArrayList<String> devicenumberlist = DatabaseManager.getInstance().getDeviceList();
        inforsumuser.setText("Users number: " + usernumberlist.size());
        inforsumsector.setText("Sectors number: " + sectornumberlist.size());
        inforsumdevice.setText("Devices number: " + devicenumberlist.size());
    }
    public static Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache(),0,100,750,
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


