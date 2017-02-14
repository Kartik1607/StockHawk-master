package com.sam_chordas.android.stockhawk.widget;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.service.WidgetService;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;
import com.sam_chordas.android.stockhawk.ui.StockGraphActivity;

/**
 * Created by Kartik Sharma on 11/12/16.
 */
public class TodayWidgetProvider extends AppWidgetProvider {

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int i : appWidgetIds){
            RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.widget);
            Intent listIntent = new Intent(context, WidgetService.class);
            views.setRemoteAdapter(R.id.listView_widget, listIntent);
            Intent clickIntentTemplate = new Intent(context, StockGraphActivity.class);
            PendingIntent clickPendingIntent = PendingIntent.getActivity(context,0,clickIntentTemplate,PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.listView_widget,clickPendingIntent);
            appWidgetManager.updateAppWidget(i, views);
            appWidgetManager.notifyAppWidgetViewDataChanged(i, R.id.listView_widget);
        }
    }



}


