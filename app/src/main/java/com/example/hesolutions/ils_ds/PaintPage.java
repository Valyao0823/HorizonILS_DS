package com.example.hesolutions.ils_ds;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PaintPage extends Activity{
    private float smallBrush, mediumBrush, largeBrush;
    private ImageButton currPaint, drawBtn, eraseBtn, newBtn, saveBtn, loadBtn;
    private DrawingView drawView;
    private LinearLayout drawingpart;
    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
    String sectorsave = "";
    Handler myHandler;
    Runnable myRunnable;
    int Selected_Device = -1;
    ArrayList<String> sectorlist = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint_page);
        drawBtn = (ImageButton)findViewById(R.id.draw_btn);
        drawView = (DrawingView)findViewById(R.id.drawing);
        drawingpart = (LinearLayout)findViewById(R.id.drawingpart);
        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        myHandler = new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(PaintPage.this, ScreenSaver.class);
                myHandler.removeCallbacks(myRunnable);
                startActivity(intent);
            }
        };
        myHandler.postDelayed(myRunnable, 3 * 60 * 1000);

        eraseBtn = (ImageButton)findViewById(R.id.erase_btn);
        eraseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog brushDialog = new Dialog(PaintPage.this);
                brushDialog.setTitle("Eraser size:");
                brushDialog.setContentView(R.layout.brush_chooser);
                ImageButton smallBtn = (ImageButton)brushDialog.findViewById(R.id.small_brush);
                smallBtn.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(smallBrush);
                        brushDialog.dismiss();
                    }
                });
                ImageButton mediumBtn = (ImageButton)brushDialog.findViewById(R.id.medium_brush);
                mediumBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(mediumBrush);
                        brushDialog.dismiss();
                    }
                });
                ImageButton largeBtn = (ImageButton)brushDialog.findViewById(R.id.large_brush);
                largeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setErase(true);
                        drawView.setBrushSize(largeBrush);
                        brushDialog.dismiss();
                    }
                });
                brushDialog.show();
            }
        });

        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        currPaint = (ImageButton)paintLayout.getChildAt(0);
        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
        drawBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog brushDialog = new Dialog(PaintPage.this);
                brushDialog.setTitle("Brush size:");
                brushDialog.setContentView(R.layout.brush_chooser);
                ImageButton smallBtn = (ImageButton) brushDialog.findViewById(R.id.small_brush);
                smallBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(smallBrush);
                        drawView.setLastBrushSize(smallBrush);
                        drawView.setErase(false);
                        brushDialog.dismiss();
                    }
                });
                ImageButton mediumBtn = (ImageButton) brushDialog.findViewById(R.id.medium_brush);
                mediumBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(mediumBrush);
                        drawView.setLastBrushSize(mediumBrush);
                        drawView.setErase(false);
                        brushDialog.dismiss();
                    }
                });

                ImageButton largeBtn = (ImageButton) brushDialog.findViewById(R.id.large_brush);
                largeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        drawView.setBrushSize(largeBrush);
                        drawView.setLastBrushSize(largeBrush);
                        drawView.setErase(false);
                        brushDialog.dismiss();
                    }
                });
                brushDialog.show();
                drawView.setBrushSize(mediumBrush);
            }
        });

        newBtn = (ImageButton)findViewById(R.id.new_btn);
        newBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder newDialog = new AlertDialog.Builder(PaintPage.this);
                newDialog.setTitle("New drawing");
                newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
                newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        drawView.startNew();
                        dialog.dismiss();
                    }
                });
                newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                newDialog.show();
            }
        });

        saveBtn = (ImageButton)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawView.getBackground()!=null) {
                    AlertDialog.Builder saveDialog = new AlertDialog.Builder(PaintPage.this);
                    saveDialog.setTitle("Warning");
                    saveDialog.setMessage("Do you want to save the layout, which cannot be changed in the future.");
                    saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            drawView.setDrawingCacheEnabled(true);
                            if (!sectorsave.equals("")) {
                                writedata(drawView.getDrawingCache(), sectorsave + ".png");
                                Toast savedToast = Toast.makeText(getApplicationContext(),
                                        "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                                savedToast.show();
                                drawView.destroyDrawingCache();
                            }

                        }
                    });
                    saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    saveDialog.show();
                }else {
                    AlertDialog.Builder saveDialog = new AlertDialog.Builder(PaintPage.this);
                    saveDialog.setTitle("Warning");
                    saveDialog.setMessage("Please load the blueprint first.");
                    saveDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    saveDialog.show();
                }

            }
        });

        loadBtn = (ImageButton)findViewById(R.id.load_btn);
        loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
