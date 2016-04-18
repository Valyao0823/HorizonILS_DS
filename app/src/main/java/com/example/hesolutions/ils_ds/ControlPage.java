package com.example.hesolutions.ils_ds;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class ControlPage extends Activity {
    String username = DataManager.getInstance().getusername();
    EnhancedSeekBar seekBar, seekBarSector;
    int intensity, sectorintensity;
    ImageView imageViewroomlayout;
    ExpandListAdapter adapter;
    TextView Intensity, ownertag, owner, sectortag, sectornameT, devicetag, devicenameT, Intensitynum, IntensitySector, IntensitynumSector;
    Handler myHandler, rthandler;
    Runnable myRunnable;
    AlertDialog controlalertdialog;
    AlertDialog.Builder controlbuilder = null;
    Timer rtstatus;
    String choosesector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_page);
        seekBar = (EnhancedSeekBar)findViewById(R.id.seekBar);
        seekBarSector = (EnhancedSeekBar)findViewById(R.id.seekBarSector);
        imageViewroomlayout = (ImageView)findViewById(R.id.imageViewroomlayout);
        ownertag = (TextView)findViewById(R.id.ownertag);
        owner = (TextView)findViewById(R.id.owner);
        sectortag = (TextView)findViewById(R.id.sectortag);
        sectornameT = (TextView)findViewById(R.id.sectornameT);
        devicetag = (TextView)findViewById(R.id.devicetag);
        devicenameT = (TextView)findViewById(R.id.devicenameT);
        Intensity = (TextView)findViewById(R.id.Intensity);
        Intensitynum = (TextView)findViewById(R.id.Intensitynum);
        IntensitySector = (TextView)findViewById(R.id.IntensitySector);
        IntensitynumSector = (TextView)findViewById(R.id.IntensitynumSector);

        ArrayList<String> sectorArray = DatabaseManager.getInstance().showSectorforuser(username);
        if (sectorArray!=null && !sectorArray.isEmpty()) {
            adapter = new ExpandListAdapter(this, sectorArray);
            ExpandableListView sectorListView = (ExpandableListView) findViewById(R.id.sectorListViewId);
            sectorListView.setAdapter(adapter);
        }
        owner.setText(username);

        myHandler = new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ControlPage.this, ScreenSaver.class);
                myHandler.removeCallbacks(myRunnable);
                startActivity(intent);
            }
        };
        myHandler.postDelayed(myRunnable, 3*60*1000);

        rtstatus = new Timer();
        rthandler = new Handler();
        rtstatus.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                rthandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (adapter!=null) {
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }, Calendar.getInstance().getTime(), 5 * 1000);

    }

    public class ExpandListAdapter extends BaseExpandableListAdapter {

        private Context context;
        private ArrayList<String> groups;

        public ExpandListAdapter(Context context, ArrayList<String> groups) {
            this.context = context;
            this.groups = groups;
        }

        @Override
        public String getChild(int groupPosition, int childPosition) {
            ArrayList<String> devicenamelist = DatabaseManager.getInstance().showDeviceforsector(getGroup(groupPosition));
            return devicenamelist.get(childPosition);
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {

            final String devicename = getChild(groupPosition, childPosition);
            if (convertView == null) {
                LayoutInflater inf = (LayoutInflater) context
                        .getSystemService(context.LAYOUT_INFLATER_SERVICE);
                convertView = inf.inflate(R.layout.deviceswitch, null);
            }
            TextView tv = (TextView) convertView.findViewById(R.id.textView);
            final EnhancedSwitch switchid = (EnhancedSwitch)convertView.findViewById(R.id.switchid);
            tv.setText(devicename);

            if (DatabaseManager.getInstance().getDeviceIntensity(devicename)!=0)
            {
                switchid.setCheckedProgrammatically(true);
            } else
            {
                switchid.setCheckedProgrammatically(false);
            }

            if (DatabaseManager.getInstance().getDeviceControl(devicename) == 1)
            {
                switchid.setTrackDrawable(getResources().getDrawable(R.drawable.locksector));
            } else
            {
                switchid.setTrackDrawable(getResources().getDrawable(R.drawable.togglebgpress));
            }


            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intensity = DatabaseManager.getInstance().getDeviceIntensity(devicename);
                    seekBar.setVisibility(View.VISIBLE);
                    Intensitynum.setVisibility(View.VISIBLE);
                    Intensity.setVisibility(View.VISIBLE);
                    seekBarSector.setVisibility(View.INVISIBLE);
                    IntensitynumSector.setVisibility(View.INVISIBLE);
                    IntensitySector.setVisibility(View.INVISIBLE);
                    if (switchid.isChecked() == true) {
                        seekBar.setProgressProgrammatically(intensity);
                        Intensitynum.setText(Integer.toString(intensity) + "%");
                    } else {
                        seekBar.setProgressProgrammatically(0);
                        Intensitynum.setText("0%");
                    }
                    devicetag.setVisibility(View.VISIBLE);
                    devicenameT.setVisibility(View.VISIBLE);
                    devicenameT.setText(((TextView) v).getText().toString());
                }
            });

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int progressfinal;

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progressfinal = progress;
                    Intensitynum.setText(Integer.toString(progress) + "%");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    seekBar.setEnabled(true);
                    Intensitynum.setText(Integer.toString(progressfinal) + "%");
                    final byte[] SetParams = new byte[5];
                    if (progressfinal == 0) {
                        SetParams[0] = (byte) 17;
                        SetParams[1] = (byte) 0;
                        SetParams[2] = (byte) 0;
                        SetParams[3] = (byte) 0;
                        SetParams[4] = (byte) 0;
                    } else {
                        SetParams[0] = (byte) 17;
                        SetParams[1] = (byte) progressfinal;
                        SetParams[2] = (byte) 0;
                        SetParams[3] = (byte) 0;
                        SetParams[4] = (byte) 0;
                    }

                    if (progressfinal == 0) {
                        if (controlalertdialog != null && controlalertdialog.isShowing()) {
                        } else {
                            controlbuilder = new AlertDialog.Builder(ControlPage.this.getParent());
                            controlbuilder.setTitle("Warning");
                            controlbuilder.setMessage("Do you want to disable the manual control? \n(Note: Disabling manual control will make device fall under preset schedule)");
                            controlbuilder.setCancelable(false);
                            controlbuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // contorl = 0 , intensity = 0

                                    adapter.notifyDataSetChanged();
                                }
                            });
                            controlbuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // control = 1, intensity = 0

                                    adapter.notifyDataSetChanged();
                                }
                            });
                            controlalertdialog = controlbuilder.create();
                            controlalertdialog.show();
                        }
                    } else {
                        // control = 1, intensity = progress value

                        adapter.notifyDataSetChanged();
                    }

                }
            });


            switchid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    seekBar.setVisibility(View.INVISIBLE);
                    Intensitynum.setVisibility(View.INVISIBLE);
                    Intensity.setVisibility(View.INVISIBLE);
                    seekBarSector.setVisibility(View.INVISIBLE);
                    IntensitynumSector.setVisibility(View.INVISIBLE);
                    IntensitySector.setVisibility(View.INVISIBLE);
                    if (switchid.isChecked() == true) {
                        // control = 1 , intensity = 100;

                        adapter.notifyDataSetChanged();
                    } else {
                        if (controlalertdialog != null && controlalertdialog.isShowing()) {
                        } else {
                            controlbuilder = new AlertDialog.Builder(ControlPage.this.getParent());
                            controlbuilder.setTitle("Warning");
                            controlbuilder.setMessage("Do you want to disable the manual control? \n(Note: Disabling manual control will make device fall under preset schedule)");
                            controlbuilder.setCancelable(false);
                            controlbuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // control = 0, intensity = 0
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            controlbuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // control = 1, intensity = 0;
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            controlalertdialog = controlbuilder.create();
                            controlalertdialog.show();
                        }
                    }

                }
            });

            return convertView;
        }
        @Override
        public int getChildrenCount(int groupPosition) {
            ArrayList<String> devicelist = DatabaseManager.getInstance().showDeviceforsector(groups.get(groupPosition));
            if (devicelist!=null) {return devicelist.size();}
            else{return 0;}
        }

        @Override
        public String getGroup(int groupPosition) {
            return groups.get(groupPosition);
        }

        @Override
        public int getGroupCount() {
            return groups.size();
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public View getGroupView(final int groupPosition, final boolean isExpanded,
                                 View convertView, final ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inf = (LayoutInflater) context
                        .getSystemService(context.LAYOUT_INFLATER_SERVICE);
                convertView = inf.inflate(R.layout.sectorswitch, null);
            }

            TextView txtTitle = (TextView) convertView.findViewById(R.id.textView);
            final EnhancedSwitch switchid = (EnhancedSwitch)convertView.findViewById(R.id.switchid);
            final String sectorname = getGroup(groupPosition);
            txtTitle.setText(sectorname);
            final ArrayList<String> devicelist = DatabaseManager.getInstance().showDeviceforsector(sectorname);
            if (devicelist!=null && !devicelist.isEmpty()) {
                for (String devicename:devicelist) {
                    if (DatabaseManager.getInstance().getDeviceIntensity(devicename)!=0)
                    {
                        switchid.setCheckedProgrammatically(true);
                        sectorintensity = DatabaseManager.getInstance().getDeviceIntensity(devicename);
                        break;
                    } else {
                        switchid.setCheckedProgrammatically(false);
                    }
                }
            }else
            {
                switchid.setCheckedProgrammatically(false);
            }

            txtTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    seekBarSector.setVisibility(View.VISIBLE);
                    IntensitynumSector.setVisibility(View.VISIBLE);
                    IntensitySector.setVisibility(View.VISIBLE);
                    seekBar.setVisibility(View.INVISIBLE);
                    Intensitynum.setVisibility(View.INVISIBLE);
                    Intensity.setVisibility(View.INVISIBLE);
                    choosesector = ((TextView)v).getText().toString();
                    if (switchid.isChecked() == true) {
                        seekBarSector.setProgressProgrammatically(sectorintensity);
                        IntensitynumSector.setText(Integer.toString(sectorintensity) + "%");
                    } else {
                        seekBarSector.setProgressProgrammatically(0);
                        IntensitynumSector.setText("0%");
                    }


                    if (isExpanded) ((ExpandableListView) parent).collapseGroup(groupPosition);
                    else ((ExpandableListView) parent).expandGroup(groupPosition, true);
                    Bitmap bitmap = dataupdate(sectorname + ".png");
                    if (bitmap != null) {
                        Drawable d = new BitmapDrawable(getResources(), bitmap);
                        imageViewroomlayout.setBackground(d);
                    } else {
                        imageViewroomlayout.setBackground(null);
                    }
                    sectortag.setVisibility(View.VISIBLE);
                    sectornameT.setVisibility(View.VISIBLE);
                    sectornameT.setText(((TextView) v).getText().toString());
                    devicetag.setVisibility(View.INVISIBLE);
                    devicenameT.setVisibility(View.INVISIBLE);
                }
            });

            seekBarSector.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                int progressfinal;

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    progressfinal = progress;
                    Intensitynum.setText(Integer.toString(progress) + "%");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    seekBar.setEnabled(true);
                    IntensitynumSector.setText(Integer.toString(progressfinal) + "%");
                    final byte[] SetParams = new byte[5];
                    if (progressfinal == 0) {
                        SetParams[0] = (byte) 17;
                        SetParams[1] = (byte) 0;
                        SetParams[2] = (byte) 0;
                        SetParams[3] = (byte) 0;
                        SetParams[4] = (byte) 0;
                    } else {
                        SetParams[0] = (byte) 17;
                        SetParams[1] = (byte) progressfinal;
                        SetParams[2] = (byte) 0;
                        SetParams[3] = (byte) 0;
                        SetParams[4] = (byte) 0;
                    }

                    if (progressfinal == 0) {
                        if (controlalertdialog != null && controlalertdialog.isShowing()) {
                        } else {
                            controlbuilder = new AlertDialog.Builder(ControlPage.this.getParent());
                            controlbuilder.setTitle("Warning");
                            controlbuilder.setMessage("Do you want to disable the manual control? \n(Note: Disabling manual control will make device fall under preset schedule)");
                            controlbuilder.setCancelable(false);
                            controlbuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Control = 0, intensity = 0;

                                }
                            });
                            controlbuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // Control = 1, intensity =0

                                }
                            });
                            controlalertdialog = controlbuilder.create();
                            controlalertdialog.show();
                        }
                    } else {
                        // Control = 1, intensity = progress value

                    }
                }
            });

            switchid.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    seekBar.setVisibility(View.INVISIBLE);
                    Intensitynum.setVisibility(View.INVISIBLE);
                    Intensity.setVisibility(View.INVISIBLE);
                    seekBarSector.setVisibility(View.INVISIBLE);
                    IntensitynumSector.setVisibility(View.INVISIBLE);
                    IntensitySector.setVisibility(View.INVISIBLE);
                    if (!sectorname.equals(" ")) {
                        if (devicelist != null && !devicelist.isEmpty()) {
                            if (switchid.isChecked() == true) {
                                // control = 1, intensity = 100;

                            } else {
                                if (controlalertdialog != null && controlalertdialog.isShowing()) {
                                } else {
                                    controlbuilder = new AlertDialog.Builder(ControlPage.this.getParent());
                                    controlbuilder.setTitle("Warning");
                                    controlbuilder.setMessage("Do you want to disable the manual control? \n(Note: Disabling manual control will make device fall under preset schedule)");
                                    controlbuilder.setCancelable(false);
                                    controlbuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // control = 0, intensity = 0;
                                        }
                                    });
                                    controlbuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // control = 1, intensity = 0;

                                        }
                                    });
                                    controlalertdialog = controlbuilder.create();
                                    controlalertdialog.show();
                                }
                            }
                        } else {
                            switchid.setCheckedProgrammatically(false);
                        }
                    }

                }
            });

            return convertView;

        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
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
        myHandler.postDelayed(myRunnable, 3 * 60 * 1000);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        rtstatus.cancel();
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
        rtstatus.cancel();
        myHandler.removeCallbacks(myRunnable);
    }
}
