package com.example.android.classscheduler;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.android.classscheduler.Model.Student;
import com.example.android.classscheduler.Model.StudentLocalDatabase;
import com.example.android.classscheduler.data.StudentContract;
import com.example.android.classscheduler.data.StudentContract.StudentEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StudentProfile extends AppCompatActivity {

    // Member variables
    private Student mCurrentStudent;

    // Views
    @BindView(R.id.student_profile_picture_view)
    ImageView mStudentPictureView;
    @BindView(R.id.student_profile_name_view)
    TextView mStudentNameView;
    @BindView(R.id.student_profile_sex_view)
    TextView mStudentSexView;
    @BindView(R.id.student_profile_age_view)
    TextView mStudentAgeView;
    @BindView(R.id.student_profile_grade_view)
    TextView mStudentGradeView;
    @BindView(R.id.student_profile_classes_view)
    TextView mStudentClassesView;
    @BindView(R.id.student_profile_plus_image_view)
    ImageView mPlusImageView;
    @BindView(R.id.student_profile_add_photo_text_view)
    TextView mAddPhotoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        // Set title
        setTitle(getString(R.string.student_profile));

        // Bind views
        ButterKnife.bind(this);

        // Retrieve the data sent with the intent
        Intent intent = getIntent();
        mCurrentStudent = intent.getParcelableExtra(StudentListActivity.STUDENT_EXTRA_KEY);

        //Populate the views with student data
        populateViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.student_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.edit_action:
                // Intent to open EditStudentInfo activity
                Intent intent = new Intent(this, EditStudentInfo.class);
                intent.putExtra(StudentListActivity.STUDENT_EXTRA_KEY, mCurrentStudent);
                startActivity(intent);
                break;
            case R.id.delete_action:
                //showDeleteDialog();
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                throw new IllegalArgumentException("Invalid Option Selected.");
        }
        return super.onOptionsItemSelected(item);
    }

    // Prompt user to confirm student deletion
//    private void showDeleteDialog() {
//        // Create an AlertDialog.Builder and set the message and click listeners for the positive
//        // and negative buttons.
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage("Are you sure you want to permanently delete this student?");
//
//        // Delete
//        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                deleteStudent();
//            }
//        });
//
//        // Don't delete
//        builder.setNegativeButton("Return", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (dialog != null) {
//                    dialog.dismiss();
//                }
//            }
//        });
//
//        // Create and show AlertDialog
//        AlertDialog alertDialog = builder.create();
//        alertDialog.show();
//    }

//    // Delete student from the database
//    private void deleteStudent() {
//        Uri.Builder currentStudentUri = new Uri.Builder();
//        Uri deletionUri = currentStudentUri.scheme("content")
//                .authority(StudentContract.CONTENT_AUTHORITY)
//                .appendPath(StudentContract.PATH_STUDENTS)
//                .appendPath(String.valueOf(mCurrentStudentLocalDatabase.getStudentId()))
//                .build();
//        int rowsDeleted = getContentResolver().delete(deletionUri, null, null);
//
//        String toastMessage;
//        if (rowsDeleted == 0) {
//            toastMessage = "Deletion failed";
//        } else {
//            toastMessage = "StudentLocalDatabase successfully deleted";
//        }
//        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
//
//        // Close activity
//        finish();
//    }

    private void populateViews() {
        // Extract data from the StudentLocalDatabase object
        String name = mCurrentStudent.getName();
        int sex = mCurrentStudent.getSex();
        int age = mCurrentStudent.getAge();
        int grade = mCurrentStudent.getGrade();
        String classes = mCurrentStudent.getClasses();
        String photoUrl = mCurrentStudent.getPhotoUrl();

        // Populate views
        mStudentNameView.setText(name);
        mStudentAgeView.setText(String.valueOf(age));
        mStudentGradeView.setText(String.valueOf(grade));
        if (TextUtils.isEmpty(classes)) {
            classes = getString(R.string.no_classes_added);
            mStudentClassesView.setTypeface(mStudentClassesView.getTypeface(), Typeface.ITALIC);
        }
        mStudentClassesView.setText(classes);

        String sexString;
        if (sex == StudentEntry.SEX_MALE) {
            sexString = getString(R.string.male);
        } else if (sex == StudentEntry.SEX_FEMALE) {
            sexString = getString(R.string.female);
        } else {
            throw new IllegalArgumentException("Invalid sex");
        }
        mStudentSexView.setText(sexString);

        // Only show the student pic if it exists
        if (!TextUtils.isEmpty(photoUrl)) {
            Glide.with(mStudentPictureView.getContext())
                    .load(photoUrl)
                    .into(mStudentPictureView);

            // Hide the Add Photo image and text
            mPlusImageView.setVisibility(View.GONE);
            mAddPhotoTextView.setVisibility(View.GONE);
        }

    }

}
