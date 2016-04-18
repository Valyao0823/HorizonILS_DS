package com.example.hesolutions.ils_ds;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.Switch;

/**
 * Created by hesolutions on 2016-04-18.
 */
public class EnhancedSwitch extends Switch implements ProgrammaticallyCheckable {
    private CompoundButton.OnCheckedChangeListener mListener = null;

    public EnhancedSwitch(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public EnhancedSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EnhancedSwitch(Context context) {
        super(context);
    }

    @Override
    public void setOnCheckedChangeListener(
            CompoundButton.OnCheckedChangeListener listener){
        if(this.mListener == null) {this.mListener = listener;}
        super.setOnCheckedChangeListener(listener);
    }

    @Override
    public void setCheckedProgrammatically(boolean checked){
        super.setOnCheckedChangeListener(null);
        super.setChecked(checked);
        super.setOnCheckedChangeListener(mListener);
    }

}
