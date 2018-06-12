package com.example.android.classscheduler;

import android.app.AlertDialog;
import android.content.Intent;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class StudentListActivity extends AppCompatActivity {

    // Member variables
    private StudentAdapter mAdapter;
    private RecyclerView mRecyclerView;

    // List of Student Objects
    private List<Student> mStudentList;
    private List<Student> mMatchedStudentList;
    private List<String> mNameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_list);

        // Set up Timber
        Timber.plant(new Timber.DebugTree());

        // Declare and initialize Firebase references
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference studentDatabaseReference = firebaseDatabase.getReference().child("students");

        // Change the title to "Students"
        setTitle(getString(R.string.students));

        // New list to contain student names
        mNameList = new ArrayList<>();
        mMatchedStudentList = new ArrayList<>();

        // Find a reference to the recyclerview
        mRecyclerView = findViewById(R.id.student_recycler_view);

        // Set a layoutmanager to the recyclerview
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Use FirebaseRecyclerAdapter to populate the RecyclerView
        FirebaseRecyclerOptions<Student> options = new FirebaseRecyclerOptions.Builder<Student>()
                .setQuery(studentDatabaseReference, Student.class)
                .build();

        // Initialize adapter
        mAdapter = new StudentAdapter(options);

        // Set adapter to recycler view
        mRecyclerView.setAdapter(mAdapter);

        // Get list of students and extract list of names
        mStudentList = mAdapter.getSnapshots();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_list_menu, menu);

        // Set SearchView
        MenuItem search = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Clear list first
                mNameList.clear();
                mMatchedStudentList.clear();

                // Loop through all students to get list of names
                for (int i = 0; i < mStudentList.size(); i++) {
                    Student student = mStudentList.get(i);
                    String name = student.getName();
                    if (name.contains(query.toLowerCase().trim())) {
                        mNameList.add(WordUtils.capitalizeFully(name));
                        mMatchedStudentList.add(student);
                    }
                }

                // Show a list of names that could match
                showNameMatches();

                // Need to clear focus otherwise onQueryTextSubmit runs twice
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }

        });
        return true;
    }

    // Helper method for show list of student names that match the search query
    // TODO 3 add pictures to better differentiate students? maybe in the future
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
        // Use intent to open the StudentLocalDatabase Profile activity
        Intent intent = new Intent(this, StudentProfile.class);
        intent.putExtra(StudentAdapter.STUDENT_ID_EXTRA_KEY, student.getStudentId());
        startActivity(intent);
    }

    // When the FAB is clicked, take the user to the Edit StudentLocalDatabase Info page
    public void openEditStudentInfo(View view) {
        Intent intent = new Intent(this, EditStudentInfo.class);
        startActivity(intent);
    }

    // Listens for database changes and populates the adapter
    @Override
    protected void onStart() {
        super.onStart();
        mAdapter.startListening();
    }

    // Stops listening for database changes and clears the adapter
    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }
}