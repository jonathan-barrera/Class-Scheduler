package com.example.android.classscheduler;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.classscheduler.model.SchoolClass;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.android.classscheduler.DateUtils.getDayOfTheWeek;
import static com.example.android.classscheduler.DateUtils.getFormattedTime;

/**
 * Created by jonathanbarrera on 6/15/18.
 * RecyclerView Adapter for SchoolClass objects
 */

public class SchoolClassAdapter extends RecyclerView.Adapter<SchoolClassAdapter.SchoolClassAdapterViewHolder> {

    // Member variables
    private List<SchoolClass> mClassData;
    onItemClickListener mCallback;

    // Interface for dealing with click events
    public interface onItemClickListener {
        void onClassSelected(int position);
    }

    @NonNull
    @Override
    public SchoolClassAdapter.SchoolClassAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the list item view
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.class_details_list_item, parent, false);

        // Initialize mCallback
        mCallback = (onItemClickListener) parent.getContext();

        return new SchoolClassAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SchoolClassAdapter.SchoolClassAdapterViewHolder holder, final int position) {
        // Extract information from the SchoolClass object
        SchoolClass currentClass = mClassData.get(position);
        String title = currentClass.getTitle();
        String subject = "Subject: " + currentClass.getSubject();
        String teacher = "Teacher: " + currentClass.getTeacher();
        List<String> scheduleList = currentClass.getSchedule();
        String schedule = formatSchedule(scheduleList);

        // Set above information to textviews
        holder.mClassTitleTextView.setText(title);
        holder.mClassSubjectTextView.setText(subject);
        holder.mClassTeacherTextView.setText(teacher);
        holder.mClassScheduleTextView.setText(schedule);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onClassSelected(position);
            }
        });
    }

    // Helper method for formatting the schedule string for the schedule text view
    private String formatSchedule(List<String> scheduleList) {
        List<String> formattedScheduleList = new ArrayList<>();

        // Loop through each schedule item (which should be a day) and add to the list
        for (int i = 0; i < scheduleList.size(); i++) {
            String string = scheduleList.get(i);
            String[] scheduleParts = string.split("/");

            // Get day of the week
            String dayOfWeek = getDayOfTheWeek(scheduleParts[0]);

            // Get start time
            String startTime = getFormattedTime(scheduleParts[1]);
            String endTime = getFormattedTime(scheduleParts[2]);

            // Get the final schedule string and add to list
            String schedule = dayOfWeek + " " + startTime + "-" + endTime;
            formattedScheduleList.add(schedule);
        }

        // Once the list has been finalized, turn into one string and return;
        String scheduleString = TextUtils.join("\n", formattedScheduleList);
        return scheduleString;
    }

    @Override
    public int getItemCount() {
        if (mClassData == null) {
            return 0;
        } else {
            return mClassData.size();
        }
    }

    public class SchoolClassAdapterViewHolder extends RecyclerView.ViewHolder {

        // Views
        @BindView(R.id.class_details_title_text_view)
        TextView mClassTitleTextView;
        @BindView(R.id.class_details_subject_text_view)
        TextView mClassSubjectTextView;
        @BindView(R.id.class_details_teacher_text_view)
        TextView mClassTeacherTextView;
        @BindView(R.id.class_details_schedule_text_view)
        TextView mClassScheduleTextView;

        public SchoolClassAdapterViewHolder(View itemView) {
            super(itemView);

            // Bind views
            ButterKnife.bind(this, itemView);
        }
    }

    // Method for setting the Class data
    public void setClassData(List<SchoolClass> classList) {
        mClassData = classList;
        notifyDataSetChanged();
    }
}
