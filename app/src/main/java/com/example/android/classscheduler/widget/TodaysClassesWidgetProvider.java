package com.example.android.classscheduler.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.example.android.classscheduler.MainMenu;
import com.example.android.classscheduler.R;

/**
 * Implementation of App Widget functionality.
 */
public class TodaysClassesWidgetProvider extends AppWidgetProvider {

    // TODO query the Classes database for classes that will occur today.
    // TODO display these using a (listview? textview?)
    // Update once every hour.

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews widget = new RemoteViews(context.getPackageName(), R.layout.todays_classes_widget);

        // Set list adapter
        Intent listIntent = new Intent(context, TodaysClassesWidgetService.class);
        widget.setRemoteAdapter(R.id.todays_classes_widget_list_view, listIntent);
        widget.setEmptyView(R.id.todays_classes_widget_list_view, R.id.empty_view);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.todays_classes_widget_list_view);

        // Intent to launch the app when clicked
        Intent launchIntent = new Intent(context, MainMenu.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent, 0);
        widget.setOnClickPendingIntent(R.id.todays_classes_widget_linear_layout, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, widget);

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.todays_classes_widget_list_view);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.todays_classes_widget_list_view);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

}

