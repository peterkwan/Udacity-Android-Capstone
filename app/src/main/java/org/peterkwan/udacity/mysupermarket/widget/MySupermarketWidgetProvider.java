package org.peterkwan.udacity.mysupermarket.widget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import org.peterkwan.udacity.mysupermarket.R;

import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE;

/**
 * Implementation of App Widget functionality.
 */
public class MySupermarketWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds)
            updateAppWidget(context, appWidgetManager, appWidgetId);

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent != null && ACTION_APPWIDGET_UPDATE.equals(intent.getAction())) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, MySupermarketWidgetProvider.class));
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list_view);

            for (int appWidgetId : appWidgetIds)
                updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.mysupermarket_widget);
        views.setRemoteAdapter(R.id.widget_list_view, new Intent(context, MySupermarketWidgetService.class));
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}

