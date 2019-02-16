package com.easysoftware.phototaker.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Callable;

import io.reactivex.Observable;

public class Utils {

    public static Observable<File> createPhotoFile(final Context context) {
        return Observable.fromCallable(new Callable<File>() {
            @Override
            public File call() throws IOException {
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                String photoFileName = "PHOTO_" + timeStamp;
                File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                File photoFile = File.createTempFile(photoFileName, ".jpg", storageDir);
                return photoFile;
            }
        });
    }

    public static Observable<Bitmap> decodeBitmap(final String url) {
        return Observable.fromCallable(new Callable<Bitmap>() {
            @Override
            public Bitmap call() {
                return BitmapFactory.decodeFile(url);
            }
        });
    }
}
