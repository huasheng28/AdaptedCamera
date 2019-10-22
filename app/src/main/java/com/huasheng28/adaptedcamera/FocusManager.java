package com.huasheng28.adaptedcamera;

import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class FocusManager {
    private static Camera camera = null;
    private static Parameters parameters = null;

    public FocusManager(Camera ca){
        camera =ca;
        parameters = ca.getParameters();
        parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        camera.setParameters(parameters);
    }

    public void setFocusArea(Rect focusRectDM){
        int width_dm = MainActivity.ScreenWidth;
        int height_dm = MainActivity.ScreenHeight;

        // 对焦位置固定指定为focusRectDM
        if (parameters.getMaxNumFocusAreas() > 0) {
            int x1 = (int) (focusRectDM.left * 2000 / width_dm) - 1000;
            int x2 = (int) (focusRectDM.right * 2000 / width_dm) - 1000;
            int y1 = (int) (focusRectDM.top * 2000 / height_dm) - 1000;
            int y2 = (int) (focusRectDM.bottom * 2000 / height_dm) - 1000;
            Rect focusRect = new Rect(x1, y1, x2, y2);
            List<Camera.Area> focusAreas = new ArrayList<Camera.Area>();
            focusAreas.add(new Camera.Area(focusRect, 1000));
            parameters.setFocusAreas(focusAreas);
            camera.setParameters(parameters);
        }
    }

    public Rect getCenterRect(int width, int height){
        int w = width /3;
        int h = height /3;
        return new Rect(w, h, 2 * w, 2 * h);
    }

    public static void focus(){
        if (camera != null && CameraManager.isPreviewing){
            camera.cancelAutoFocus();
            parameters.setFocusMode(Parameters.FOCUS_MODE_AUTO);
            camera.setParameters(parameters);
            camera.autoFocus(Callback);
        }
    }

    public static Camera.AutoFocusCallback Callback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            try {
                camera.cancelAutoFocus();
                if (success){
                    parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
                    camera.setParameters(parameters);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(MainActivity.TAG, e.getMessage());
            }
        }
    };
}
