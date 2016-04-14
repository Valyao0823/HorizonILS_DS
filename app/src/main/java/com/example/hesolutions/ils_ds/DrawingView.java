package com.example.hesolutions.ils_ds;

/**
 * Created by hesolutions on 2016-04-14.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;


public class DrawingView extends View {
    //drawing path
    private Path drawPath;
    //drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xFF660000;
    //canvas
    private Canvas drawCanvas;
    //canvas bitmap
    private Bitmap canvasBitmap;
    private float brushSize, lastBrushSize;
    private boolean erase=false;
    private int modeint = 1;

    private float startX = 0F;
    private float startY = 0F;

    private boolean enabletouch = false;
    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();

    }
    private void setupDrawing(){
//get drawing area setup for interaction
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAlpha(125);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
        brushSize = getResources().getInteger(R.integer.medium_size);
        lastBrushSize = brushSize;
        drawPaint.setStrokeWidth(brushSize);
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//view given size
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);

    }
    @Override
    protected void onDraw(Canvas canvas) {
//draw view
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
//detect user touch
        if (enabletouch == true) {
            float touchX = event.getX();
            float touchY = event.getY();
            switch (modeint) {
                case 1:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            drawPath.moveTo(touchX, touchY);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            drawPaint.setStyle(Paint.Style.STROKE);
                            drawPath.lineTo(touchX, touchY);
                            break;
                        case MotionEvent.ACTION_UP:
                            drawCanvas.drawPath(drawPath, drawPaint);
                            drawPath.reset();
                            break;
                        default:
                            return false;
                    }
                    break;
                case 2:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            this.startX = event.getX();
                            this.startY = event.getY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            drawPath.reset();
                            drawPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                            drawPath.addRect(startX, startY, touchX, touchY, Path.Direction.CCW);
                            break;
                        case MotionEvent.ACTION_UP:
                            drawCanvas.drawPath(drawPath, drawPaint);
                            drawPath.reset();
                            break;
                        default:
                            return false;
                    }
                    break;
                case 3:
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            this.startX = event.getX();
                            this.startY = event.getY();
                            break;
                        case MotionEvent.ACTION_MOVE:
                            double distanceX = Math.abs((double) (startX - touchX));
                            double distanceY = Math.abs((double) (startY - touchY));
                            double radius = Math.sqrt(Math.pow(distanceX, 2.0) + Math.pow(distanceY, 2.0));
                            drawPath.reset();
                            drawPaint.setStyle(Paint.Style.FILL_AND_STROKE);
                            drawPath.addCircle(startX, startY, (float) radius, Path.Direction.CCW);
                            break;
                        case MotionEvent.ACTION_UP:
                            drawCanvas.drawPath(drawPath, drawPaint);
                            drawPath.reset();
                            break;
                        default:
                            return false;
                    }
                    break;
            }
            invalidate();
            return true;
        }
        else
        {
            return true;
        }
    }
    public void setColor(String newColor){
//set color
        invalidate();
        paintColor = Color.parseColor(newColor);
        drawPaint.setColor(paintColor);
        drawPaint.setAlpha(125);
    }

    public void setBrushSize(float newSize){
//update size
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                newSize, getResources().getDisplayMetrics());
        brushSize=pixelAmount;
        drawPaint.setStrokeWidth(brushSize);

    }

    public void setLastBrushSize(float lastSize){
        lastBrushSize=lastSize;
    }
    public float getLastBrushSize(){
        return lastBrushSize;
    }
    public void setErase(boolean isErase){
//set erase true or false
        erase=isErase;
        if(erase) drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else drawPaint.setXfermode(null);

    }
    public void startNew(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    public void setImageBitmap(Bitmap bitmap)
    {
        Drawable d = new BitmapDrawable(getResources(), bitmap);
        setBackground(d);
    }

    public void setMode(Integer getmode)
    {
        switch (getmode)
        {
            case 1:
                modeint = 1;
                break;
            case 2:
                modeint = 2;
                break;
            case 3:
                modeint = 3;
                break;
        }
    }

    public void setEnabletouch(boolean eachcase)
    {
        this.enabletouch = eachcase;
    }

}
