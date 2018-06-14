package com.example.android.classscheduler.Model;

import java.util.List;

/**
 * Created by jonathanbarrera on 6/13/18.
 * Object to hold a SchoolClass's (as in School SchoolClass) information
 */

public class SchoolClass {

    // Variables to hold info
    private String mTitle;
    private String mSubject;
    private String mTeacher;
    private List<String> mSchedule;

    public SchoolClass() {}

    // Constructor
    public SchoolClass(String title, String subject, String teacher, List<String> schedule) {
        mTitle = title;
        mSubject = subject;
        mTeacher = teacher;
        mSchedule = schedule;
    }

    // Methods for retrieving information
    public String getTitle() { return mTitle; }
    public void setTitle(String title) { mTitle = title; }
    public String getSubject() { return mSubject; }
    public void setSubject(String subject) { mSubject = subject; }
    public String getTeacher() { return mTeacher; }
    public void setTeacher(String teacher) { mTeacher = teacher; }
    public List<String> getSchedule() { return mSchedule; }
    public void setSchedule(List<String> schedule) { mSchedule = schedule; }
}