// Start the Intent
                startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
            }
        });


        ImageButton penmode = (ImageButton)findViewById(R.id.pen_mode);
        penmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setErase(false);
                drawView.setMode(1);
            }
        });
        ImageButton recmode = (ImageButton)findViewById(R.id.rec_mode);
        recmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setErase(false);
                drawView.setMode(2);
            }
        });
        ImageButton cirmode = (ImageButton)findViewById(R.id.cir_mode);
        cirmode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawView.setErase(false);
                drawView.setMode(3);
            }
        });

        LoadList();
    }
    public void paintClicked(View view){
        //use chosen color
        drawView.setErase(false);
        drawView.setBrushSize(drawView.getLastBrushSize());
        if(view!=currPaint){
//update color
            ImageButton imgView = (ImageButton)view;
            String color = view.getTag().toString();
            drawView.setColor(color);
            imgView.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.paint));
            currPaint=(ImageButton)view;

        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();
                // Set the Image in ImageView after decoding the String
                drawView.setImageBitmap(BitmapFactory
                        .decodeFile(imgDecodableString));
                drawView.setEnabletouch(true);

            } else {
                Toast.makeText(this, "You haven't picked the blueprint",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }
    public static void writedata(Bitmap bitmap, String filename) {
        String state;
        state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {

            File root = Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/Horizon/Bitmap");
            if (!dir.exists()) {
                dir.mkdir();
            }
            File file = new File(dir, filename);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void LoadList()
    {
        sectorlist = DatabaseManager.getInstance().getSectorList();
        ListView sectorlistlayout = (ListView)findViewById(R.id.sectorlistlayout);
        TextView blank = (TextView)findViewById(R.id.textView13);
        if (!sectorlist.isEmpty()) {
            blank.setVisibility(View.GONE);
            sectorlistlayout.setVisibility(View.VISIBLE);
            final MyAdapter adapter = new MyAdapter(this, sectorlist);
            sectorlistlayout.setAdapter(adapter);
            sectorlistlayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Bitmap bitmap = null;
                    bitmap = dataupdate(sectorlist.get(position)+".png");
                    if (bitmap!=null)
                    {
                        Drawable d = new BitmapDrawable(getResources(), bitmap);
                        drawingpart.setVisibility(View.VISIBLE);
                        drawView.setBackground(d);
                        drawView.setEnabletouch(false);
                        LinearLayout toolpanel = (LinearLayout)findViewById(R.id.toolpanel);
                        toolpanel.setVisibility(View.GONE);
                    }else
                    {
                        showContent(view);
                        if (drawView.getBackground()!=null)
                        {
                            drawView.setEnabletouch(true);
                        }else{
                            drawView.setEnabletouch(false);
                        }
                        LinearLayout toolpanel = (LinearLayout)findViewById(R.id.toolpanel);
                        toolpanel.setVisibility(View.VISIBLE);
                    }
                    Selected_Device = position;
                    adapter.notifyDataSetChanged();
                }
            });
        }else {
            blank.setVisibility(View.VISIBLE);
            sectorlistlayout.setVisibility(View.GONE);
        }
    }

    public class MyAdapter extends ArrayAdapter<String> {

        private Activity context;
        private ArrayList<String> devicelist;

        public MyAdapter(Activity context, ArrayList<String> zoneList) {
            super(context, R.layout.devicelistadmin, zoneList);
            this.context = context;
            this.devicelist = zoneList;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView = inflater.inflate(R.layout.devicelistadmin, null);
            TextView txtTitle = (TextView) rowView.findViewById(R.id.textView);
            txtTitle.setText(devicelist.get(position));

            if (position == Selected_Device) {
                txtTitle.setBackground(getResources().getDrawable(R.drawable.buttonclicked));
            } else {
                txtTitle.setBackground(getResources().getDrawable(R.drawable.buttonunclick));
            }

            return rowView;
        }

    }
    public void showContent(View view)
    {
        sectorsave = ((TextView) view).getText().toString();
        drawingpart.setVisibility(View.VISIBLE);
        drawView.startNew();
        drawView.setEnabletouch(false);
        drawView.setBackground(null);
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
