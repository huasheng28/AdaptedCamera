package com.huasheng28.adaptedcamera;

import android.graphics.Bitmap;
        import android.os.Environment;
        import android.util.Log;

        import java.io.BufferedOutputStream;
        import java.io.ByteArrayOutputStream;
        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.util.concurrent.TimeUnit;

        import okhttp3.Call;
        import okhttp3.Callback;
        import okhttp3.MediaType;
        import okhttp3.MultipartBody;
        import okhttp3.OkHttpClient;
        import okhttp3.Request;
        import okhttp3.RequestBody;
        import okhttp3.Response;

public class UploadImage {

    public UploadImage(){}

    public static void doUpload(byte[] bytes){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
        MultipartBody multipartBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("image","filename.jpg", RequestBody.create(MediaType.parse("image/jpg"), bytes))
                .build();
        final Request request = new Request.Builder()
                .url("http://192.168.1.211/v1/identify")
                .post(multipartBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d(MainActivity.TAG, "upload failed");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d(MainActivity.TAG, "upload success");
                Log.d(MainActivity.TAG, response.body().string());
            }
        });
    }

    public static byte[] getScaledPic(Bitmap origin){
        int w = origin.getWidth();
        int h = origin.getHeight();
        Bitmap scaled;
        if (w < h) {
            int width = 1700;
            int height = width * h / w;
            scaled = Bitmap.createScaledBitmap(origin, width, height, true);
        }else {
            int height = 1700;
            int width = height * w / h;
            scaled = Bitmap.createScaledBitmap(origin, width, height, true);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        scaled.compress(Bitmap.CompressFormat.JPEG, 85, baos);
        byte[] bytes = baos.toByteArray();
        return bytes;
    }
}
