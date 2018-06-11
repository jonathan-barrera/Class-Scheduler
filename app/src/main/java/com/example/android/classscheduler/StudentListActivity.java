package com.example.android.classscheduler;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.android.classscheduler.Model.Student;
import com.example.android.classscheduler.data.StudentContract;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
    public static final String STUDENT_ID_EXTRA_KEY = "student-id-extra";

    // Member variables
    private FirebaseRecyclerAdapter mAdapter;
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
        mAdapter = new FirebaseRecyclerAdapter<Student, ViewHolder>(options) {
            @NonNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.student_list_item, parent, false);

                return new ViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ViewHolder holder, int position,
                                            @NonNull final Student currentStudent) {
                // Extract all of the information for the current student
                String studentName = currentStudent.getName();
                int studentSex = currentStudent.getSex();
                long studentBirthdate = currentStudent.getBirthdate();
                String studentPictureUrl = currentStudent.getPhotoUrl();

                String studentSexString;
                if (studentSex == StudentContract.StudentEntry.SEX_MALE) {
                    studentSexString = holder.itemView.getContext().getResources().getString(R.string.male);
                } else {
                    studentSexString = holder.itemView.getContext().getResources().getString(R.string.female);
                }

                // Populate the textviews with the data
                holder.studentNameTV.setText(studentName);
                holder.studentSexTV.setText(studentSexString);
                holder.studentAgeTV.setText(DateUtils.getAge(studentBirthdate));

                // Put a student picture if it exist
                if (!TextUtils.isEmpty(studentPictureUrl)) {
                    Glide.with(holder.studentPictureIV.getContext())
                            .load(studentPictureUrl)
                            .into(holder.studentPictureIV);
                } else {
                    holder.studentPictureIV.setImageResource(R.drawable.ic_baseline_account_circle_24px);
                }

                // Set onclicklistener
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Open the student profile for the student that was clicked on
                        openStudentProfile(currentStudent);
                    }
                });
            }
        };

        // Set adapter to recycler view
        mRecyclerView.setAdapter(mAdapter);
    }

    // When the FAB is clicked, take the user to the Edit StudentLocalDatabase Info page
    public void openEditStudentInfo(View view) {
        Intent intent = new Intent(this, EditStudentInfo.class);
        startActivity(intent);
    }

    // Helper method for opening student profile for student clicked on
    private void openStudentProfile(Student student) {
        // Use intent to open the StudentLocalDatabase Profile activity
        Intent intent = new Intent(this, StudentProfile.class);
        intent.putExtra(STUDENT_ID_EXTRA_KEY, student.getStudentId());
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

//    private void attachDatabaseReadListener() {
//        if (mChildEventListener == null) {
//            mChildEventListener = new ChildEventListener() {
//                @Override
//                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                    Student student = dataSnapshot.getValue(Student.class);
//                    mStudentList.add(student);
//                    mAdapter.setStudentData(mStudentList);
//                }
//
//                @Override
//                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                }
//
//                @Override
//                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//                }
//
//                @Override
//                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError databaseError) {
//                }
//            };
//            mDatabaseReference.addChildEventListener(mChildEventListener);
//        }
//    }
//
//    private void detachDatabaseReadListener() {
//        if (mChildEventListener != null) {
//            mDatabaseReference.removeEventListener(mChildEventListener);
//            mChildEventListener = null;
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        detachDatabaseReadListener();
//        mStudentList.clear();
//        mAdapter.setStudentData(null);
//    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        attachDatabaseReadListener();
//    }