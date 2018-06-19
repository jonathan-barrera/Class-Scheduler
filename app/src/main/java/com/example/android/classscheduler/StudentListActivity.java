package com.example.android.classscheduler;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.android.classscheduler.Model.Student;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class StudentListActivity extends AppCompatActivity {

    // Member variables
    private StudentAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private String mUserId;

    // Firebase instances
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mStudentDatabaseReference;
    private ChildEventListener mChildEventListener;

    // List of Student Objects
    private List<Student> mStudentList;
    private List<Student> mMatchedStudentList;

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
                .child("users")
                .child(mUserId)
                .child("students");

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

        // Use this data to download relevant SchoolClass objects from Firebase Database
        // Initialize Firebase instances
        if (mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    Student student = dataSnapshot.getValue(Student.class);
                    for (int i = 0; i < mStudentList.size(); i++) {
                        if (mStudentList.get(i).getStudentId().equals(student.getStudentId())) {
                            return;
                        }
                    }
                    mStudentList.add(student);
                    mAdapter.notifyDataSetChanged();
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

            mStudentDatabaseReference.addChildEventListener(mChildEventListener);
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
                    Timber.d(title + "flag");
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

        // Detach ChildEventListener
        if (mChildEventListener != null) {
            mStudentDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }
    }
}