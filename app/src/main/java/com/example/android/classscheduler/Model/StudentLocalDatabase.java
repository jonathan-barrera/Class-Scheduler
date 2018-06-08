package com.example.android.classscheduler.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class StudentLocalDatabase implements Parcelable {

    // Variables for holding a student's information
    private Long mStudentId;
    private String mStudentName;
    private int mStudentSex;
    private int mStudentAge;
    private int mStudentGrade;
    private String mStudentClasses;
    private byte[] mStudentPicture;

    // Constructor
    public StudentLocalDatabase(long id, String name, int sex, int age, int grade, byte[] picture, String classes) {
        mStudentId = id;
        mStudentName = name;
        mStudentSex = sex;
        mStudentAge = age;
        mStudentGrade = grade;
        mStudentPicture = picture;
        mStudentClasses = classes;
    }

    // Methods for retrieving a student's data
    public long getStudentId() {return mStudentId;}
    public String getStudentName() {return mStudentName;}
    public int getStudentSex() {return mStudentSex;}
    public int getStudentAge() {return mStudentAge;}
    public int getStudentGrade() {return mStudentGrade;}
    public String getStudentClasses() {return mStudentClasses;}
    public byte[] getStudentPicture() {return mStudentPicture;}

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StudentLocalDatabase> CREATOR = new Creator<StudentLocalDatabase>() {
        @Override
        public StudentLocalDatabase createFromParcel(Parcel in) {
            return new StudentLocalDatabase(in);
        }

        @Override
        public StudentLocalDatabase[] newArray(int size) {
            return new StudentLocalDatabase[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mStudentId);
        dest.writeString(mStudentName);
        dest.writeInt(mStudentSex);
        dest.writeInt(mStudentAge);
        dest.writeInt(mStudentGrade);
        dest.writeString(mStudentClasses);

        // Only save student picture to Parcelable if it exists
        boolean[] booleanArray = new boolean[1];
        if (mStudentPicture != null) {
            booleanArray[0] = true;
        } else {
            booleanArray[0] = false;
        }
        dest.writeBooleanArray(booleanArray);
        if (booleanArray[0]) {
            dest.writeInt(mStudentPicture.length);
            dest.writeByteArray(mStudentPicture);
        }
    }

    protected StudentLocalDatabase(Parcel in) {
        mStudentId = in.readLong();
        mStudentName = in.readString();
        mStudentSex = in.readInt();
        mStudentAge = in.readInt();
        mStudentGrade = in.readInt();
        mStudentClasses = in.readString();

        // Only read student picture from Parcelable if it was saved
        boolean[] hasPicture = new boolean[1];
        in.readBooleanArray(hasPicture);
        if (hasPicture[0]) {
            mStudentPicture = new byte[in.readInt()];
            in.readByteArray(mStudentPicture);
        }
    }
}
