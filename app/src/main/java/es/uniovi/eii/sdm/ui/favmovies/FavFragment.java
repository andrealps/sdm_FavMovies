package es.uniovi.eii.sdm.ui.favmovies;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.uniovi.eii.sdm.ListaPeliculaAdapter;
import es.uniovi.eii.sdm.R;
import es.uniovi.eii.sdm.ShowMovie;
import es.uniovi.eii.sdm.datos.db.PeliculasDataSource;
import es.uniovi.eii.sdm.modelo.Pelicula;

public class FavFragment extends Fragment {
    public static final String PELICULA_SELECCIONADA = "peli_seleccionada";

    private View root;

    RecyclerView listaPeliView;
    private List<Pelicula> listaPelisFavoritas;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.fragment_fav, container, false);

        listaPeliView = root.findViewById(R.id.recyclerView);
        listaPeliView.setHasFixedSize(true);

        // Recuperamos datos de la BD
        recuperarPeliculasFavoritasDb();
        cargarView();

        return root;
    }

    /**
     * Recupera todas las películas de la tabla películas (favoritas) de la BD y las carga en
     * la lista: listaPelisFavoritas
     */
    private void recuperarPeliculasFavoritasDb() {
        PeliculasDataSource peliculasDataSource = new PeliculasDataSource(root.getContext());
        // Abrir
        peliculasDataSource.open();
        listaPelisFavoritas = peliculasDataSource.getAllValorations();

        Log.d("BD recupera favoritas", "listaPelisFavoritas: " + listaPelisFavoritas);
        // Cerrar
        peliculasDataSource.close();
    }

    /**
     * Usaremos este método para cargar el RecyclerView, la lista de películas y el Adapter.
     * Este método se invoca desde onResume (especialmente
     */
    protected void cargarView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        listaPeliView.setLayoutManager(layoutManager);
        ListaPeliculaAdapter lpAdapter = new ListaPeliculaAdapter(listaPelisFavoritas,
                this::clikonItem);

        listaPeliView.setAdapter(lpAdapter);
    }

    // Click del item del adapter
    public void clikonItem(Pelicula peli) {
        Log.i("Click adapter", "Item Clicked " + peli.getCategoria().getNombre());
        //Paso el modo de apertura
        Intent intent = new Intent(root.getContext(), ShowMovie.class);
        intent.putExtra(PELICULA_SELECCIONADA, peli);

        startActivity(intent);
    }
}