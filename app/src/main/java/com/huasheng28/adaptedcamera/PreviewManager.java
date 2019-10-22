package com.huasheng28.adaptedcamera;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PreviewManager {
    private Camera camera = null;
    private Parameters params = null;
    public static int PreviewWidth;
    public static int PreviewHeight;

    public PreviewManager(Camera camera){
        this.camera = camera;
        this.params = camera.getParameters();
    }

    public void setBestPreviewSize(int screenWidth, int screenHeight){
        try{
            int tmpWidth;
            int tmpHeight;
            if (CameraManager.degree == 90 || CameraManager.degree== 270){
                tmpWidth = screenHeight;
                tmpHeight = screenWidth;
            }else {
                tmpWidth = screenWidth;
                tmpHeight = screenHeight;
            }
            List<Size> list = params.getSupportedPreviewSizes();

            for (Size tmp : list){//如果有预览尺寸与屏幕尺寸相同，直接返回
                if (tmp.width == tmpWidth && tmp.height == tmpHeight){
                    PreviewWidth = tmp.width;
                    PreviewHeight = tmp.height;
                    Log.d(MainActivity.TAG, "PreviewWidth: " + PreviewWidth + " PreviewHeight: " +PreviewHeight);
                    params.setPreviewSize(PreviewWidth, PreviewHeight);
                    camera.setParameters(params);
                    return;
                }
            }

            List<Size> closeSizeList = getCloseRatioSizeList(list, tmpWidth, tmpHeight);
            Size size;
            if (closeSizeList.size() >0){
                int minArea = screenHeight * screenWidth;
                size = getMinPreviewSize(closeSizeList, minArea);
                if (size == null){
                    size = getMaxPreviewSize(closeSizeList);
                    if (size == null) {
                        size = params.getPreviewSize();
                    }
                }
            }else {
                size = params.getPreviewSize();
            }

            PreviewWidth = size.width;
            PreviewHeight = size.height;
            Log.d(MainActivity.TAG, "PreviewWidth: " + PreviewWidth + " PreviewHeight: " +PreviewHeight);
            params.setPreviewSize(PreviewWidth, PreviewHeight);
            camera.setParameters(params);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //获取相机支持的预览尺寸比例和所给的尺寸比例绝对值小于0.1的尺寸列表
    public static List<Size> getCloseRatioSizeList(List<Size> sizeList, int width, int height) {
        float ratio = (float) width / height;
        List<Size> listSize = new ArrayList<Size>();
        Iterator<Size> itor = sizeList.iterator();
        while (itor.hasNext()) {
            Size cur = itor.next();
            float curRatio = (float) cur.width / cur.height;
            if (0.1 >= Math.abs(ratio - curRatio)) {
                listSize.add(cur);
            }
        }
        return listSize;
    }

    //从尺寸列表中获取预览尺寸的面积大于最小面积的最小尺寸。
    public static Size getMinPreviewSize(List<Size> sizeList, int lowestArea) {
        Size minSize = null;
        if (sizeList.size() >= 1) {
            Iterator<Size> itor = sizeList.iterator();
            while (itor.hasNext()) {
                Size cur = itor.next();
                int temp = cur.width * cur.height;
                if (temp >= lowestArea) {
                    if (minSize == null) {
                        minSize = cur;
                    } else {
                        int min = minSize.width * minSize.height;
                        if (temp < min) {
                            minSize = cur;
                        }
                    }
                }
            }
        }
        return minSize;
    }

    //从尺寸列表中获取预览尺寸的面积最大的尺寸。
    public static Size getMaxPreviewSize(List<Size> sizelist) {
        Size maxSize = null;
        int lenth_size = sizelist.size();
        if (lenth_size >= 1) {
            Iterator<Size> itor = sizelist.iterator();
            while (itor.hasNext()) {
                Size cur = itor.next();
                int temp = cur.width * cur.height;
                if (maxSize == null){
                    maxSize = cur;
                }else {
                    int max = maxSize.width * maxSize.height;
                    if (temp > max) {
                        maxSize = cur;
                    }
                }
            }
        }
        return maxSize;
    }
}
