package com.example.android.classscheduler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by jonathanbarrera on 6/3/18.
 * This class contains two helper methods for converting between byte[] and bitmap
 */

public class BitmapUtils {

    // Helper method to convert Bitmap to Byte Array
    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        // TODO change to 100% quality when using the online database
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    // Helper method to convert ByteArray to Bitmap
    public static Bitmap byteArrayToBitmap(byte[] bytes) {
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
