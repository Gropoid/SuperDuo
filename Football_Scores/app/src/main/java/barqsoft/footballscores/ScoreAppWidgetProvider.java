package barqsoft.footballscores;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;

import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import barqsoft.footballscores.service.myFetchService;

/**
 * Created by geraud on 09/01/16.
 */
public class ScoreAppWidgetProvider extends AppWidgetProvider {

    private static final String TAG = ScoreAppWidgetProvider.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v(TAG, "onReceive ...");
        super.onReceive(context, intent);
        String action = intent.getAction();
        if (myFetchService.ACTION_SCORES_UPDATED.equals(action)) {
            Log.v(TAG, "... ACTION_SCORES_UPDATED");
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date d = new Date();
            String[] args = new String[]{format.format(d)};
            Cursor c = context.getContentResolver().query(
                    DatabaseContract.scores_table.buildScoreWithDate(),
                    null,
                    null,
                    args,
                    null);

            if (c != null) {
                // Find the first upcoming match
                c.moveToFirst();
                while (!c.isLast()) {     // If last, then we display it instead of getting out of bounds
                    String match_time = c.getString(ScoresAdapter.COL_MATCHTIME);
                    String c_match_date = c.getString(ScoresAdapter.COL_DATE);
                    format = new SimpleDateFormat("HH:mm");
                    try {
                        Date match_date = format.parse(match_time);
                        int HH = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                        int mm = Calendar.getInstance().get(Calendar.MINUTE);
                        d = format.parse(String.format("%02d:%02d", HH, mm));
                        if (match_date.after(d))
                            break;
                    } catch (ParseException ex) {
                        c.moveToNext();
                        continue;
                    }
                    c.moveToNext();
                }

                SharedPreferences prefs = context.getSharedPreferences("WidgetInfo", Context.MODE_PRIVATE);
                int widgetCount = prefs.getInt("WIDGETS_COUNT", 0);
                for (int i = 0; i < widgetCount; i++) {
                    int id = prefs.getInt("WIDGET_ID" + String.valueOf(i), 0);

                    RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.score_appwidget);
                    // I have no idea where those column numbers come from, so I take them from the adapter. There has to be a better way
                    remoteViews.setTextViewText(R.id.tv_home_team, c.getString(ScoresAdapter.COL_HOME));
                    remoteViews.setTextViewText(R.id.tv_visitor_team, c.getString(ScoresAdapter.COL_AWAY));
                    remoteViews.setTextViewText(R.id.tv_score,
                            Utilities.getScores(
                                    c.getInt(ScoresAdapter.COL_HOME_GOALS),
                                    c.getInt(ScoresAdapter.COL_AWAY_GOALS)));
                    remoteViews.setTextViewText(R.id.tv_data, c.getString(ScoresAdapter.COL_MATCHTIME));
                    remoteViews.setImageViewResource(R.id.iv_home_crest, Utilities.getTeamCrestByTeamName(c.getString(ScoresAdapter.COL_HOME)));
                    remoteViews.setImageViewResource(R.id.iv_visitor_crest, Utilities.getTeamCrestByTeamName(c.getString(ScoresAdapter.COL_AWAY)));

                    AppWidgetManager.getInstance(context).updateAppWidget(id, remoteViews);
                }
            }
        }
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.v(TAG, "onUpdate");
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        SharedPreferences prefs = context.getSharedPreferences("WidgetInfo", Context.MODE_PRIVATE);
        prefs.edit().putInt("WIDGETS_COUNT", appWidgetIds.length).apply();
        for (int i = 0 ; i < appWidgetIds.length ; i++) {
            prefs.edit().putInt("WIDGET_ID" + String.valueOf(i), appWidgetIds[i]).apply();
        }
        Intent service_start = new Intent(context, myFetchService.class);
        context.startService(service_start);
    }

}
