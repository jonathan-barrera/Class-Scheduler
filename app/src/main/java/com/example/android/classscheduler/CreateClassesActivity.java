package com.example.android.classscheduler;

import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateClassesActivity extends AppCompatActivity {

    // Views
    @BindView(R.id.sunday_schedule_check_box)
    CheckBox mSundayCheckBox;
    @BindView(R.id.monday_schedule_check_box)
    CheckBox mMondayCheckBox;
    @BindView(R.id.tuesday_schedule_check_box)
    CheckBox mTuesdayCheckBox;
    @BindView(R.id.wednesday_schedule_check_box)
    CheckBox mWednesdayCheckBox;
    @BindView(R.id.thursday_schedule_check_box)
    CheckBox mThursdayCheckBox;
    @BindView(R.id.friday_schedule_check_box)
    CheckBox mFridayCheckBox;
    @BindView(R.id.saturday_schedule_check_box)
    CheckBox mSaturdayCheckBox;

    // Member variables
    private int mStartTimeHour;
    private int mStartTimeMinute;
    private int mEndTimeHour;
    private int mEndTimeMinute;
    private int mCurrentSelectedDay;

    // List of strings to hold schedule times
    private List<String> mClassTimesList = new ArrayList<>();

    // Checked Change Listener
    private CompoundButton.OnCheckedChangeListener mCheckedChangeListener =
            new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        getCurrentSelectedDay(buttonView);
                        getStartTime();
                    }
                    // TODO if unchecked, need to delete the time slot for this day
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_classes);

        // Bind views
        ButterKnife.bind(this);

        // Set onchecklisteners on checkboxes
        setOnCheckListeners();
    }

    private void getStartTime() {
        final Calendar myCalender = Calendar.getInstance();
        final int hour = myCalender.get(Calendar.HOUR_OF_DAY);
        int minute = myCalender.get(Calendar.MINUTE);

        TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (view.isShown()) {
                    mStartTimeHour = hourOfDay;
                    mStartTimeMinute = minute;

                    getEndTime();
                }
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(CreateClassesActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, false);
        timePickerDialog.setTitle("Choose Start Time:");
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();
    }

    private void getEndTime() {
        final Calendar myCalender = Calendar.getInstance();
        final int hour = myCalender.get(Calendar.HOUR_OF_DAY);
        int minute = myCalender.get(Calendar.MINUTE);

        TimePickerDialog.OnTimeSetListener myTimeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if (view.isShown()) {
                    mEndTimeHour = hourOfDay;
                    mEndTimeMinute = minute;

                    // Make a string to hold the schedule info
                    String chosenTimeAndDay = mCurrentSelectedDay + "/" + mStartTimeHour + ":" + mStartTimeMinute + "/" + mEndTimeHour + ":" + mEndTimeMinute;

                    // Add string to list of strings containing all class times for this class
                    mClassTimesList.add(chosenTimeAndDay);

                    // TODO need a helper method to stop user from saving multiple times for a day

                    Toast.makeText(CreateClassesActivity.this, chosenTimeAndDay, Toast.LENGTH_SHORT).show();
                }
            }
        };

        TimePickerDialog timePickerDialog = new TimePickerDialog(CreateClassesActivity.this,
                android.R.style.Theme_Holo_Light_Dialog_NoActionBar, myTimeListener, hour, minute, false);
        timePickerDialog.setTitle("Choose End Time:");
        timePickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        timePickerDialog.show();
    }

    // Helper method to set onCheckListeners on each CheckBox
    private void setOnCheckListeners() {
        mSundayCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);
        mMondayCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);
        mTuesdayCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);
        mWednesdayCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);
        mThursdayCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);
        mFridayCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);
        mSaturdayCheckBox.setOnCheckedChangeListener(mCheckedChangeListener);
    }

    // Helper method to determine which day of the week is having its time slot set
    private void getCurrentSelectedDay(View view) {
        switch(view.getId()) {
            case R.id.sunday_schedule_check_box:
                mCurrentSelectedDay = 1;
                break;
            case R.id.monday_schedule_check_box:
                mCurrentSelectedDay = 2;
                break;
            case R.id.tuesday_schedule_check_box:
                mCurrentSelectedDay = 3;
                break;
            case R.id.wednesday_schedule_check_box:
                mCurrentSelectedDay = 4;
                break;
            case R.id.thursday_schedule_check_box:
                mCurrentSelectedDay = 5;
                break;
            case R.id.friday_schedule_check_box:
                mCurrentSelectedDay = 6;
                break;
            case R.id.saturday_schedule_check_box:
                mCurrentSelectedDay = 7;
                break;
            default:
                throw new IllegalArgumentException("Invalid View selected: " + view.getId());
        }
        if (view == mSundayCheckBox) {
            mCurrentSelectedDay = 1;
        }
    }
}
