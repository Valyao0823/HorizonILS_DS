package com.example.hesolutions.ils_ds;

/**
 * Created by hesolutions on 2016-04-18.
 */
import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
 * Created by hesolutions on 2016-02-24.
 */
public class EnhancedSeekBar extends SeekBar implements ProgrammaticallyProgress {
    private SeekBar.OnSeekBarChangeListener mListener = null;

    public EnhancedSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public EnhancedSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EnhancedSeekBar(Context context) {
        super(context);
    }

    @Override
    public void setOnSeekBarChangeListener(
            SeekBar.OnSeekBarChangeListener listener){
        if(this.mListener == null) {this.mListener = listener;}
        super.setOnSeekBarChangeListener(listener);
    }

    @Override
    public void setProgressProgrammatically(int progress){
        super.setOnSeekBarChangeListener(null);
        super.setProgress(progress);
        super.setOnSeekBarChangeListener(mListener);
    }
}
