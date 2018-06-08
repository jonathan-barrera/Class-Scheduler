package com.example.android.classscheduler;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.android.classscheduler.Model.Student;
import com.example.android.classscheduler.Model.StudentLocalDatabase;
import com.example.android.classscheduler.data.StudentContract;
import com.example.android.classscheduler.data.StudentContract.StudentEntry;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class StudentListActivity extends AppCompatActivity
        implements StudentAdapter.StudentAdapterOnClickHandler {

    // TODO fix the doubling of the profiles in the main list everytime you leave the app

    // Constants
    private static final int STUDENT_LOADER = 0;
    public static final String STUDENT_EXTRA_KEY = "student-extra";

    // Firebase Instances
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private ChildEventListener mChildEventListener;

    // Variables and views
    private StudentAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private List<Student> mStudentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        // Set up Timber
        Timber.plant(new Timber.DebugTree());

        // Initialize Firebase references
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("students");

        // Change the title to "Students"
        setTitle(getString(R.string.students));

        // Find a reference to the recyclerview
        mRecyclerView = findViewById(R.id.student_recycler_view);

        // Set a layoutmanager to the recyclerview
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize List of students
        mStudentList = new ArrayList<>();

        // Create an empty adapter that will be used to display the student info
        //mAdapter = new StudentLocalDatabaseAdapter(this);
        //mRecyclerView.setAdapter(mAdapter);
        mAdapter = new StudentAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        attachDatabaseReadListener();
    }

    // When the FAB is clicked, take the user to the Edit StudentLocalDatabase Info page
    public void openEditStudentInfo(View view) {
        Intent intent = new Intent(this, EditStudentInfo.class);
        startActivity(intent);
    }

    @Override
    public void onClick(Student student) {
        // Use intent to open the StudentLocalDatabase Profile activity
        Intent intent = new Intent(this, StudentProfile.class);
        intent.putExtra(STUDENT_EXTRA_KEY, student);
        startActivity(intent);
    }

    private void attachDatabaseReadListener() {
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Student student = dataSnapshot.getValue(Student.class);
                    // TODO Change this to FirebaseRecyclerView
                    mStudentList.add(student);
                    mAdapter.setStudentData(mStudentList);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
            mDatabaseReference.addChildEventListener(mChildEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (mChildEventListener != null) {
            mDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachDatabaseReadListener();
        mStudentList.clear();
        mAdapter.setStudentData(null);
    }
}

//
//    public void insertDummyData() {
//        // Temporary function to quickly add fake student data
//        ContentValues values = new ContentValues();
//        values.put(StudentContract.StudentEntry.COLUMN_NAME, "Jonathan Barrera");
//        values.put(StudentContract.StudentEntry.COLUMN_SEX, 0);
//        values.put(StudentContract.StudentEntry.COLUMN_AGE, 26);
//        values.put(StudentContract.StudentEntry.COLUMN_GRADE, 1);
//        Uri uri = getContentResolver().insert(StudentContract.StudentEntry.CONTENT_URI, values);
//    }
//
//    @Override
//    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        String[] projection = {
//                StudentEntry._ID,
//                StudentEntry.COLUMN_NAME,
//                StudentEntry.COLUMN_SEX,
//                StudentEntry.COLUMN_AGE,
//                StudentEntry.COLUMN_GRADE,
//                StudentEntry.COLUMN_PICTURE,
//                StudentEntry.COLUMN_CLASSES
//        };
//        return new CursorLoader(this, StudentEntry.CONTENT_URI, projection, null,
//                null, null);
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//        // Extract the information for each student from the cursor
//        try {
//            List<StudentLocalDatabase> studentLocalDatabaseList = new ArrayList<>();
//            while (cursor.moveToNext()) {
//                // Get student info
//                long id = cursor.getLong(cursor.getColumnIndex(StudentEntry._ID));
//                String name = cursor.getString(cursor.getColumnIndex(StudentEntry.COLUMN_NAME));
//                int sex = cursor.getInt(cursor.getColumnIndex(StudentEntry.COLUMN_SEX));
//                int age = cursor.getInt(cursor.getColumnIndex(StudentEntry.COLUMN_AGE));
//                int grade = cursor.getInt(cursor.getColumnIndex(StudentEntry.COLUMN_GRADE));
//                byte[] picture = cursor.getBlob(cursor.getColumnIndex(StudentEntry.COLUMN_PICTURE));
//                String classes = cursor.getString(cursor.getColumnIndex(StudentEntry.COLUMN_CLASSES));
//                Timber.d(id + name + sex + age + grade);
//
//                // Create student object and add to list
//                StudentLocalDatabase currentStudentLocalDatabase = new StudentLocalDatabase(id, name, sex, age, grade, picture, classes);
//                studentLocalDatabaseList.add(currentStudentLocalDatabase);
//            }
//
//            // Set list of data to the Adapter
//            //mAdapter.setStudentData(studentLocalDatabaseList);
//
//        } finally {
//            cursor.close();
//        }
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> loader) {
//    }