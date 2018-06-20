package com.example.android.classscheduler;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.android.classscheduler.model.SchoolClass;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.text.WordUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ClassDetailsActivity extends AppCompatActivity implements SchoolClassAdapter.onItemClickListener{

    // TODO Database on the phone to save last viewed students/classes?
    // TODO (2) Build the Widget
    // TODO Change all strings to string variables
    // TODO Fix RTL formatting
    // TODO can add a list of enrolled students for each class
    // TODO have the list of classes for a student update in realtime (if one is deleted).
    // TODO check for scrolling/onsavedinstancestate mistakes
    // TODO build a check for network connection
    // TODO need to do signing configuration stuff
    // TODO accessibility (content descriptions, etc.)


    // List to hold list of chosen class titles
    List<String> mChosenClassList;

    // List to hold School Class objects
    List<SchoolClass> mClassObjectList;

    // Views
    @BindView(R.id.class_details_recycler_view)
    RecyclerView mClassDetailsRecyclerView;

    // Firebase instances
    private FirebaseDatabase mFirebaseDatabase;

    private String mUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_details);

        // Bind views
        ButterKnife.bind(this);

        // Get UserId
        SharedPreferences sharedPreferences = getSharedPreferences(MainMenu.SHARED_PREFS, MODE_PRIVATE);
        mUserId = sharedPreferences.getString(MainMenu.USER_ID_SHARED_PREF_KEY, null);

        // Initialize Firebase instances
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference classesDatabaseReference = mFirebaseDatabase.getReference()
                .child("users")
                .child(mUserId)
                .child("classes");

        // Get data from intent
        Intent intent = getIntent();
        mChosenClassList = intent.getStringArrayListExtra(StudentProfile.CLASS_LIST_EXTRA_KEY);
        mClassObjectList = new ArrayList<>();

        // Set Title
        String title = WordUtils.capitalizeFully(intent
                .getStringExtra(StudentProfile.STUDENT_NAME_EXTRA_KEY)) + "'s Classes";
        setTitle(title);

        // Set Layout Manager
        mClassDetailsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter and set to Recycler View
        final SchoolClassAdapter schoolClassAdapter = new SchoolClassAdapter();
        mClassDetailsRecyclerView.setAdapter(schoolClassAdapter);
        schoolClassAdapter.setClassData(mClassObjectList);

        // Use this data to download relevant SchoolClass objects from Firebase Database
        // Initialize Firebase instances
        classesDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                SchoolClass schoolClass = dataSnapshot.getValue(SchoolClass.class);
                if (mChosenClassList.contains(schoolClass.getTitle())) {
                    mClassObjectList.add(schoolClass);
                    schoolClassAdapter.notifyDataSetChanged();
                }
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
    public void onClassSelected(int position) {
    }
}
