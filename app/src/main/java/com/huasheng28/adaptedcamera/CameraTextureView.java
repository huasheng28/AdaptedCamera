package com.huasheng28.adaptedcamera;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.util.AttributeSet;
import android.util.Log;
import android.view.TextureView;

public class CameraTextureView extends TextureView implements TextureView.SurfaceTextureListener {

    public CameraTextureView(Context context, AttributeSet attributes){
        super(context,attributes);
        this.setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        CameraManager.getInstance().setSurfaceTexture(surface);
        CameraManager.getInstance().InitCamera();
        cropCameraStream(width, height);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        cropCameraStream(width, height);
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        CameraManager.getInstance().stopPreview();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Invoked every time there's a new Camera preview frame
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed){
            MainActivity.ScreenWidth = right;
            MainActivity.ScreenHeight = bottom;
            Log.d(MainActivity.TAG, "onLayout right: " + right + " bottom: " + bottom);
        }
    }

    private void cropCameraStream(int width, int height){
        int previewWidth;
        int previewHeight;
        if (CameraManager.degree == 90 || CameraManager.degree== 270){
            previewWidth = PreviewManager.PreviewHeight;
            previewHeight = PreviewManager.PreviewWidth;
        }else {
            previewWidth = PreviewManager.PreviewWidth;
            previewHeight = PreviewManager.PreviewHeight;
        }

        RectF previewRect = new RectF(0,0, width, height);
        double previewRatio = (double) previewWidth / previewHeight;
        double surfaceRatio = (double) width /height;
        int displayWidth;
        int displayHeight;
        float dx = 0;
        float dy = 0;
        if (previewRatio > surfaceRatio){
            displayWidth = (int)(height * previewRatio);
            displayHeight = height;
            dx = (float)((displayWidth - width) / 2);
        }else {
            displayWidth = width;
            displayHeight = (int)(width / previewRatio);
            dy = (float)((displayHeight - height) / 2);
        }
        RectF targetRect = new RectF(-dx, -dy, displayWidth-dx, displayHeight-dy);
        Matrix matrix = new Matrix();
        matrix.setRectToRect(previewRect, targetRect, Matrix.ScaleToFit.FILL);
        this.setTransform(matrix);

//        Log.d(MainActivity.TAG, "previewWidth: " + previewWidth + " previewHeight: " + previewHeight);
//        Log.d(MainActivity.TAG, "previewRatio: " + previewRatio + " surfaceRatio: " + surfaceRatio);
//        Log.d(MainActivity.TAG, "displayWidth: " + displayWidth + " displayHeight: " + displayHeight);
//        Log.d(MainActivity.TAG, "dx: " + dx + " dy: " + dy);
    }
}
