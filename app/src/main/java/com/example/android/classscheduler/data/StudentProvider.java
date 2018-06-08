package com.example.android.classscheduler.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.TextureView;

import java.sql.SQLData;

public class StudentProvider extends ContentProvider{

    // Database helper method
    private StudentDbHelper mDbHelper;

    // Uri matcher code for the content uri for the students table
    private static final int STUDENTS_TABLE_CODE = 100;

    // Uri matcher code for a single student
    private static final int SINGLE_STUDENT_CODE = 101;

    // UriMatcher code to match a content URI to a corresponding code
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(StudentContract.CONTENT_AUTHORITY, StudentContract.PATH_STUDENTS,
                STUDENTS_TABLE_CODE);
        sUriMatcher.addURI(StudentContract.CONTENT_AUTHORITY, StudentContract.PATH_STUDENTS
                + "/#", SINGLE_STUDENT_CODE);
    }

    @Override
    // Initialize DbHelper
    public boolean onCreate() {
        mDbHelper = new StudentDbHelper(getContext());

        return true;
    }

    // Perform the query for the given URI
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // Create cursor variable
        Cursor cursor;

        // Use the UriMatcher to match the URI to a code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case STUDENTS_TABLE_CODE:
                // Query the entire table
                cursor = database.query(StudentContract.StudentEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            case SINGLE_STUDENT_CODE:
                // Query a single student
                // Extract the student ID from the URI
                selection = StudentContract.StudentEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                // Return the row with _ID = student ID
                cursor = database.query(StudentContract.StudentEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI: " + uri);
        }
        // Set notification URI on the cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    // Returns the MIME type of data for the content URI
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case STUDENTS_TABLE_CODE:
                return StudentContract.StudentEntry.CONTENT_LIST_TYPE;
            case SINGLE_STUDENT_CODE:
                return StudentContract.StudentEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    // Insert new data into the provider with the given Content Values
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        // Match URI to a specific code
        final int match = sUriMatcher.match(uri);
        switch(match) {
            case STUDENTS_TABLE_CODE:
                return insertStudent(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for: " + uri);
        }
    }

    // helper method
    private Uri insertStudent(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(StudentContract.StudentEntry.COLUMN_NAME);
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("StudentLocalDatabase requires a name.");
        }

        // Check that the sex is not null
        Integer sex = values.getAsInteger(StudentContract.StudentEntry.COLUMN_SEX);
        if (sex == null || !StudentContract.StudentEntry.isValidSex(sex)) {
            throw new IllegalArgumentException("StudentLocalDatabase requires appropriate sex.");
        }

        // Check that the age is above 0
        Integer age = values.getAsInteger(StudentContract.StudentEntry.COLUMN_AGE);
        if (age != null && age < 0) {
            throw new IllegalArgumentException("StudentLocalDatabase requires appropriate age.");
        }

        // Check that the grade is above 0
        Integer grade = values.getAsInteger(StudentContract.StudentEntry.COLUMN_GRADE);
        if (grade != null && grade < 0) {
            throw new IllegalArgumentException("StudentLocalDatabase requires appropriate grade.");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(StudentContract.StudentEntry.TABLE_NAME, null, values);

        if (id == -1) {
            // Insert failed
            Log.e("StudentProvider.class", "Insertion failed for " + uri);
            return null;
        }

        // Notify listeners that the data has changed for the student table content uri
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Keep track of rows deleted
        int rowsDeleted;

        // Match the uri
        int match = sUriMatcher.match(uri);
        switch (match) {
            case STUDENTS_TABLE_CODE:
                rowsDeleted = database.delete(StudentContract.StudentEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            case SINGLE_STUDENT_CODE:
                // Extract the student ID from the URI
                selection = StudentContract.StudentEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                rowsDeleted = database.delete(StudentContract.StudentEntry.TABLE_NAME, selection,
                        selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for: " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Match URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case STUDENTS_TABLE_CODE:
                return updateStudent(uri, values, selection, selectionArgs);
            case SINGLE_STUDENT_CODE:
                // Extract the student ID from the URI
                selection = StudentContract.StudentEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf(ContentUris.parseId(uri))};

                return updateStudent(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for: " + uri);
        }
    }

    private int updateStudent(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Check that the name is not null
        String name = values.getAsString(StudentContract.StudentEntry.COLUMN_NAME);
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("StudentLocalDatabase requires a name.");
        }

        // Check that the sex is not null
        Integer sex = values.getAsInteger(StudentContract.StudentEntry.COLUMN_SEX);
        if (sex == null || !StudentContract.StudentEntry.isValidSex(sex)) {
            throw new IllegalArgumentException("StudentLocalDatabase requires appropriate sex.");
        }

        // Check that the age is above 0
        Integer age = values.getAsInteger(StudentContract.StudentEntry.COLUMN_AGE);
        if (age != null && age < 0) {
            throw new IllegalArgumentException("StudentLocalDatabase requires appropriate age.");
        }

        // Check that the grade is above 0
        Integer grade = values.getAsInteger(StudentContract.StudentEntry.COLUMN_GRADE);
        if (grade != null && grade < 0) {
            throw new IllegalArgumentException("StudentLocalDatabase requires appropriate grade.");
        }

        // If there are no values to update, then don't try to update the database.
        if (values.size() == 0) {
            return 0;
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(StudentContract.StudentEntry.TABLE_NAME, values,
                selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }
}
