package com.example.android.classscheduler;

import android.app.AlertDialog;
import android.content.Intent;
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

    // Constants
    private final static String STUDENTS_FIREBASE_KEY = "students";

    // Member variables
    //private StudentAdapterAlt mAdapter;
    private StudentAdapter mAdapter;
    private RecyclerView mRecyclerView;

    // Firebase instances
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mClassesDatabaseReference;

    // List of Student Objects
    private List<Student> mStudentList;
    private List<Student> mMatchedStudentList;
    private List<String> mNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        // Declare and initialize Firebase references
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference studentDatabaseReference = firebaseDatabase.getReference().child(STUDENTS_FIREBASE_KEY);

        // Change the title to "Students"
        setTitle(getString(R.string.students));

        // New list to contain student names
        mNameList = new ArrayList<>();
        mMatchedStudentList = new ArrayList<>();
        mStudentList = new ArrayList<>();

        // Initialize Firebase instances
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mClassesDatabaseReference = mFirebaseDatabase.getReference().child("students");

        // Find a reference to the recyclerview
        mRecyclerView = findViewById(R.id.student_recycler_view);

        // Set a layoutmanager to the recyclerview
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Use FirebaseRecyclerAdapter to populate the RecyclerView
        FirebaseRecyclerOptions<Student> options = new FirebaseRecyclerOptions.Builder<Student>()
                .setQuery(studentDatabaseReference, Student.class)
                .build();

        // Initialize adapter
        //mAdapter = new StudentAdapterAlt(options);

        // Set adapter to recycler view
        //mRecyclerView.setAdapter(mAdapter);

        // Get list of students and extract list of names
        //mStudentList = mAdapter.getSnapshots();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // TODO can move to oncreate?
        // Initialize adapter and set to Recycler View
        mAdapter = new StudentAdapter();
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setClassData(mStudentList);

        // Use this data to download relevant SchoolClass objects from Firebase Database
        // Initialize Firebase instances
        mClassesDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Student student = dataSnapshot.getValue(Student.class);
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
        });
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
//                // Clear list first
//                mNameList.clear();
//                mMatchedStudentList.clear();
//
//                // Loop through all students to get list of names
//                for (int i = 0; i < mStudentList.size(); i++) {
//                    Student student = mStudentList.get(i);
//                    String name = student.getName();
//                    if (name.contains(query.toLowerCase().trim())) {
//                        mNameList.add(WordUtils.capitalizeFully(name));
//                        mMatchedStudentList.add(student);
//                    }
//                }
//
//                // Show a list of names that could match
//                showNameMatches();
//
//                // Need to clear focus otherwise onQueryTextSubmit runs twice
//                searchView.clearFocus();
//                return true;
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

    // Helper method for show list of student names that match the search query
    private void showNameMatches() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.name_matcher_list, null);
        dialog.setView(view);
        dialog.setTitle("Matches");

        ListView matchesListView = view.findViewById(R.id.name_matcher_list_view);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, mNameList);
        matchesListView.setAdapter(adapter);

        matchesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Student student = mMatchedStudentList.get(position);
                openStudentProfile(student);
            }
        });

        dialog.show();
    }

    // Helper method for opening student profile for student clicked on
    private void openStudentProfile(Student student) {
        // Use intent to open the Student Profile activity
        Intent intent = new Intent(this, StudentProfile.class);
        intent.putExtra(StudentAdapterAlt.STUDENT_ID_EXTRA_KEY, student.getStudentId());
        startActivity(intent);
    }

    // When the FAB is clicked, take the user to the Edit StudentLocalDatabase Info page
    public void openEditStudentInfo(View view) {
        Intent intent = new Intent(this, EditStudentInfo.class);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mStudentList.clear();
        mAdapter.setClassData(null);
    }
}