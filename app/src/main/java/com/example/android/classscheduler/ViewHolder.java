package com.example.android.classscheduler;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.classscheduler.Model.Student;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by jonathanbarrera on 6/10/18.
 */

public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    @BindView(R.id.student_name_text_view) TextView studentNameTV;
    @BindView(R.id.student_sex_text_view) TextView studentSexTV;
    @BindView(R.id.student_age_text_view) TextView studentAgeTV;
    @BindView(R.id.student_picture_image_view) ImageView studentPictureIV;

    public ViewHolder(View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);
    }

    @Override
    public void onClick(View v) {

    }
}
