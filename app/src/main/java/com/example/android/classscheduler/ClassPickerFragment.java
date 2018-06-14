package com.example.android.classscheduler;

import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jonathanbarrera on 6/13/18.
 */

public class ClassPickerFragment extends android.support.v4.app.DialogFragment {

    //TODO bug 1: when you navigate to create new class and then come back, the list gets doubled
    //TODO fix 2: put the ListView in a scrollable view, but allow the button at the bottom to remain in view

    // Views
    @BindView(R.id.class_picker_search_view)
    SearchView mClassPickerSearchView;
    @BindView(R.id.class_picker_list_view)
    ListView mClassPickerListView;
    @BindView(R.id.class_picker_dismiss_button)
    Button mClassPickerDismissButton;
    @BindView(R.id.class_picker_create_class_button)
    Button mCreateClassButton;

    // Member variables
    private ArrayAdapter<String> mAdapter;
    private List<String> mClassList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Inflate view
        View rootView = inflater.inflate(R.layout.fragment_class_picker, container, false);

        // Bind views
        ButterKnife.bind(this, rootView);

        // Set title
        getDialog().setTitle("Choose A Class");

        // Get the ClassList data
        mClassList = getArguments().getStringArrayList(EditStudentInfo.CLASS_LIST_KEY);

        // Create and set adapter to list view
        mAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, mClassList);
        mClassPickerListView.setAdapter(mAdapter);

        // Set query hint in search view
        mClassPickerSearchView.setQueryHint("Class Title");

        // Set the OnQueryTextListener
        mClassPickerSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return true;
            }
        });



        // Set OnClickLiners for the two Buttons
        mClassPickerDismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mCreateClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CreateClassesActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
