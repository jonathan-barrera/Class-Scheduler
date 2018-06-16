package com.example.android.classscheduler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.classscheduler.Model.SchoolClass;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ClassDetailsActivity extends AppCompatActivity {

    // TODO Function to remove classes
    // TODO Function to edit classes
    // TODO Database on the phone to save last viewed students/classes?
    // TODO (2) Build the Widget
    // TODO Change all strings to string variables
    // TODO Fix RTL formatting
    // TODO (1) Build Firebase Authorization
    // TODO (2) Have this authorization be linked to some "school/organization"?

    // List to hold list of chosen class titles
    List<String> mChosenClassList;

    // List to hold School Class objects
    List<SchoolClass> mClassObjectList;

    // Views
    @BindView(R.id.class_details_recycler_view)
    RecyclerView mClassDetailsRecyclerView;

    // Firebase instances
    private FirebaseDatabase mFirebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        // Bind views
        ButterKnife.bind(this);

        // Set Title
        setTitle("Class Details");

        // Initialize Firebase instances
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference classesDatabaseReference = mFirebaseDatabase.getReference().child("classes");

        // Get data from intent
        mChosenClassList = getIntent().getStringArrayListExtra(StudentProfile.CLASS_LIST_EXTRA_KEY);
        mClassObjectList = new ArrayList<>();

        // Set Layout Manager
        mClassDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter and set to Recycler View
        final SchoolClassAdapter schoolClassAdapter = new SchoolClassAdapter();
        mClassDetailsRecyclerView.setAdapter(schoolClassAdapter);

        // Use this data to download relevant SchoolClass objects from Firebase Database
        // Initialize Firebase instances
        classesDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                SchoolClass schoolClass = dataSnapshot.getValue(SchoolClass.class);
                if (mChosenClassList.contains(schoolClass.getTitle())) {
                    mClassObjectList.add(schoolClass);
                }
                schoolClassAdapter.setClassData(mClassObjectList);
                Timber.d("size of list is " + mChosenClassList.size());
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
}
