package com.example.android.classscheduler;

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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.android.classscheduler.Model.SchoolClass;
import com.example.android.classscheduler.Model.Student;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import timber.log.Timber;

/**
 * Activity for showing the full list of classes offered.
 */
public class ClassListActivity extends AppCompatActivity implements SchoolClassAdapter.onItemClickListener {

    // Constants
    public static final String CLASS_OBJECT_FIREBASE_KEY = "class-object-key";

    // List to hold School Class objects
    List<SchoolClass> mClassObjectList;
    List<SchoolClass> mMatchedClassObjectList;

    SchoolClassAdapter mAdapter;

    // Views
    @BindView(R.id.class_list_recycler_view)
    RecyclerView mClassListRecyclerView;

    // Firebase instances
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mClassesDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);

        // Bind views
        ButterKnife.bind(this);

        // Initialize Firebase instances
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mClassesDatabaseReference = mFirebaseDatabase.getReference().child("classes");

        // Get data from intent
        mClassObjectList = new ArrayList<>();
        mMatchedClassObjectList = new ArrayList<>();

        // Set Title
        setTitle(getString(R.string.classes));

        // Set Layout Manager
        mClassListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Initialize adapter and set to Recycler View
        mAdapter = new SchoolClassAdapter();
        mClassListRecyclerView.setAdapter(mAdapter);
        mAdapter.setClassData(mClassObjectList);

        // Use this data to download relevant SchoolClass objects from Firebase Database
        // Initialize Firebase instances
        mClassesDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                SchoolClass schoolClass = dataSnapshot.getValue(SchoolClass.class);
                mClassObjectList.add(schoolClass);
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
        getMenuInflater().inflate(R.menu.class_list_menu, menu);

        // Set SearchView
        MenuItem search = menu.findItem(R.id.action_search_classes);
        final SearchView searchView = (SearchView) search.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
//                // Clear list first
//                mMatchedClassObjectList.clear();
//
//                // Show a list of names that could match
//                mAdapter.setClassData(mMatchedClassObjectList);
//
//                // Loop through all students to get list of matched classes
//                for (int i = 0; i < mClassObjectList.size(); i++) {
//                    SchoolClass schoolClass = mClassObjectList.get(i);
//                    String title = schoolClass.getTitle();
//                    if ((title.toLowerCase()).contains(query.toLowerCase().trim())) {
//                        mMatchedClassObjectList.add(schoolClass);
//                        mAdapter.notifyDataSetChanged();
//                    }
//                }
//
//                // Need to clear focus otherwise onQueryTextSubmit runs twice
//                searchView.clearFocus();
//                return true;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Clear list first
                mMatchedClassObjectList.clear();

                // Show a list of names that could match
                mAdapter.setClassData(mMatchedClassObjectList);

                // Loop through all students to get list of matched classes
                for (int i = 0; i < mClassObjectList.size(); i++) {
                    SchoolClass schoolClass = mClassObjectList.get(i);
                    String title = schoolClass.getTitle();
                    Timber.d(title + "flag");
                    if ((title.toLowerCase()).contains(newText.toLowerCase().trim())) {
                        mMatchedClassObjectList.add(schoolClass);
                        mAdapter.notifyDataSetChanged();
                        Timber.d("mmatchedclassobjectlist size is " + mMatchedClassObjectList.size());
                    }
                }
                return true;
            }

        });
        return true;
    }

    // Method for opening Create Classes Activity
    public void openCreateClassesActivity(View v) {
        Intent intent = new Intent(this, CreateClassesActivity.class);
        startActivity(intent);
    }

    @Override
    public void onClassSelected(int position) {
        Intent intent = new Intent(this, CreateClassesActivity.class);
        SchoolClass schoolClass = mClassObjectList.get(position);
        intent.putExtra(CLASS_OBJECT_FIREBASE_KEY, schoolClass);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mClassObjectList.clear();
        mAdapter.setClassData(null);
    }
}
