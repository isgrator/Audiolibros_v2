package com.example.audiolibros.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.example.audiolibros.Aplicacion;
import com.example.audiolibros.Libro;
import com.example.audiolibros.MainActivity;
import com.example.audiolibros.OnZoomSeekBarListener;
import com.example.audiolibros.R;
import com.example.audiolibros.ZoomSeekBar;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class DetalleFragment extends Fragment implements
        View.OnTouchListener, MediaPlayer.OnPreparedListener,
        MediaController.MediaPlayerControl, OnZoomSeekBarListener {
    public static String ARG_ID_LIBRO = "id_libro";
    MediaPlayer mediaPlayer;
    MediaController mediaController;
    ZoomSeekBar zsbAzul;

    @Override public View onCreateView(LayoutInflater inflador, ViewGroup
            contenedor, Bundle savedInstanceState) {
        View vista = inflador.inflate(R.layout.fragment_detalle,
                contenedor, false);
        Bundle args = getArguments();
        if (args != null) {
            int position = args.getInt(ARG_ID_LIBRO);
            ponInfoLibro(position, vista);
        } else {
            ponInfoLibro(0, vista);
        }
        return vista;
    }

    private void ponInfoLibro(int id, View vista) {
        Libro libro = ((Aplicacion) getActivity().getApplication()).getListaLibros().get(id);
        ((TextView) vista.findViewById(R.id.titulo)).setText(libro.titulo);
        ((TextView) vista.findViewById(R.id.autor)).setText(libro.autor);
        //((ImageView) vista.findViewById(R.id.portada)).setImageResource(libro.recursoImagen);
        //Para cargar imágenes de internet
        Aplicacion aplicacion = (Aplicacion) getActivity().getApplication();
        ((NetworkImageView) vista.findViewById(R.id.portada)).setImageUrl(
                libro.urlImagen,aplicacion.getLectorImagenes());

        //ZoomSeekBar*******************************************
        zsbAzul = (ZoomSeekBar) vista.findViewById(R.id.zsb_azul);
        zsbAzul.setOnZoomSeekBarListener(this);
        //******************************************************
        //
        vista.setOnTouchListener(this);
        if (mediaPlayer != null){
            mediaPlayer.release();
        }
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaController = new MediaController(getActivity());
        Uri audio = Uri.parse(libro.urlAudio);

        try {
            mediaPlayer.setDataSource(getActivity(), audio);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            Log.e("Audiolibros", "ERROR: No se puede reproducir "+audio,e);
        }
    }

    public void ponInfoLibro(int id) {
        ponInfoLibro(id, getView());
    }

    @Override public void onPrepared(MediaPlayer mediaPlayer) {
        Log.d("Audiolibros", "Entramos en onPrepared de MediaPlayer");
        //mediaPlayer.start();
        //Autorreproducir ON/OFF según prefenecia escogida***************
        SharedPreferences preferencias = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        if (preferencias.getBoolean("pref_autoreproducir", true)) {
            mediaPlayer.start();

            Log.d("isabel","duracion en milisegundos: "+mediaPlayer.getDuration());
            Log.d("isabel","duracion: "+(mediaPlayer.getDuration()/1000)/60 +"' "+(mediaPlayer.getDuration()/1000)%60 +"''");
            int duracionSeg = mediaPlayer.getDuration() / 1000;
            int duracionMin = duracionSeg / 60;
            int picoSegundos = duracionSeg % 60;
            zsbAzul.setVal(mediaPlayer.getCurrentPosition() / 1000);
            zsbAzul.setValMin(0);
            zsbAzul.setValMax(duracionSeg);
            zsbAzul.setEscalaMin(0);
            zsbAzul.setEscalaMax(duracionSeg);
            zsbAzul.setEscalaIni(0);
            zsbAzul.setEscalaRaya(duracionSeg/60);
            zsbAzul.setEscalaRayaLarga(duracionSeg / 5);
        zsbAzul.invalidate();
    }
    //***************************************************************
        mediaController.setMediaPlayer(this);
        mediaController.setAnchorView(getView().findViewById(
            R.id.fragment_detalle));
        mediaController.setEnabled(true);
        mediaController.show();
    }

    @Override public boolean onTouch(View vista, MotionEvent evento) {
        mediaController.show();
        return false;
    }

    @Override public void onStop() {
        mediaController.hide();
        try {
            mediaPlayer.stop();
            mediaPlayer.release();
        } catch (Exception e) {
            Log.d("Audiolibros", "Error en mediaPlayer.stop()");
        }
        super.onStop();
    }

    @Override public boolean canPause() {
        return true;
    }

    @Override public boolean canSeekBackward() {
        return true;
    }

    @Override public boolean canSeekForward() {
        return true;
    }

    @Override public int getBufferPercentage() {
        return 0;
    }

    @Override public int getCurrentPosition() {
        try {
            return mediaPlayer.getCurrentPosition();
        } catch (Exception e) {
            return 0;
        }
    }

    @Override public int getDuration() {
        return mediaPlayer.getDuration();
    }

    @Override public boolean isPlaying() {
        return mediaPlayer.isPlaying();
    }

    @Override public void pause() {
        mediaPlayer.pause();
    }

    @Override public void seekTo(int pos) {
        mediaPlayer.seekTo(pos);
    }

    @Override public void start() {mediaPlayer.start();

    }

    @Override public int getAudioSessionId() {
        return 0;
    }

    //Mostrar/ocultar elementos cuando se cambia de fragment
    //Así se llama a mostrarElementos cada vez que un nuevo fragment es visualizado
    @Override public void onResume(){
        Log.i("isabel","onResume()");
        DetalleFragment detalleFragment = (DetalleFragment)
                getFragmentManager().findFragmentById(R.id.detalle_fragment);
        if (detalleFragment == null ) {
            ((MainActivity) getActivity()).mostrarElementos(false);
        }
        super.onResume();
    }

    @Override
    public void onMoverPalanca(int val) {
        //Acciones a tomar cuando se mueva la palanca
        Log.i("isabel","hola "+val);
        mediaPlayer.seekTo(val*1000);  //Para que sea otra vez el valor en milisegundos
        mediaPlayer.start();
    }
}
