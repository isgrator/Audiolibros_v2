package com.example.audiolibros;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

import static android.content.Context.MODE_PRIVATE;

/**
 * Implementation of App Widget functionality.
 */
public class widgetAudioLibros extends AppWidgetProvider {




    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] widgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int widgetId : widgetIds) {
            actualizaWidget(context, widgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }


    public static void actualizaWidget(Context context, int widgetId) {
        //Crea las remoteViews y las edita
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),R.layout.widget_audio_libros);

        CharSequence widgetText = context.getString(R.string.appwidget_text);
        remoteViews.setTextViewText(R.id.appwidget_text, widgetText);

        //Recuperamos último título y autor de las preferencias
        SharedPreferences pref = ultimoLibroWidget(context);
        String titulo = pref.getString("ultimo_titulo","Ningún título");
        String autor = pref.getString("ultimo_autor","Ningún autor");
        remoteViews.setTextViewText(R.id.tv_titulo, titulo);
        remoteViews.setTextViewText(R.id.tv_autor, autor);

        //Lanza la actividad principal al pulsar sobre el widget
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.icono_lista_peq, pendingIntent);
        //Indica al widget manager que actualice el widget con id widgetId
        AppWidgetManager.getInstance(context).updateAppWidget(widgetId, remoteViews);
    }

    //Método para recuperar datos del libro para el widget
    private static SharedPreferences ultimoLibroWidget(Context context){
        SharedPreferences pref = context.getSharedPreferences(
                "com.example.audiolibros_internal", MODE_PRIVATE);
        return pref;
    }
}

