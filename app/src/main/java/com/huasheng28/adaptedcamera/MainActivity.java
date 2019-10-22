package com.huasheng28.adaptedcamera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener, View.OnTouchListener {
    public static CameraTextureView textureView = null;
    public RelativeLayout takeLayout = null;
    public static ImageButton takeButton = null;
    public  TextView retakeButton = null;
    public  TextView submitButton = null;
    public static ImageView pictureView = null;
    public static RelativeLayout picTakenLayout = null;
    public static int ScreenWidth = 0;
    public static int ScreenHeight = 0;
    public static String TAG = "ttt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
        init();
        CameraManager.getInstance().setCameraDegree(this);
        getScreenMetrics(this);
    }

    public static void getScreenMetrics(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        ScreenWidth = dm.widthPixels;
        ScreenHeight = dm.heightPixels;
        Log.d(MainActivity.TAG, "ScreenWidth: " + ScreenWidth + " ScreenHeight: " + ScreenHeight);
    }

    private void init(){
        textureView = (CameraTextureView) findViewById(R.id.camera_preview);
        textureView.setOnTouchListener(this);
        picTakenLayout = (RelativeLayout) findViewById(R.id.pic_taken);
        pictureView = (ImageView) findViewById(R.id.show_pic);
        takeLayout = (RelativeLayout) findViewById(R.id.take_layout);
        takeButton = (ImageButton) findViewById(R.id.take_picture);
        takeButton.setOnClickListener(this);
        retakeButton = (TextView) findViewById(R.id.retake);
        retakeButton.setOnClickListener(this);
        submitButton = (TextView) findViewById(R.id.submit);
        submitButton.setOnClickListener(this);
    }

    private void checkPermissions(){
        List<String> list = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            list.add(Manifest.permission.CAMERA);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            list.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        int count = list.size();
        if (count == 0)return;
        String[] listString = new String[count];
        for (int i = 0; i < count; i++){
            listString[i] =list.get(i);
        }
        ActivityCompat.requestPermissions(this,listString, 101);
        while (ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){}
        while (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){}
    }

    @Override
    protected void onResume(){
        super.onResume();
        CameraManager.getInstance().InitCamera();
        retakePic();
    }

    @Override
    protected void onPause(){
        super.onPause();
        CameraManager.getInstance().stopPreview();
    }

    @Override
    public void onClick(View v){
        switch (v.getId()) {
            case R.id.take_picture:
                PictureManager.takePicture();
                break;
            case R.id.retake:
                retakePic();
                CameraManager.getInstance().InitCamera();
                break;
            case R.id.submit:
//                UploadImage.doUpload(UploadImage.getScaledPic(PictureManager.PictureBitmap));
                break;
            default:
                break;
        }
    }

    public static void picTaken(){
        pictureView.setVisibility(View.VISIBLE);
        Bitmap target = cropBitmap(PictureManager.PictureBitmap);
        pictureView.setImageBitmap(target);

        textureView.setVisibility(View.INVISIBLE);
        takeButton.setVisibility(View.INVISIBLE);
        picTakenLayout.setVisibility(View.VISIBLE);
    }

    public static Bitmap cropBitmap(Bitmap bitmap){
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        float bitmapRatio = (float)bitmapWidth / bitmapHeight;
        float surfaceRatio = (float)ScreenWidth / ScreenHeight;
        if (bitmapRatio > surfaceRatio){
            int targetWidth = (int)(bitmapHeight * surfaceRatio);
            int targetHeight = bitmapHeight;
            int x = Math.abs((bitmapWidth - targetWidth) / 2);
            return Bitmap.createBitmap(bitmap, x, 0, targetWidth, targetHeight, null, false);
        }else if (bitmapRatio < surfaceRatio){
            int targetWidth = bitmapWidth;
            int targetHeight = (int)(bitmapWidth / surfaceRatio);
            int y = Math.abs((bitmapHeight - targetHeight) /2);
            return Bitmap.createBitmap(bitmap, 0, y, targetWidth, targetHeight, null, false);
        }else {
            return bitmap;
        }
    }

    public static void retakePic(){
        pictureView.setVisibility(View.INVISIBLE);
        pictureView.setImageBitmap(null);
        PictureManager.PictureByte = null;
        PictureManager.PictureBitmap = null;
        textureView.setVisibility(View.VISIBLE);
        takeButton.setVisibility(View.VISIBLE);
        picTakenLayout.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event){
        if(view.getId() == R.id.camera_preview) {
            if (event.getPointerCount() == 1 && event.getY() < ScreenHeight - takeLayout.getHeight()) {
                FocusManager.focus();
            }
        }
        return false;
    }
}
