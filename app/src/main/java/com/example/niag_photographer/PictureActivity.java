package com.example.niag_photographer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class PictureActivity extends ActivityController implements SurfaceHolder.Callback{

    private static final String TAG = "picture_page";
    private Button develop_btn;
    private Camera mCamera;
    private SurfaceView mPreview;
    private SurfaceHolder mHolder;
    private int click_num = 0;
    private int cameraId=1;//聲明cameraId屬性，ID为1調用前置鏡頭，0調用後置鏡頭

    private Camera.PictureCallback mpictureCallback=new Camera.PictureCallback(){
        @Override
        public void onPictureTaken(byte[] data,Camera camera){
            File tempfile=new File("/sdcard/emp.png");
            //新建一个文件对象tempfile，並保存在某路徑(sdcard)中
            try{ FileOutputStream fos =new FileOutputStream(tempfile);
                fos.write(data);//將照片放入文件中
                fos.close();//關閉文件
                Intent intent=new Intent(PictureActivity.this,AfterPictureActivity.class);//新建信使對象
                intent.putExtra("picpath",tempfile.getAbsolutePath());//打包文件给信使
                startActivity(intent);//開新的activity，即打開展示照片的布局介面
                PictureActivity.this.finish();//關閉現有介面
            }
            catch (IOException e){e.printStackTrace();}
        }
    };

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        //robot = Robot.getInstance();//temi
        getPermission();

        develop_btn = (Button)findViewById(R.id.topbar_btn);
        develop_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                click_num ++;
                if (click_num>=10){
                    Log.d(TAG, "develop mode on ! ");
                    click_num = 0;
                    //top bar open
                    /*
                    if(turnDevelopMode(robot)){
                        Toast.makeText(CameraActivity.this, "工作人員模式", Toast.LENGTH_LONG).show();
                    }

                     */

                }
            }
        });

        final TextView textView = (TextView)findViewById(R.id.count_down_txt);


        CountDownTimer countDownTimer = new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long l) {
                if (l<=6000){
                    long t = l/1000L;
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(String.format(Locale.getDefault(), "%d", t));

                }

            }

            @Override
            public void onFinish() {
                textView.setVisibility(View.INVISIBLE);
                //robot.speak(TtsRequest.create("5 4 3 2 1", false));
                //capture();
            }
        }.start();


        mPreview=findViewById(R.id.camera_scene);//初始化預覽介面
        mHolder=mPreview.getHolder();
        mHolder.addCallback(this);
        //點擊預覽介面聚焦
        mPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.autoFocus(null);
            }
        });
    }


    public void capture(){
        Camera.Parameters parameters=mCamera.getParameters();
        parameters.setPictureFormat(ImageFormat.JPEG);//設置照片格式
        parameters.setPreviewSize(800,400);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        //鏡頭聚焦
        mCamera.autoFocus(new Camera.AutoFocusCallback(){
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if(success){mCamera.takePicture(null,null, mpictureCallback);}
            }
        });

    }

    //activity生命周期在onResume是介面應是顯示狀態
    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera==null){//如果此時鏡頭值仍為空
            mCamera=getCamera();//則通過getCamera()方法開啟鏡頭
            if(mHolder!=null){
                setStartPreview(mCamera,mHolder);//開啟預覽介面
            }
        }
    }
    //activity暫停的時候釋放鏡頭
    @Override
    protected void onPause() {
        super.onPause();
        releaseCamera();
    }
    //onResume()中提到的開啟鏡頭的方法
    private Camera getCamera(){
        Camera camera;//聲明局部變量camera
        try{
            camera=Camera.open(cameraId);}//根據 cameraID 設置打開鏡頭
        catch (Exception e){
            camera=null;
            e.printStackTrace(); }
        return camera;
    }

    //開啟預覽介面
    private void setStartPreview(Camera camera,SurfaceHolder holder){
        try{
            camera.setPreviewDisplay(holder);
            camera.setDisplayOrientation(180);//如果没有這行你看到的預覽介面就会是水平的
            camera.startPreview();}
        catch (Exception e){
            e.printStackTrace(); }
    }
    //定義釋放鏡頭的方法
    private void releaseCamera(){
        if(mCamera!=null){//如果鏡頭還未釋放，則執行以下
            mCamera.stopPreview();//1.首先停止預覽
            mCamera.setPreviewCallback(null);//2.預覽返回值為null
            mCamera.release(); //3.釋放鏡頭
            mCamera=null;//4.鏡頭對象值為null
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setStartPreview(mCamera,mHolder);
    }

    @Override
    /**
     public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
     mCamera.stopPreview();//如果預覽介面改變，則首先停止預覽介面
     if(mCamera !=null) {
     mCamera.release();
     mCamera=null;
     }
     setStartPreview(mCamera,mHolder);//調整再重新打開預覽介面
     }
     */

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        if (holder.getSurface() == null){
            // preview surface does not exist
            Log.d(TAG, "holder.getSurface() == null");
            return;
        }

        try {
            mCamera.stopPreview();

        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
            Log.d(TAG, "Error stopping camera preview: " + e.getMessage());
        }

        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            mCamera.startFaceDetection(); // re-start face detection feature

        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();//若預覽介面銷毀則釋放相機
    }


    public void getPermission(){
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            //ActivityCompat.requestPermissions(this,},1);
        }
        else {
            Log.d(TAG, "getPermission: has permission");
        }
    }

}