package com.example.gxkj.cardnumbinding.camera;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gxkj.cardnumbinding.R;
import com.example.gxkj.cardnumbinding.camera.camera.CameraManager;
import com.example.gxkj.cardnumbinding.camera.decode.AmbientLightManager;
import com.example.gxkj.cardnumbinding.camera.decode.BeepManager;
import com.example.gxkj.cardnumbinding.camera.decode.CaptureActivityHandler;
import com.example.gxkj.cardnumbinding.camera.decode.InactivityTimer;
import com.example.gxkj.cardnumbinding.camera.view.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.IOException;
import java.util.Collection;



/**
 * @author Endless
 */
public class CaptureActivity extends Activity implements SurfaceHolder.Callback {

    private static final String TAG = CaptureActivity.class.getSimpleName();
    private static final int SCAN_HINT = 1001;
    private static final int CODE_HINT = 1002;

    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private ViewfinderView viewfinderView;
    private boolean hasSurface;
    private Collection<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;
    private AmbientLightManager ambientLightManager;
    private TextView toolbarTitle;
    private LinearLayout viewEnterQrcode;
    private RelativeLayout toManualEnterQrcode;
    private SurfaceView surfaceView;

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);//底部导航栏覆盖activity
        setContentView(R.layout.act_capture);
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);
        ambientLightManager = new AmbientLightManager(this);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        initToolBar();
    }

    private void initToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.left));
        toolbar.inflateMenu(R.menu.base_toolbar_menu);
        MenuItem item = toolbar.getMenu().getItem(0);
        toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("扫描二维码");
        item.setTitle("帮助");
        item.setOnMenuItemClickListener(v -> {
            Toast.makeText(CaptureActivity.this, "帮助界面开发中", Toast.LENGTH_SHORT).show();
            return false;
        });
        toolbar.setNavigationOnClickListener(view ->  {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAfterTransition();
            } else {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // CameraManager must be initialized here, not in onCreate(). This is necessary because we don't
        // want to open the camera driver and measure the screen size if we're going to show the help on
        // first launch. That led to bugs where the scanning rectangle was the wrong size and partially
        // off screen.
        cameraManager = new CameraManager(getApplication());

        viewfinderView = findViewById(R.id.viewfinderView);
        viewfinderView.setCameraManager(cameraManager);
        handler = null;

        beepManager.updatePrefs();
        ambientLightManager.start(cameraManager);

        inactivityTimer.onResume();

        decodeFormats = null;
        characterSet = null;

        surfaceView = findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        handleEnterCode();


        if (hasSurface) {
            // The activity was paused but not stopped, so the surface still exists. Therefore
            // surfaceCreated() won't be called, so init the camera here.
            initCamera(surfaceHolder);
        } else {
            // Install the callback and wait for surfaceCreated() to init the camera.
            surfaceHolder.addCallback(this);
        }
    }

    private void handleEnterCode() {
        //手动输入二维码编号
        viewEnterQrcode = findViewById(R.id.view_enter_qrcode);
        toManualEnterQrcode = findViewById(R.id.to_manual_enter_qrcode);
//        findViewById(R.id.enter_qrcode_img).setOnClickListener(v -> showEnterCodeView());
        findViewById(R.id.enter_qrcode_tv).setOnClickListener(v -> showEnterCodeView());
        AppCompatButton btnEnterCode = findViewById(R.id.action_enter_qrcode);
        final EditText inputQrcode = findViewById(R.id.input_qrcode);
        btnEnterCode.setOnClickListener(v -> {
            String code = inputQrcode.getText().toString();
            if (!TextUtils.isEmpty(code)) {
                Intent resultIntent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("result", code);
                resultIntent.putExtras(bundle);
                CaptureActivity.this.setResult(CODE_HINT, resultIntent);
                CaptureActivity.this.finish();
            } else {
                Toast.makeText(CaptureActivity.this, getString(R.string.plz_enter_qrcode), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEnterCodeView() {
        surfaceView.setVisibility(View.GONE);
        viewfinderView.setVisibility(View.GONE);
        viewEnterQrcode.setVisibility(View.VISIBLE);
        toolbarTitle.setText("输入二维码编号");
        toManualEnterQrcode.setVisibility(View.GONE);
        ImageView viewLogo = viewEnterQrcode.findViewById(R.id.act_capture_logo);
//        String userLogo = PreferencesUtils.getInstance(this).getUserLogo();
//        if (!TextUtils.isEmpty(userLogo)) {
//            Glide.with(this).load(Constant.getImgUrl() + userLogo)
//                    .apply(new RequestOptions().error(R.drawable.load_fail_pic))
//                    .into(viewLogo);
//        }
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        ambientLightManager.stop();
        beepManager.close();
        cameraManager.closeDriver();
        //historyManager = null; // Keep for onActivityResult
        if (!hasSurface) {
            surfaceView = (SurfaceView) findViewById(R.id.preview_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.removeCallback(this);
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG, "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, decodeFormats, characterSet, cameraManager);
            }
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
        }
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();
    }

    /**
     * 处理扫码结果
     *
     * @param rawResult
     * @param barcode
     * @param scaleFactor
     */
    public void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor) {

        boolean fromLiveScan = barcode != null;
        if (fromLiveScan) {
            String resultString = rawResult.getText();
            // Then not from history, so beep/vibrate and we have an image to draw on
            beepManager.playBeepSoundAndVibrate();
            Intent resultIntent = new Intent();
            Bundle bundle = new Bundle();
            bundle.putString("result", resultString);
            resultIntent.putExtras(bundle);
            this.setResult(SCAN_HINT, resultIntent);
        } else {
            Toast.makeText(CaptureActivity.this, "扫描失败，请重试", Toast.LENGTH_SHORT).show();
        }
        CaptureActivity.this.finish();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }
}
