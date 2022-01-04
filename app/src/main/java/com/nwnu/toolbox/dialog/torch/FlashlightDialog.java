package com.nwnu.toolbox.dialog.torch;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.nwnu.toolbox.R;
import com.sdsmdg.harjot.crollerTest.Croller;
import com.warkiz.widget.IndicatorSeekBar;

public class FlashlightDialog extends Dialog {

    private Button cancel;
    private Button start;
    private Croller frequency;

    private onCancelClickedListener cancelListener;
    private onStartClickedListener startListener;



    public FlashlightDialog(Context context) {
        super(context, R.style.dialog_custom);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.torch_flashlight_dialog_layout);
        setCanceledOnTouchOutside(false);

        initView();
        initEvent();
    }

    private void initView() {
        cancel = (Button) findViewById(R.id.flashlight_dialog_layout_cancel);
        start = (Button) findViewById(R.id.flashlight_dialog_layout_start);
        frequency = (Croller) findViewById(R.id.flashlight_dialog_layout_seek_bar);
    }

    private void initEvent() {
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (cancelListener != null) {
                    cancelListener.onClick();
                }
            }
        });

        start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (startListener != null) {
                    startListener.onClick(frequency.getProgress());
                }
            }
        });
    }

    public void setOnCancelClickedListener(onCancelClickedListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public void setOnStartClickedListener(onStartClickedListener startListener) {
        this.startListener = startListener;
    }

    public interface onCancelClickedListener {
        public void onClick();
    }

    public interface onStartClickedListener {
        public void onClick(int fre);
    }
}
