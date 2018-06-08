package com.example.android.classscheduler;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.classscheduler.Model.StudentLocalDatabase;
import com.example.android.classscheduler.data.StudentContract.StudentEntry;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class StudentLocalDatabaseAdapter extends RecyclerView.Adapter<StudentLocalDatabaseAdapter.StudentAdapterViewHolder> {

    // List of StudentLocalDatabase objects to return
    private List<StudentLocalDatabase> mStudentLocalDatabaseData;
    private StudentAdapterOnClickHandler mClickHandler;

    // Interface for handling onclick events
    public interface StudentAdapterOnClickHandler {
        void onClick(StudentLocalDatabase studentLocalDatabase);
    }

    // Create a constructor to instantiate the clickhandler
    public StudentLocalDatabaseAdapter(StudentAdapterOnClickHandler clickHandler) { mClickHandler = clickHandler;}

    @Override
    public StudentAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Timber.d("oncreateviewholder called");
        // Inflate the list item layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_list_item, parent,
                false);
        return new StudentAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentLocalDatabaseAdapter.StudentAdapterViewHolder holder, int position) {
        Timber.d("onbindviewholder called");
        // For each student object in the List<StudentLocalDatabase> get the relevant data
        StudentLocalDatabase currentStudentLocalDatabase = mStudentLocalDatabaseData.get(position);
        String studentName = currentStudentLocalDatabase.getStudentName();
        int studentSex = currentStudentLocalDatabase.getStudentSex();
        int studentAge = currentStudentLocalDatabase.getStudentAge();
        byte[] studentPictureByteArray = currentStudentLocalDatabase.getStudentPicture();

        String studentSexString;
        if (studentSex == StudentEntry.SEX_MALE) {
            studentSexString = holder.itemView.getContext().getResources().getString(R.string.male);
        } else {
            studentSexString = holder.itemView.getContext().getResources().getString(R.string.female);
        }

        // Populate the textviews with the data
        holder.studentNameTV.setText(studentName);
        holder.studentSexTV.setText(studentSexString);
        holder.studentAgeTV.setText(String.valueOf(studentAge));

        // Put a student picture if it exist
        if (studentPictureByteArray != null) {
            Bitmap studentPicture = BitmapUtils.byteArrayToBitmap(studentPictureByteArray);
            holder.studentPictureIV.setImageBitmap(studentPicture);
        } else {
            // TODO Fix the discrepancy in profile picture sizes (pic vs no pic)
            holder.studentPictureIV.setImageResource(R.drawable.ic_baseline_account_circle_48px);
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
            StudentLocalDatabase studentLocalDatabase = mStudentLocalDatabaseData.get(position);
            mClickHandler.onClick(studentLocalDatabase);
        }
    }

    @Override
    public int getItemCount() {
        if (mStudentLocalDatabaseData == null) {
            return 0;
        }
        else {
            return mStudentLocalDatabaseData.size();
        }
    }

    public void setStudentData(List<StudentLocalDatabase> studentLocalDatabaseList) {
        Timber.d("setstudentdata called");
        // Set the studentLocalDatabaseList to mStudentLocalDatabaseData
        mStudentLocalDatabaseData = studentLocalDatabaseList;
        notifyDataSetChanged();
    }
}
