package com.example.android.classscheduler.widget;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.classscheduler.MainMenu;
import com.example.android.classscheduler.R;
import com.example.android.classscheduler.model.SchoolClass;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import timber.log.Timber;

/**
 * Created by jonathanbarrera on 6/19/18.
 */

public class TodaysClassesWidgetFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private int mWidgetId;
    private String mUserId;
    private List<String> mClassList;
    private CountDownLatch mCountDownLatch;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;


    public TodaysClassesWidgetFactory(Context context, Intent intent) {
        // Get context
        mContext = context;

        // Initialize list
        mClassList = new ArrayList<>();

        // Initialize Firebase
        mFirebaseDatabase = FirebaseDatabase.getInstance();
    }

//    private void populateListItem() {
//        Timber.d("populateListItem flag");
//
//        mDatabaseReference.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                SchoolClass schoolClass = dataSnapshot.getValue(SchoolClass.class);
//                mClassList.add(schoolClass.getTitle());
//                Timber.d(schoolClass.getTitle() + "flag");
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {}
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {}
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {}
//        });
//
//    }

    @Override
    public void onCreate() {
    }

    @Override
    public void onDataSetChanged() {
        //populateListItem();

        //mCountDownLatch = new CountDownLatch(1);

        getItems();

//        // Get UserId
//        SharedPreferences sharedPreferences = mContext.getSharedPreferences(MainMenu.SHARED_PREFS, Context.MODE_PRIVATE);
//        mUserId = sharedPreferences.getString(MainMenu.USER_ID_SHARED_PREF_KEY, null);
//
//        mDatabaseReference = mFirebaseDatabase.getReference()
//                .child("users")
//                .child(mUserId)
//                .child("classes");
//
//        mDatabaseReference.addValueEventListener(this);
//
//        synchronized (this) {
//            try {
//                //this.wait();
//                mCountDownLatch.await();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }

    private void getItems() {
        // Get UserId
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(MainMenu.SHARED_PREFS, Context.MODE_PRIVATE);
        mUserId = sharedPreferences.getString(MainMenu.USER_ID_SHARED_PREF_KEY, null);

        mDatabaseReference = mFirebaseDatabase.getReference()
                .child("users")
                .child(mUserId)
                .child("classes");

        mDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
//                if (mCountDownLatch.getCount() != 0) {
//                    mClassList.clear();
//                }

                for (DataSnapshot child : children) {
                    mClassList.add(child.getValue(SchoolClass.class).getTitle());
                }

//                if (mCountDownLatch.getCount() != 0) {
//                    mCountDownLatch.countDown();
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onDestroy() {
        mClassList.clear();
    }

    @Override
    public int getCount() {
        return mClassList.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        // Inflate list item
        RemoteViews listItem = new RemoteViews(mContext.getPackageName(),
                R.layout.widget_list_item);

        // Populate list item
        listItem.setTextViewText(R.id.widget_list_item_text_view, mClassList.get(position));

        return listItem;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

//    @Override
//    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//        mClassList.clear();
//        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
//            SchoolClass schoolClass = snapshot.getValue(SchoolClass.class);
//            mClassList.add(schoolClass.getTitle());
//        }
//        synchronized (this) {
//            this.notify();
//        }
//    }
//
//    @Override
//    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//    }
}
