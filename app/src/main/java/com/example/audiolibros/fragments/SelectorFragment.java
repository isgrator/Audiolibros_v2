package com.example.audiolibros.fragments;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.app.Activity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.audiolibros.AdaptadorLibros;
import com.example.audiolibros.AdaptadorLibrosFiltro;
import com.example.audiolibros.Aplicacion;
import com.example.audiolibros.Libro;
import com.example.audiolibros.MainActivity;
import com.example.audiolibros.R;

import java.util.List;

public class SelectorFragment extends Fragment implements Animation.AnimationListener {
    private Activity actividad;
    private RecyclerView recyclerView;
    //private AdaptadorLibros adaptador;
    private AdaptadorLibrosFiltro adaptador;
    private List<Libro> listaLibros;

    @Override public void onAttach(Activity actividad) {
        super.onAttach(actividad);
        this.actividad = actividad;
        Aplicacion app = (Aplicacion) actividad.getApplication();
        adaptador = app.getAdaptador();
        listaLibros = app.getListaLibros();
    }

    @Override public View onCreateView(LayoutInflater inflador, ViewGroup
            contenedor, Bundle savedInstanceState) {
        //Activa la opción de añadir nuevos elementos items de menú desde el fragment
        setHasOptionsMenu(true);
        //***************************************************************************
        View vista = inflador.inflate(R.layout.fragment_selector,
                contenedor, false);
        recyclerView = (RecyclerView) vista.findViewById(
                R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(actividad,2));
        recyclerView.setAdapter(adaptador);
        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Toast.makeText(actividad, "Seleccionado el elemento: "
                                + recyclerView.getChildAdapterPosition(v),
                        Toast.LENGTH_SHORT).show();*/
                //((MainActivity) actividad).mostrarDetalle(recyclerView.getChildAdapterPosition(v));
                ((MainActivity) actividad).mostrarDetalle(
                        (int) adaptador.getItemId(
                                recyclerView.getChildAdapterPosition(v)));
            }
        });

        //Para añadir un menú contextual*******************************
        adaptador.setOnItemLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(final View v) {
                final int id = recyclerView.getChildAdapterPosition(v);
                AlertDialog.Builder menu = new AlertDialog.Builder(actividad);
                CharSequence[] opciones = { "Compartir", "Borrar ", "Insertar" };
                menu.setItems(opciones, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int opcion) {
                        switch (opcion) {
                            case 0: //Compartir
                                Libro libro = listaLibros.get(id);
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("text/plain");
                                i.putExtra(Intent.EXTRA_SUBJECT, libro.titulo);
                                i.putExtra(Intent.EXTRA_TEXT, libro.urlAudio);
                                startActivity(Intent.createChooser(i, "Compartir"));
                                break;
                            case 1: //Borrar
                                Snackbar.make(v,"¿Estás seguro?", Snackbar.LENGTH_LONG)
                                        .setAction("SI", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                //listaLibros.remove(id);
                                                //Animacion twin
                                                Animation anim = AnimationUtils.loadAnimation(actividad, R.anim.menguar);
                                                v.startAnimation(anim);
                                                //Animacion propieades*********
                                                /*Animator anim = AnimatorInflater.loadAnimator(actividad, R.animator.menguar);
                                                //anim.addListener(SelectorFragment.this);
                                                anim.setTarget(v);
                                                anim.start();*/
                                                adaptador.borrar(id);
                                                //******************************
                                                //adaptador.notifyDataSetChanged();
                                            }
                                        })
                                        .show();

                                break;
                            case 2: //Insertar
                                //(El Snackbar se puede quitar tb moviéndolo con el dedo hacia la derecha)
                                Snackbar.make(v,"Libro insertado", Snackbar.LENGTH_INDEFINITE)
                                        .setAction("OK", new View.OnClickListener() {
                                            @Override public void onClick(View view) { }
                                        })
                                        .show();
                                //listaLibros.add(listaLibros.get(id));
                                int posicion = recyclerView.getChildLayoutPosition(v);
                                adaptador.insertar((Libro) adaptador.getItem(posicion));

                                adaptador.notifyDataSetChanged();
                                break;
                        }
                    }
                });
                menu.create().show();
                return true;
            }
        });
        //*************************************************************
        return vista;
    }

    //Mostrar/Ocultar elementos al cambiar de fragment
    //Restaura los elementos ocultados cuando se regrese a SelectorFragment
    @Override public void onResume(){
        ((MainActivity) getActivity()).mostrarElementos(true);
        super.onResume();
    }

    //Para el menú******************************************************
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_selector, menu);
        //Para incorporar búsquedas dinámicamente*****
        MenuItem searchItem = menu.findItem(R.id.menu_buscar);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextChange(String query) {
                        adaptador.setBusqueda(query);
                        adaptador.notifyDataSetChanged();
                        return false;
                    }

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }
                });
        //Para deshacer la búsqueda y volver a mostrar todos los libros
        searchItem.setOnActionExpandListener(
                new MenuItem.OnActionExpandListener() {
                    @Override
                    public boolean onMenuItemActionCollapse(MenuItem item) {
                        adaptador.setBusqueda("");
                        adaptador.notifyDataSetChanged();
                        return true;  // Para permitir cierre
                    }

                    @Override
                    public boolean onMenuItemActionExpand(MenuItem item) {
                        return true;  // Para permitir expansión
                    }
                });
        //*********************************************
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_ultimo) {
            ((MainActivity) actividad).irUltimoVisitado();
            return true;
        } else if (id == R.id.menu_buscar) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAnimationStart(Animation animation) {
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        adaptador.notifyDataSetChanged();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    //******************************************************************

}
