package com.example.audiolibros;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.List;

public class AdaptadorLibros extends RecyclerView.Adapter<AdaptadorLibros.ViewHolder> {
    private LayoutInflater inflador;      //Crea Layouts a partir del XML
    protected List<Libro> listaLibros;    //Lista de libros a visualizar
    private Context contexto;

    //Para añadir un menú contextual al hacer una pulsación larga
    private View.OnLongClickListener onLongClickListener;

    //Para poder seleccionar un elemento del RecyclerView
    private View.OnClickListener onClickListener;

    public AdaptadorLibros(Context contexto, List<Libro> listaLibros) {
        inflador = (LayoutInflater) contexto
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listaLibros = listaLibros;
        this.contexto = contexto;
    }

    //Creamos nuestro ViewHolder, con los tipos de elementos a modificar
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView portada;
        public TextView titulo;
        public ViewHolder(View itemView) {
            super(itemView);
            portada = (ImageView) itemView.findViewById(R.id.portada);
            titulo = (TextView) itemView.findViewById(R.id.titulo);
        }
    }

    // Creamos el ViewHolder con las vista de un elemento sin personalizar
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflamos la vista desde el xml
        View v = inflador.inflate(R.layout.elemento_selector, null);
        v.setOnClickListener(onClickListener);  //Aplicamos el escuchador a cada una de las vistas del RecyclerView
        v.setOnLongClickListener(onLongClickListener); //Aplicamos el escuchador de pulsaciones largas
        return new ViewHolder(v);
    }

    // Usando como base el ViewHolder y lo personalizamos
    @Override
    public void onBindViewHolder(final ViewHolder holder, int posicion) {
        Libro libro = listaLibros.get(posicion);
        Aplicacion aplicacion = (Aplicacion) contexto.getApplicationContext();
        holder.titulo.setText(libro.titulo);

        aplicacion.getLectorImagenes().get(libro.urlImagen,
                new ImageLoader.ImageListener() {
                    @Override public void onResponse(ImageLoader.ImageContainer
                                                             response, boolean isImmediate) {
                        Bitmap bitmap = response.getBitmap();
                        holder.portada.setImageBitmap(bitmap);
                    }
                    @Override public void onErrorResponse(VolleyError error) {
                        holder.portada.setImageResource(R.drawable.books);
                    }
                });
        //Animacion propiedades*******
        holder.itemView.setScaleX(1);
        holder.itemView.setScaleY(1);
        //*******

    }

    // Indicamos el número de elementos de la lista
    @Override public int getItemCount() {
        return listaLibros.size();
    }

    //Para seleccionar elemento del RecyclerView
    public void setOnItemClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    //Para el menú contextual
    public void setOnItemLongClickListener(View.OnLongClickListener
                                                   onLongClickListener) {
        this.onLongClickListener = onLongClickListener;
    }
}