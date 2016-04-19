package com.example.hesolutions.ils_ds;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.collect.BiMap;
import com.zxing.activity.CaptureActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AdminPage extends Activity {

    ArrayList<String> usernamelist, sectornamelist, devicenamelist;
    TextView inforsumuser,inforsumsector,inforsumdevice;
    Button adduser, addsector, adddevice;
    RelativeLayout userlistlayout, sectorlistlayout, devicelistlayout, infor;
    String userName = "";
    String sectorName = "";
    String deviceName = "";
    UserCustomListAdapter useradapter;
    SectorCustomListAdapter sectoradapter;
    DeviceCustomListAdapter deviceadapter;
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
                            startNewActivityIntent.putExtra("Case", 6);
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
        sectornamelist = DatabaseManager.getInstance().showSectorforuser(userName);
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
        devicenamelist = DatabaseManager.getInstance().showDeviceforsector(sectorName);
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



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId()==R.id.sectorlist) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            sectorName = sectornamelist.get(info.position);
            menu.setHeaderTitle(sectorName);
            menu.add(0, 0, 0, "Share");
            menu.add(0, 1, 0, "Remove");
        }
        if (v.getId() == R.id.userlist) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            userName = usernamelist.get(info.position);
            menu.setHeaderTitle(userName);
            menu.add(0, 2, 0, "Delete");
            menu.add(0, 4, 0, "Change Password");
        }
        if (v.getId() == R.id.devicelist)
        {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            deviceName = devicenamelist.get(info.position);
            menu.setHeaderTitle(deviceName);
            menu.add(0, 3, 0, "Delete");
        }
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        if (menuItemIndex == 1)
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdminPage.this.getParent());
            alertDialog.setTitle("Warning");
            alertDialog.setMessage("Do you want to remove the sector?");
            alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (userName!=null && sectorName!=null) DatabaseManager.getInstance().removeSectorforuser(userName,sectorName);
                    ArrayList<String> showSectors = DatabaseManager.getInstance().showSectors();
                    if (!showSectors.contains(sectorName)){
                        DatabaseManager.getInstance().removeSector(sectorName);
                        File root = Environment.getExternalStorageDirectory();
                        File dir = new File(root.getAbsolutePath() + "/Horizon/Bitmap");
                        File file = new File(dir, sectorName+".png");
                        if (file.exists())file.delete();
                    }

                    ListView deviceList = (ListView) findViewById(R.id.devicelist);
                    deviceList.setAdapter(null);
                    sectornamelist.remove(info.position);
                    sectoradapter.notifyDataSetChanged();
                    adddevice.setVisibility(View.INVISIBLE);
                    GetSummary();
                    dialog.dismiss();
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
            ArrayList<String> allusernames = DatabaseManager.getInstance().getUserNameList();
            allusernames.remove(userName);
            if (!allusernames.isEmpty()) {
                View rootView = getWindow().getDecorView().findViewById(android.R.id.content);
                Bitmap bitmap = getScreenShot(rootView);
                DataManager.getInstance().setBitmap(bitmap);
                Intent startNewActivityIntent = new Intent(AdminPage.this, AdminAddNew.class);
                startNewActivityIntent.putExtra("Case", 3);
                startNewActivityIntent.putExtra("userName", userName);
                startNewActivityIntent.putExtra("sectorName", sectornamelist.get(info.position));
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
                    ArrayList<String> sectors = DatabaseManager.getInstance().showSectorforuser(userName);
                    if (userName!=null)DatabaseManager.getInstance().removeUserfromMapping(userName);
                    ArrayList<String> showSectors = DatabaseManager.getInstance().showSectors();
                    for(String sector:sectors) {
                        if (!showSectors.contains(sector)){
                            DatabaseManager.getInstance().removeSector(sector);
                            File root = Environment.getExternalStorageDirectory();
                            File dir = new File(root.getAbsolutePath() + "/Horizon/Bitmap");
                            File file = new File(dir, sector+".png");
                            if (file.exists())file.delete();
                        }
                    }
                    DatabaseManager.getInstance().removeEventforuser(userName);
                    DatabaseManager.getInstance().removeUser(userName);
                    usernamelist.remove(userName);
                    useradapter.notifyDataSetChanged();
                    ListView deviceList = (ListView) findViewById(R.id.devicelist);
                    deviceList.setAdapter(null);
                    ListView sectorlist = (ListView) findViewById(R.id.sectorlist);
                    sectorlist.setAdapter(null);
                    GetSummary();
                    dialog.dismiss();
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
                    if (deviceName!=null && sectorName!=null) DatabaseManager.getInstance().updateDevicefromSector(deviceName,null);
                    devicenamelist.remove(info.position);
                    deviceadapter.notifyDataSetChanged();
                    GetSummary();
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
            startNewActivityIntent.putExtra("Case", 4);
            startNewActivityIntent.putExtra("UserName",userName);
            ActivityAdminStack activityadminStack = (ActivityAdminStack) getParent();
            activityadminStack.push("AdminAddNew", startNewActivityIntent);
        }

        return true;
    }

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


