package com.example.android.classscheduler;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.classscheduler.model.Student;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class StudentListActivity extends AppCompatActivity {

    // Member variables
    private StudentAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private String mUserId;
    private Parcelable mSavedState;

    // Firebase instances
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mStudentDatabaseReference;
    private ValueEventListener mValueEventListener;

    // List of Student Objects
    private List<Student> mStudentList;
    private List<Student> mMatchedStudentList;

    // Keys
    public static final String BUNDLE_RECYCLER_VIEW_KEY = "bundle-recycler-view-key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        // Change the title to "Students"
        setTitle(getString(R.string.students));

        // Get the UserId
        SharedPreferences sharedPreferences = getSharedPreferences(MainMenu.SHARED_PREFS, MODE_PRIVATE);
        mUserId = sharedPreferences.getString(MainMenu.USER_ID_SHARED_PREF_KEY, null);

        // New list to contain student names
        mMatchedStudentList = new ArrayList<>();
        mStudentList = new ArrayList<>();

        // Initialize Firebase instances
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mStudentDatabaseReference = mFirebaseDatabase.getReference()
                .child(EditStudentInfo.FIREBASE_CHILD_KEY_USERS)
                .child(mUserId)
                .child(EditStudentInfo.FIREBASE_CHILD_KEY_STUDENTS);

        // Find a reference to the recyclerview
        mRecyclerView = findViewById(R.id.student_recycler_view);

        // Set a layoutmanager to the recyclerview
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter and set to Recycler View
        mAdapter = new StudentAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setClassData(mStudentList);
    }

    @Override
    protected void onResume() {
        super.onResume();


        // Query Database for Student info
        if (mValueEventListener == null) {
            mValueEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                    for (DataSnapshot child : children) {
                        Student student = child.getValue(Student.class);
                        mStudentList.add(student);
                    }

                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.getLayoutManager().onRestoreInstanceState(mSavedState);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            mStudentDatabaseReference.addValueEventListener(mValueEventListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_list_menu, menu);

        // Set SearchView
        MenuItem search = menu.findItem(R.id.action_search_students);
        final SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Clear list first
                mMatchedStudentList.clear();

                // Show a list of names that could match
                mAdapter.setClassData(mMatchedStudentList);

                // Loop through all students to get list of matched classes
                for (int i = 0; i < mStudentList.size(); i++) {
                    Student student = mStudentList.get(i);
                    String title = student.getName();
                    if ((title.toLowerCase()).contains(newText.toLowerCase().trim())) {
                        mMatchedStudentList.add(student);
                        mAdapter.notifyDataSetChanged();
                    }
                }
                return true;
            }

        });
        return true;
    }

    // When the FAB is clicked, take the user to the Edit StudentLocalDatabase Info page
    public void openEditStudentInfo(View view) {
        Intent intent = new Intent(this, EditStudentInfo.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Clear Student Object list
        mStudentList.clear();

        if (mValueEventListener != null) {
            mStudentDatabaseReference.removeEventListener(mValueEventListener);
            mValueEventListener = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_VIEW_KEY, mRecyclerView.getLayoutManager().
                onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mSavedState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_VIEW_KEY);
            if (mSavedState != null) {
                mRecyclerView.getLayoutManager().onRestoreInstanceState(mSavedState);
            }

        }
    }
}