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

import com.example.android.classscheduler.model.SchoolClass;
import com.example.android.classscheduler.model.Student;
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

import static com.example.android.classscheduler.StudentListActivity.BUNDLE_RECYCLER_VIEW_KEY;

/**
 * Activity for showing the full list of classes offered.
 */
public class ClassListActivity extends AppCompatActivity implements SchoolClassAdapter.onItemClickListener {

    // Constants
    public static final String CLASS_OBJECT_FIREBASE_KEY = "class-object-key";

    // List to hold School Class objects
    List<SchoolClass> mClassObjectList;
    List<SchoolClass> mMatchedClassObjectList;

    // Member variables
    private SchoolClassAdapter mAdapter;
    private String mUserId;
    private Parcelable mSavedState;

    // Views
    @BindView(R.id.class_list_recycler_view)
    RecyclerView mClassListRecyclerView;

    // Firebase instances
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mClassesDatabaseReference;
    private ValueEventListener mValueEventLister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_list);

        // Bind views
        ButterKnife.bind(this);

        // Get UserId
        SharedPreferences sharedPreferences = getSharedPreferences(MainMenu.SHARED_PREFS, MODE_PRIVATE);
        mUserId = sharedPreferences.getString(MainMenu.USER_ID_SHARED_PREF_KEY, null);

        // Initialize Firebase instances
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mClassesDatabaseReference = mFirebaseDatabase.getReference()
                .child(EditStudentInfo.FIREBASE_CHILD_KEY_USERS)
                .child(mUserId)
                .child(EditStudentInfo.FIREBASE_CHILD_KEY_CLASSES);

        // Get data from intent
        mClassObjectList = new ArrayList<>();
        mMatchedClassObjectList = new ArrayList<>();

        // Set Title
        setTitle(getString(R.string.classes));

        // Set Layout Manager
        mClassListRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize adapter and set to Recycler View
        mAdapter = new SchoolClassAdapter();
        mClassListRecyclerView.setAdapter(mAdapter);
        mAdapter.setClassData(mClassObjectList);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Query firebase database for class info
        if (mValueEventLister == null) {
            mValueEventLister = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                    for (DataSnapshot child : children) {
                        SchoolClass schoolClass = child.getValue(SchoolClass.class);
                        mClassObjectList.add(schoolClass);
                    }

                    mAdapter.notifyDataSetChanged();
                    mClassListRecyclerView.getLayoutManager().onRestoreInstanceState(mSavedState);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            };

            mClassesDatabaseReference.addValueEventListener(mValueEventLister);
        }
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
                    if ((title.toLowerCase()).contains(newText.toLowerCase().trim())) {
                        mMatchedClassObjectList.add(schoolClass);
                        mAdapter.notifyDataSetChanged();
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
    protected void onPause() {
        super.onPause();

        // Clear Class Object List
        mClassObjectList.clear();

        // Detach ValueEventListener
        if (mValueEventLister != null) {
            mClassesDatabaseReference.removeEventListener(mValueEventLister);
            mValueEventLister = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_VIEW_KEY, mClassListRecyclerView.getLayoutManager().
                onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            mSavedState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_VIEW_KEY);
            if (mSavedState != null) {
                mClassListRecyclerView.getLayoutManager().onRestoreInstanceState(mSavedState);
            }

        }
    }
}
