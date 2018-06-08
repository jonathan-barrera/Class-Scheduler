package com.example.android.classscheduler.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class StudentContract {

    private StudentContract(){}

    // Content Uri statics
    public static final String CONTENT_AUTHORITY = "com.example.android.classscheduler";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_STUDENTS = "students";

    public static abstract class StudentEntry implements BaseColumns {
        // The ContentUri to access the student database in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_STUDENTS);

        // The MIME type of the CONTENT_URI for a list of students
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STUDENTS;

        // The MIME type of the CONTENT_URI for a single student
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_STUDENTS;

        // Name of the students table in the database
        public static final String TABLE_NAME = "students";

        // Column Titles for the students table
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SEX = "sex";
        public static final String COLUMN_AGE = "age";
        public static final String COLUMN_GRADE = "grade";
        public static final String COLUMN_CLASSES = "classes";
        public static final String COLUMN_PICTURE = "picture";

        // Possible values for sex
        public static final int SEX_MALE = 0;
        public static final int SEX_FEMALE = 1;

        // Return whether or not the given sex is valid
        public static boolean isValidSex(int gender) {
            return (gender == SEX_MALE || gender == SEX_FEMALE);
        }
    }
}
