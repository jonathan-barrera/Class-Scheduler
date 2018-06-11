package com.example.android.classscheduler;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.android.classscheduler.Model.Student;
import com.example.android.classscheduler.data.StudentContract;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

public class StudentListActivity extends AppCompatActivity {

    // Member variables
    //private FirebaseRecyclerAdapter mAdapter;
    private StudentAdapter mAdapter;
    private RecyclerView mRecyclerView;

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

        // Find a reference to the recyclerview
        mRecyclerView = findViewById(R.id.student_recycler_view);

        // Set a layoutmanager to the recyclerview
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Use FirebaseRecyclerAdapter to populate the RecyclerView
        FirebaseRecyclerOptions<Student> options = new FirebaseRecyclerOptions.Builder<Student>()
                .setQuery(studentDatabaseReference, Student.class)
                .build();

        mAdapter = new StudentAdapter(options);
//        mAdapter = new FirebaseRecyclerAdapter<Student, ViewHolder>(options) {
//            @NonNull
//            @Override
//            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//                View view = LayoutInflater.from(parent.getContext())
//                        .inflate(R.layout.student_list_item, parent, false);
//
//                return new ViewHolder(view);
//            }
//
//            @Override
//            protected void onBindViewHolder(@NonNull ViewHolder holder, int position,
//                                            @NonNull final Student currentStudent) {
//                // Extract all of the information for the current student
//                String studentName = currentStudent.getName();
//                int studentSex = currentStudent.getSex();
//                long studentBirthdate = currentStudent.getBirthdate();
//                String studentPictureUrl = currentStudent.getPhotoUrl();
//
//                String studentSexString;
//                if (studentSex == StudentContract.StudentEntry.SEX_MALE) {
//                    studentSexString = holder.itemView.getContext().getResources().getString(R.string.male);
//                } else {
//                    studentSexString = holder.itemView.getContext().getResources().getString(R.string.female);
//                }
//
//                // Populate the textviews with the data
//                holder.studentNameTV.setText(studentName);
//                holder.studentSexTV.setText(studentSexString);
//                holder.studentAgeTV.setText(DateUtils.getAge(studentBirthdate));
//
//                // Put a student picture if it exist
//                if (!TextUtils.isEmpty(studentPictureUrl)) {
//                    Glide.with(holder.studentPictureIV.getContext())
//                            .load(studentPictureUrl)
//                            .into(holder.studentPictureIV);
//                } else {
//                    holder.studentPictureIV.setImageResource(R.drawable.ic_baseline_account_circle_24px);
//                }
//
//                // Set onclicklistener
//                holder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        // Open the student profile for the student that was clicked on
//                        openStudentProfile(currentStudent);
//                    }
//                });
//            }
//        };

        // Set adapter to recycler view
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_list_menu, menu);

        // Set SearchView
        MenuItem search = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
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