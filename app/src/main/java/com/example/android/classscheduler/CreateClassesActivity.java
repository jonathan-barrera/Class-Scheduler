package com.example.android.classscheduler;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android.classscheduler.Model.SchoolClass;
import com.example.android.classscheduler.Model.Student;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CreateClassesActivity extends AppCompatActivity {

    // Constants
    private static final int SUNDAY_INT = 1;
    private static final int MONDAY_INT = 2;
    private static final int TUESDAY_INT = 3;
    private static final int WEDNESDAY_INT = 4;
    private static final int THURSDAY_INT = 5;
    private static final int FRIDAY_INT = 6;
    private static final int SATURDAY_INT = 7;

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
    @BindView(R.id.sunday_schedule_time_text_view)
    TextView mSundayTimeTextView;
    @BindView(R.id.monday_schedule_time_text_view)
    TextView mMondayTimeTextView;
    @BindView(R.id.tuesday_schedule_time_text_view)
    TextView mTuesdayTimeTextView;
    @BindView(R.id.wednesday_schedule_time_text_view)
    TextView mWednesdayTimeTextView;
    @BindView(R.id.thursday_schedule_time_text_view)
    TextView mThursdayTimeTextView;
    @BindView(R.id.friday_schedule_time_text_view)
    TextView mFridayTimeTextView;
    @BindView(R.id.saturday_schedule_time_text_view)
    TextView mSaturdayTimeTextView;
    @BindView(R.id.title_edit_text)
    EditText mTitleEditText;
    @BindView(R.id.subject_edit_text)
    EditText mSubjectEditText;
    @BindView(R.id.teacher_edit_text)
    EditText mTeacherEditText;

    // Member variables
    private int mStartTimeHour;
    private String mStartTimeMinute;
    private int mEndTimeHour;
    private String mEndTimeMinute;
    private int mCurrentSelectedDay;

    // Firebase Instances
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    // Variables to build a SchoolClass object
    private String mTitle;
    private String mSubject;
    private String mTeacher;
    private String mClassId;
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

        // Initialize Firebase instances
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("classes");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_classes_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_save_class) {
            saveNewClass();
            return true;
        }
        return false;
    }

    // Helper method to save the new SchoolClass object
    private void saveNewClass() {
        // First check if the user's input is valid
        if (!checkUserInputValidity()) {
            return;
        }

        // Create new SchoolClass object
        SchoolClass schoolClass = new SchoolClass(mTitle, mSubject, mTeacher, mClassTimesList);

        // Save to Firebase database
        mDatabaseReference.child(mTitle).setValue(schoolClass);

        // Close activity
        finish();
    }

    // Helper method for validating the user's input before saving a new SchoolClass object
    private boolean checkUserInputValidity() {
        mTitle = mTitleEditText.getText().toString().trim();
        mSubject = mSubjectEditText.getText().toString().trim();
        mTeacher = mTeacherEditText.getText().toString().trim();

        if (TextUtils.isEmpty(mTitle)) {
            Toast.makeText(this, "Please enter a valid title.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(mSubject)) {
            Toast.makeText(this, "Please enter a valid subject.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(mTeacher)) {
            Toast.makeText(this, "Please enter a valid teacher.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (mClassTimesList.size() == 0) {
            Toast.makeText(this, "Please enter schedule times.", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
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

                    // Properly format minutes from 0-9
                    if (minute >= 0 && minute < 10) {
                        mStartTimeMinute = "0" + minute;
                    } else {
                        mStartTimeMinute = String.valueOf(minute);
                    }

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

                    // Properly format minutes 0-9
                    if (minute >= 0 && minute < 10) {
                        mEndTimeMinute = "0" + minute;
                    } else {
                        mEndTimeMinute = String.valueOf(minute);
                    }

                    // Make a string to hold the schedule info
                    String chosenTimeAndDay = mCurrentSelectedDay + "/" + mStartTimeHour + ":" +
                            mStartTimeMinute + "/" + mEndTimeHour + ":" + mEndTimeMinute;

                    // Add string to list of strings containing all class times for this class
                    mClassTimesList.add(chosenTimeAndDay);

                    // Change the text view to reflect the time slot
                    updateTimeSlotTextView();

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

    // Helper method to update the text view for a checkbox to show the chosen time slot
    private void updateTimeSlotTextView() {
        String timeInterval = formatTimeInterval();
        switch (mCurrentSelectedDay) {
            case SUNDAY_INT:
                mSundayTimeTextView.setText(timeInterval);
                break;
            case MONDAY_INT:
                mMondayTimeTextView.setText(timeInterval);
                break;
            case TUESDAY_INT:
                mTuesdayTimeTextView.setText(timeInterval);
                break;
            case WEDNESDAY_INT:
                mWednesdayTimeTextView.setText(timeInterval);
                break;
            case THURSDAY_INT:
                mThursdayTimeTextView.setText(timeInterval);
                break;
            case FRIDAY_INT:
                mFridayTimeTextView.setText(timeInterval);
                break;
            case SATURDAY_INT:
                mSundayTimeTextView.setText(timeInterval);
                break;
            default:
                throw new IllegalArgumentException("Invalid Day of the Week integer: " +
                        mCurrentSelectedDay);
        }
    }

    // Helper method for formatting the time interval of a class
    private String formatTimeInterval() {
        int startTimeHour = mStartTimeHour;
        String startTimeMin = String.valueOf(mStartTimeMinute);
        int endTimeHour = mEndTimeHour;
        String endTimeMin = String.valueOf(mEndTimeMinute);
        String timeOfDayStart = "am";
        String timeOfDayEnd = "am";

        // Format starting hour
        if (mStartTimeHour == 0) {
            startTimeHour = 12;
        } else if (mStartTimeHour > 12) {
            startTimeHour = mStartTimeHour - 12;
            timeOfDayStart = "pm";
        } else if (mStartTimeHour == 12) {
            timeOfDayStart = "pm";
        }

        // Format ending hour
        if (mEndTimeHour == 0) {
            endTimeHour = 12;
        } else if (mEndTimeHour > 12) {
            endTimeHour = mEndTimeHour - 12;
            timeOfDayEnd = "pm";
        } else if (mEndTimeHour == 12) {
            timeOfDayEnd = "pm";
        }

        return startTimeHour + ":" + startTimeMin + timeOfDayStart + " - " +
                endTimeHour + ":" + endTimeMin + timeOfDayEnd;
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
                mCurrentSelectedDay = SUNDAY_INT;
                break;
            case R.id.monday_schedule_check_box:
                mCurrentSelectedDay = MONDAY_INT;
                break;
            case R.id.tuesday_schedule_check_box:
                mCurrentSelectedDay = TUESDAY_INT;
                break;
            case R.id.wednesday_schedule_check_box:
                mCurrentSelectedDay = WEDNESDAY_INT;
                break;
            case R.id.thursday_schedule_check_box:
                mCurrentSelectedDay = THURSDAY_INT;
                break;
            case R.id.friday_schedule_check_box:
                mCurrentSelectedDay = FRIDAY_INT;
                break;
            case R.id.saturday_schedule_check_box:
                mCurrentSelectedDay = SATURDAY_INT;
                break;
            default:
                throw new IllegalArgumentException("Invalid View selected: " + view.getId());
        }
        if (view == mSundayCheckBox) {
            mCurrentSelectedDay = 1;
        }
    }
}
