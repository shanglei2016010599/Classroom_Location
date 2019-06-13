package com.example.classroom_location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.classroom_location.camera.CameraManager;
import com.example.classroom_location.decoding.CaptureActivityHandler;
import com.example.classroom_location.decoding.InactivityTimer;
import com.example.classroom_location.view.ViewfinderView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.io.IOException;
import java.util.Vector;

import okhttp3.Call;
import okhttp3.Response;


public class QRScannerActivity extends AppCompatActivity implements SurfaceHolder.Callback {


    ViewfinderView viewfinderView;

    private CaptureActivityHandler handler;
    private boolean hasSurface;
    private Vector<BarcodeFormat> decodeFormats;
    private String characterSet;
    private InactivityTimer inactivityTimer;
    private MediaPlayer mediaPlayer;
    private boolean playBeep;
    private static final float BEEP_VOLUME = 0.10f;
    private boolean vibrate;

    private static final String TAG = "QRScannerActivity";
    private static final int CHECK_IN_OK = 4;
    private static final int CHECK_IN_ERRPR = 5;
    private static String resultString;
    private String name;
    private String account;
    private String Location;
    private String status;
    private Handler Checkhandler;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_qrscanner);

        viewfinderView = findViewById(R.id.viewfinder_view);

        init();

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 1);
        }

        final Intent intent = getIntent();
        name = intent.getStringExtra("name");
        account = intent.getStringExtra("account");
        status = intent.getStringExtra("status");

        Checkhandler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case CHECK_IN_OK:
                        Intent Checkintent = new Intent();
                        Checkintent.putExtra("status", status);
                        Checkintent.putExtra("location", Location);
                        setResult(CHECK_IN_OK, intent);
                        finish();
                        break;
                    case CHECK_IN_ERRPR:
                        finish();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void init() {
        CameraManager.init(getApplication());
        hasSurface = false;
        inactivityTimer = new InactivityTimer(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SurfaceView surfaceView = findViewById(R.id.preview_view);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(this);
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        decodeFormats = null;
        characterSet = null;

        playBeep = true;
        AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
        if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
            playBeep = false;
        }
        initBeepSound();
        vibrate = true;

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    /**
     * 处理扫描结果
     */
    public void handleDecode(Result result) {
        inactivityTimer.onActivity();
        playBeepSoundAndVibrate();
        resultString = result.getText();

        if (TextUtils.isEmpty(resultString)) {
            Toast.makeText(QRScannerActivity.this, "扫描失败！", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(QRScannerActivity.this, resultString, Toast.LENGTH_SHORT).show();
            if (status.equals("0"))
                CheckIn(name, account, resultString);
            else {
                Toast.makeText(QRScannerActivity.this, "已签到过！",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
        } catch (IOException ioe) {
            return;
        } catch (RuntimeException e) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(holder);
        }

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;

    }

    public ViewfinderView getViewfinderView() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public void drawViewfinder() {
        viewfinderView.drawViewfinder();

    }

    private void initBeepSound() {
        if (playBeep && mediaPlayer == null) {
            // The volume on STREAM_SYSTEM is not adjustable, and users found it
            // too loud,
            // so we now play on the music stream.
            setVolumeControlStream(AudioManager.STREAM_MUSIC);
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setOnCompletionListener(beepListener);

            AssetFileDescriptor file = getResources().openRawResourceFd(
                    R.raw.beep);
            try {
                mediaPlayer.setDataSource(file.getFileDescriptor(),
                        file.getStartOffset(), file.getLength());
                file.close();
                mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
                mediaPlayer.prepare();
            } catch (IOException e) {
                mediaPlayer = null;
            }
        }
    }

    private static final long VIBRATE_DURATION = 200L;

    private void playBeepSoundAndVibrate() {
        if (playBeep && mediaPlayer != null) {
            mediaPlayer.start();
        }
        if (vibrate) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(VIBRATE_DURATION);
        }
    }

    /**
     * When the beep has finished playing, rewind to queue up another one.
     */
    private final MediaPlayer.OnCompletionListener beepListener = new MediaPlayer.OnCompletionListener() {
        public void onCompletion(MediaPlayer mediaPlayer) {
            mediaPlayer.seekTo(0);
        }
    };

    /* 进行签到 */
    private void CheckIn(String name, String account, final String location){
        final String url = "http://192.168.0.103:8080/test1_war_exploded/CheckInServlet?" +
                "name=" + name +
                "&account=" + account +
                "&location=" + location;
        HttpUtil.sendOkHttpRequest(url, new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                Toast.makeText(QRScannerActivity.this, "访问网络失败，请重试",
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                assert response.body() != null;
                String result = response.body().string();
                if (result.equals("success")){
                    Looper.prepare();
                    Log.d(TAG, "onResponse: 签到成功");
                    Toast.makeText(QRScannerActivity.this, "签到成功",
                            Toast.LENGTH_SHORT).show();
                    status = "1";
                    Location = location;
                    Message message = new Message();
                    message.what = CHECK_IN_OK;
                    Checkhandler.sendMessage(message);      // 发送消息
                    Looper.loop();
                } else if (result.equals("fail")){
                    Looper.prepare();
                    Toast.makeText(QRScannerActivity.this, "签到失败",
                            Toast.LENGTH_SHORT).show();
                    Message message = new Message();
                    message.what = CHECK_IN_ERRPR;
                    Checkhandler.sendMessage(message);      // 发送消息
                    Looper.loop();
                } else if (result.equals("isChecked")){
                    Looper.prepare();
                    Toast.makeText(QRScannerActivity.this, "该位置上已有人",
                            Toast.LENGTH_SHORT).show();
                    Message message = new Message();
                    message.what = CHECK_IN_ERRPR;
                    Checkhandler.sendMessage(message);      // 发送消息
                    Looper.loop();
                }

            }
        });
    }

}
