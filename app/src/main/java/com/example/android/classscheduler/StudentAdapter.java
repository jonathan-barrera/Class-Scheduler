package com.example.android.classscheduler;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.classscheduler.Model.Student;
import com.example.android.classscheduler.data.StudentContract;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by jonathanbarrera on 6/7/18.
 */

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentAdapterViewHolder> {

    // List of Student objects to return
    private List<Student> mStudentData;
    private StudentAdapterOnClickHandler mClickHandler;

    // Interface for handling onclick events
    public interface StudentAdapterOnClickHandler {
        void onClick(Student student);
    }

    // Create a constructor to instantiate the clickhandler
    public StudentAdapter(StudentAdapterOnClickHandler clickHandler) { mClickHandler = clickHandler;}

    @Override
    public StudentAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Timber.d("oncreateviewholder called");
        // Inflate the list item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_list_item, parent,
                false);
        return new StudentAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentAdapter.StudentAdapterViewHolder holder, int position) {
        Timber.d("onbindviewholder called");
        // For each student object in the List<Student> get the relevant data
        Student currentStudent = mStudentData.get(position);
        String studentName = currentStudent.getName();
        int studentSex = currentStudent.getSex();
        int studentAge = currentStudent.getAge();
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
        holder.studentAgeTV.setText(String.valueOf(studentAge));

        // Put a student picture if it exist
        if (!TextUtils.isEmpty(studentPictureUrl)) {
            Glide.with(holder.studentPictureIV.getContext())
                    .load(studentPictureUrl)
                    .into(holder.studentPictureIV);
        } else {
            holder.studentPictureIV.setImageResource(R.drawable.ic_baseline_account_circle_24px);
        }
    }

    // Create the view holder that will be used in the recycler view
    public class StudentAdapterViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        @BindView(R.id.student_name_text_view) TextView studentNameTV;
        @BindView(R.id.student_sex_text_view) TextView studentSexTV;
        @BindView(R.id.student_age_text_view) TextView studentAgeTV;
        @BindView(R.id.student_picture_image_view) ImageView studentPictureIV;

        public StudentAdapterViewHolder(View itemView) {
            super(itemView);

            // Bind views with Butterknife
            ButterKnife.bind(this, itemView);

            // Set OnClickListener
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            Student student = mStudentData.get(position);
            mClickHandler.onClick(student);
        }
    }

    @Override
    public int getItemCount() {
        if (mStudentData == null) {
            return 0;
        }
        else {
            return mStudentData.size();
        }
    }

    public void setStudentData(List<Student> studentList) {
        Timber.d("setstudentdata called");
        // Set the studentList to mStudentData
        mStudentData = studentList;
        notifyDataSetChanged();
    }
}