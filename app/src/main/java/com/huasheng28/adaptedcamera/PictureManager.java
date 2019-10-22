package com.huasheng28.adaptedcamera;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class PictureManager {
    private static Camera camera = null;
    private Parameters params = null;
    public static int PictureWidth;
    public static int PictureHeight;
    public static byte[] PictureByte = null;
    public static Bitmap PictureBitmap = null;
    public static final int MESSAGE_BITMAP_READY = 1;

    public PictureManager(Camera ca){
        camera = ca;
        this.params = ca.getParameters();
    }

    public void setBestPictureSize(int width, int height){
        try {
            List<Size> pictureSize = params.getSupportedPictureSizes();
            Collections.sort(pictureSize, new Comparator<Size>() {
                @Override
                public int compare(Size a, Size b) {
                    return a.width * a.height - b.width * b.height;
                }
            });
            int limitSize = 1920 * 1080;
            Size size;
            size = getMinPictureSize(pictureSize,limitSize);
            PictureWidth = size.width;
            PictureHeight = size.height;
            params.setPictureSize(PictureWidth, PictureHeight);
            camera.setParameters(params);
            Log.d(MainActivity.TAG, "PictureWidth: " +PictureWidth+ " PictureHeight: " +PictureHeight);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Size getMinPictureSize(List<Size> sizeList, int lower_limit) {
        Size minsize = null;
        if (sizeList.size() >= 1) {
            Iterator<Size> itor = sizeList.iterator();
            while (itor.hasNext()) {
                Size cur = itor.next();
                int temp = cur.width * cur.height;
                if (temp >= lower_limit) {
                    if (minsize == null) {
                        minsize = cur;
                    } else {
                        int min = minsize.width * minsize.height;
                        if (temp < min) {
                            minsize = cur;
                        }
                    }
                }
            }
        }
        return minsize;
    }

    public static Size getMaxPictureSize(List<Size> sizelist) {
        Size maxsize = null;
        int lenth_size = sizelist.size();
        if (lenth_size >= 1) {
            Iterator<Size> itor = sizelist.iterator();
            while (itor.hasNext()) {
                Size cur = itor.next();
                int temp = cur.width * cur.height;
                if (maxsize == null)
                    maxsize = cur;
                else {
                    int max = maxsize.width * maxsize.height;
                    if (temp > max) {
                        maxsize = cur;
                    }
                }
            }
        }
        return maxsize;
    }

    public static void takePicture(){
        if (camera != null && CameraManager.isPreviewing){
            try {
                CameraManager.isPreviewing = false;
                camera.takePicture(null, null, pictureCallback);
            }catch ( Exception e){
                e.printStackTrace();
                Log.d(MainActivity.TAG, e.getMessage());
            }
        }
    }

    public static Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                CameraManager.getInstance().stopPreview();
                PictureByte = data;
                PictureBitmap = rotateBitmap(data);
                Message message = new Message();
                message.what = MESSAGE_BITMAP_READY;
                handler.sendMessage(message);
            }catch (Exception e){
                e.printStackTrace();
                Log.d(MainActivity.TAG, e.getMessage());
            }
        }
    };

    public static Bitmap rotateBitmap(byte[] pictureByte){
        Bitmap bitmap = BitmapFactory.decodeByteArray(pictureByte, 0, pictureByte.length);
        if (CameraManager.degree != 0){
            int originWidth = bitmap.getWidth();
            int originHeight = bitmap.getHeight();
            Matrix matrix = new Matrix();
            matrix.setRotate(CameraManager.degree);
            return Bitmap.createBitmap(bitmap,0, 0, originWidth, originHeight, matrix, false);
        }
        return bitmap;
    }

    @SuppressLint("HandlerLeak")
    public static final Handler handler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_BITMAP_READY:
                    savePic(PictureBitmap);
                    MainActivity.picTaken();
                    break;
                default:
                    break;
            }
        }
    };

    private static void savePic(Bitmap bitmap){
        File dir = new File(Environment.getExternalStorageDirectory(), "huasheng28");
        if (!dir.exists()){
            dir.mkdirs();
        }
        File currentImageFile = new File(dir, System.currentTimeMillis()+ ".jpg");
        Log.d(MainActivity.TAG, currentImageFile.getAbsolutePath());
        if (!currentImageFile.exists()){
            try {
                currentImageFile.createNewFile();
                FileOutputStream outputStream = new FileOutputStream(currentImageFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
            }catch (IOException e){
                e.printStackTrace();
                Log.d(MainActivity.TAG, e.getMessage());
            }
        }
    }
}
