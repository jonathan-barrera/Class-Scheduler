package com.example.android.classscheduler;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.classscheduler.Model.SchoolClass;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jonathanbarrera on 6/15/18.
 * RecyclerView Adapter for SchoolClass objects
 */

public class SchoolClassAdapter extends RecyclerView.Adapter<SchoolClassAdapter.SchoolClassAdapterViewHolder> {

    private List<SchoolClass> mClassData;

    @NonNull
    @Override
    public SchoolClassAdapter.SchoolClassAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the list item view
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.class_details_list_item, parent, false);

        return new SchoolClassAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SchoolClassAdapter.SchoolClassAdapterViewHolder holder, int position) {
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

    // Helper method to format times
    private String getFormattedTime(String time) {
        String timeOfDay = "am";
        String[] timeParts = time.split(":");
        int hour = Integer.parseInt(timeParts[0]);

        // Make sure the hours are formatted in 12 hour format, with proper am/pm label
        if (hour > 12) {
            hour = hour - 12;
            timeOfDay = "pm";
        } else if (hour == 12) {
            timeOfDay = "pm";
        } else if (hour == 0) {
            hour = 12;
        }

        // Get the full formatted time string and return
        String formattedTime = hour + ":" + timeParts[1] + timeOfDay;
        return formattedTime;
    }

    // Helper method for getting the day of the week
    private String getDayOfTheWeek(String scheduleDay) {
        int day = Integer.parseInt(scheduleDay);
        switch (day) {
            case CreateClassesActivity.SUNDAY_INT:
                return "Sunday";
            case CreateClassesActivity.MONDAY_INT:
                return "Monday";
            case CreateClassesActivity.TUESDAY_INT:
                return "Tuesday";
            case CreateClassesActivity.WEDNESDAY_INT:
                return "Wednesday";
            case CreateClassesActivity.THURSDAY_INT:
                return "Thursday";
            case CreateClassesActivity.FRIDAY_INT:
                return "Friday";
            case CreateClassesActivity.SATURDAY_INT:
                return "Saturday";
            default:
                throw new IllegalArgumentException("Invalid day of the week integer: " + day);
        }
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
