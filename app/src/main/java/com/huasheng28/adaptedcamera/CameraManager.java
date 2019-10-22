package com.huasheng28.adaptedcamera;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

public class CameraManager {
    private static CameraManager cameraManager = null;
    public PreviewManager previewManager = null;
    public PictureManager pictureManager = null;
    public FocusManager focusManager = null;
    private SurfaceTexture surfaceTexture =null;
    public Camera camera = null;
    public static int degree;
    public static boolean isPreviewing = false;

    public CameraManager(){}

    public static CameraManager getInstance(){
        if (cameraManager == null){
            cameraManager = new CameraManager();
        }
        return cameraManager;
    }

    public void setSurfaceTexture(SurfaceTexture surfaceTexture){
        this.surfaceTexture = surfaceTexture;
    }

    public void InitCamera(){
        if (camera != null){
            stopPreview();
        }
        try {
            camera = Camera.open();
            camera.setDisplayOrientation(degree);
            camera.setPreviewTexture(surfaceTexture);

            previewManager = new PreviewManager(camera);
            previewManager.setBestPreviewSize(MainActivity.ScreenWidth, MainActivity.ScreenHeight);

            pictureManager = new PictureManager(camera);
            pictureManager.setBestPictureSize(PreviewManager.PreviewWidth, PreviewManager.PreviewHeight);

            focusManager = new FocusManager(camera);
            Rect rect = focusManager.getCenterRect(MainActivity.ScreenWidth, MainActivity.ScreenHeight);
            focusManager.setFocusArea(rect);

            camera.startPreview();
            isPreviewing = true;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void stopPreview(){
        if (camera != null){
            try {
                camera.setPreviewTexture(null);
                camera.stopPreview();
                isPreviewing = false;
                camera.release();
                camera = null;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //获取相机翻转角度
    public void setCameraDegree(Context context) {
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        Camera.CameraInfo camInfo = new android.hardware.Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, camInfo);

        degree=(camInfo.orientation - degrees + 360) % 360;
    }
}
