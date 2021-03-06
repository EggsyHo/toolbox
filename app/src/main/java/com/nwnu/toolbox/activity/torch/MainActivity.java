package com.nwnu.toolbox.activity.torch;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.nwnu.toolbox.R;
import com.nwnu.toolbox.activity.base.BaseActivity;
import com.nwnu.toolbox.dialog.torch.FlashlightDialog;
import com.nwnu.toolbox.util.torch.TorchUtils;

public class MainActivity extends Activity implements View.OnClickListener{
    private TextView aboutText;
    private ImageButton torchBtn;
    private Context mContext;
    private LinearLayout frontFlashLight;
    private CheckBox checkbox;
    private Switch flashlightSwitch;
    private Button backBtn;
    private TextView title;

    private FlashlightDialog flashlightDialog;

    private Thread flashLoopThread;

    private boolean isFrontFlashlight = false;
    private boolean isFlashLoop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.torch_main);

        mContext = this;
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 1);
        }

        aboutText = (TextView) findViewById(R.id.main_text);
        torchBtn = (ImageButton) findViewById(R.id.torch_btn);
        frontFlashLight = (LinearLayout) findViewById(R.id.front_flash_light);
        checkbox = (CheckBox) findViewById(R.id.mainCheckBox);
        flashlightSwitch = (Switch) findViewById(R.id.flashlight_switch);
        backBtn = (Button) findViewById(R.id.title_layout_back_button);
        title = (TextView) findViewById(R.id.title_layout_title_text);

        title.setText("?????????");
        backBtn.setOnClickListener(this);

        // ???????????????????????????
        if (TorchUtils.frontFlashLightId() == -1) {
            frontFlashLight.setVisibility(View.INVISIBLE);
            isFrontFlashlight = false;
        }

        aboutText.setVisibility(View.GONE);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Toast.makeText(this, "???????????????????????????!\n  ????????????????????????!", Toast.LENGTH_SHORT).show();
            torchBtn.setVisibility(View.INVISIBLE);
            flashlightSwitch.setVisibility(View.INVISIBLE);
            screenLight();
        } else {
            torchBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TorchUtils.IS_TORCH_ON) {
                        TorchUtils.openFlash(mContext, checkbox.isChecked());

                        torchBtn.setBackgroundResource(R.drawable.button_on);
                        flashlightSwitch.setEnabled(false);
                    } else {
                        TorchUtils.closeFlash(checkbox.isChecked());
                        torchBtn.setBackgroundResource(R.drawable.button_off);
                        flashlightSwitch.setEnabled(true);
                    }
                    TorchUtils.IS_TORCH_ON = !TorchUtils.IS_TORCH_ON;
                }
            });

            flashlightSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton cb, boolean isChecked) {
                    if (isChecked) {
                        flashlightDialog = new FlashlightDialog(MainActivity.this);
                        flashlightDialog.setOnCancelClickedListener(new FlashlightDialog.onCancelClickedListener() {
                            public void onClick() {
                                flashlightDialog.dismiss();
                                flashlightSwitch.setChecked(false);
                                isFlashLoop = false;
                            }
                        });
                        flashlightDialog.setOnStartClickedListener(new FlashlightDialog.onStartClickedListener() {
                            public void onClick(int fre) {
                                isFlashLoop = true;
                                final int frequency = 1000 - (fre + 1) * 100;
                                flashLoopThread = new FlashLoopThread(frequency);
                                flashLoopThread.start();
                                torchBtn.setEnabled(false);
                                flashlightDialog.dismiss();
                            }
                        });
                        flashlightDialog.show();
                    } else {
                        isFlashLoop = false;
                        torchBtn.setEnabled(true);
                    }
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_layout_back_button:
                finish();
                break;
        }
    }

    private class FlashLoopThread extends Thread {
        private int frequency;
        public FlashLoopThread(int frequency) {
            this.frequency = Math.abs(frequency);
        }
        public void run() {
            try {
                while (isFlashLoop) {
                    TorchUtils.closeFlash(checkbox.isChecked());
                    SystemClock.sleep(frequency);
                    TorchUtils.openFlash(mContext, checkbox.isChecked());
                    SystemClock.sleep(30);
                }
                TorchUtils.closeFlash(checkbox.isChecked());
            } catch (final Exception e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (TorchUtils.IS_TORCH_ON) {
            torchBtn.setBackgroundResource(R.drawable.button_on);
        } else {
            torchBtn.setBackgroundResource(R.drawable.button_off);
        }
    }

    private void screenLight() {
        Window localWindow = this.getWindow();
        WindowManager.LayoutParams params = localWindow.getAttributes();
        params.screenBrightness = 1.0f;
        localWindow.setAttributes(params);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(mContext, "????????????????????????", Toast.LENGTH_SHORT).show();
                }
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent("android.appwidget.action.APPWIDGET_UPDATE");
        this.sendBroadcast(intent);
    }
}
