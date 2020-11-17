package es.uniovi.eii.sdm.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.uniovi.eii.sdm.ListaPeliculaAdapter;
import es.uniovi.eii.sdm.R;
import es.uniovi.eii.sdm.ShowMovie;
import es.uniovi.eii.sdm.datos.server.ServerDataMapper;
import es.uniovi.eii.sdm.datos.server.movielist.MovieData;
import es.uniovi.eii.sdm.datos.server.movielist.MovieListResult;
import es.uniovi.eii.sdm.modelo.Pelicula;
import es.uniovi.eii.sdm.remote.ApiUtils;
import es.uniovi.eii.sdm.remote.ThemoviedbApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static es.uniovi.eii.sdm.remote.ApiUtils.API_KEY;
import static es.uniovi.eii.sdm.remote.ApiUtils.LANGUAGE;

public class HomeFragment extends Fragment {
    public static final String PELICULA_SELECCIONADA = "peli_seleccionada";

    private RecyclerView listaPeliView;
    private View root;

    private ThemoviedbApi clienteThemoviedbApi;

    private List<Pelicula> listaPeli;

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_home, container, false);

        listaPeliView = root.findViewById(R.id.recyclerView);
        listaPeliView.setHasFixedSize(true);

        // Cliente para hacer peticiones
        clienteThemoviedbApi = ApiUtils.creaThemoviedbApi();

        // Recuperamos datos de la BD
        realizarPeticionPeliculasPopulares(clienteThemoviedbApi);

        return root;
    }


    /**
     * Reliza una petición a la API: lista de películas populares
     * De forma asíncrona y procesa el resultado
     * @param clienteThemoviedbApi
     */
    private void realizarPeticionPeliculasPopulares(ThemoviedbApi clienteThemoviedbApi) {
        // Hacemos una llamada que nos devuelva MovieListResult
        Call<MovieListResult> call = clienteThemoviedbApi.getListMovies("popular", API_KEY, LANGUAGE, 1);
        // Petición asíncrona a la API
        call.enqueue(new Callback<MovieListResult>() {
            // Respuesta a la llamada
            @Override
            public void onResponse(Call<MovieListResult> call, Response<MovieListResult> response) {
                switch (response.code()){
                    // Llamada correcta
                    case 200:
                        MovieListResult data = response.body();
                        List<MovieData> listDatosPeliculas = data.getResults();  // getMovieData
                        Log.d("Petición PelPopular", "ListaDatosPeliculas: " + listDatosPeliculas);
                        // convierte desde los objetos de data a los objetos modelo MovieData --> Pelicula
                        listaPeli= ServerDataMapper.convertMovieListToDomain(listDatosPeliculas);

                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(root.getContext());
                        listaPeliView.setLayoutManager(layoutManager);

                        // Instanciamos el adapter con los datos de la petición y lo asignamos a RecyclerView
                        // Generar el adaptador, le pasamos la lista de usuarios
                        // y el manejador para el evento click sobre un elemento
                        ListaPeliculaAdapter lpAdapter = new ListaPeliculaAdapter(listaPeli,
                                peli -> clikonItem(peli));
                        /*Le coloco el adapter*/
                        listaPeliView.setAdapter(lpAdapter);

                        break;
                    default:
                        call.cancel();
                        break;
                }
            }

            @Override
            public void onFailure(Call<MovieListResult> call, Throwable t) {
                Log.e("Lista - error", t.toString());
            }
        });
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