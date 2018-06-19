package com.example.android.classscheduler;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import timber.log.Timber;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        // Set up Timber
        Timber.plant(new Timber.DebugTree());

        // Set the title
        setTitle(getString(R.string.main_menu));
    }

    // Method for opening the student list activity
    public void openStudentListActivity(View v) {
        Intent intent = new Intent(this, StudentListActivity.class);
        startActivity(intent);
    }

    // Method for opening the class list activity
    public void openClassListActivity(View v) {
        Intent intent = new Intent(this, ClassListActivity.class);
        startActivity(intent);
    }
}
