package com.example.android.classscheduler.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.classscheduler.data.StudentContract.StudentEntry;

public class StudentDbHelper extends SQLiteOpenHelper {

    // If you update the database schema, must increment the version number
    public static final int DATABASE_VERSION = 1;

    // Name of the database
    public static final String DATABASE_NAME = "studentdatabase.db";

    public StudentDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a string the creates the SQL statement
        String SQL_CREATE_STUDENT_TABLE = "CREATE TABLE " + StudentEntry.TABLE_NAME + " (" +
                StudentEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                StudentEntry.COLUMN_NAME + " TEXT NON NULL, " +
                StudentEntry.COLUMN_SEX + " INTEGER NON NULL, " +
                StudentEntry.COLUMN_AGE + " INTEGER NON NULL, " +
                StudentEntry.COLUMN_GRADE + " INTEGER NON NULL, " +
                StudentEntry.COLUMN_PICTURE + " BLOG, " +
                StudentEntry.COLUMN_CLASSES + " TEXT);";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_STUDENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
