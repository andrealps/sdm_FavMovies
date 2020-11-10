package es.uniovi.eii.sdm.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import es.uniovi.eii.sdm.R;
import es.uniovi.eii.sdm.datos.db.ActoresDataSource;
import es.uniovi.eii.sdm.datos.server.ServerDataMapper;
import es.uniovi.eii.sdm.datos.server.credits.Cast;
import es.uniovi.eii.sdm.datos.server.credits.Credits;
import es.uniovi.eii.sdm.modelo.Actor;
import es.uniovi.eii.sdm.remote.ApiUtils;
import es.uniovi.eii.sdm.remote.ThemoviedbApi;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static es.uniovi.eii.sdm.remote.ApiUtils.API_KEY;
import static es.uniovi.eii.sdm.remote.ApiUtils.LANGUAGE;

public class ActorFragment extends Fragment {
    // RecyclerView de actores
    RecyclerView recycleActors;
    // Lista para el Recycler
    List<Actor> listaActores = new ArrayList<>();

    // Referencia a la vista
    View root;

    // API, comunicación
    private ThemoviedbApi clienteThemoviedbApi;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        root = inflater.inflate(R.layout.fragment_actores, container, false);

        recycleActors = root.findViewById(R.id.reciclerViewActores);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        recycleActors.setLayoutManager(layoutManager);
        recycleActors.setHasFixedSize(true);

        // Cogemos los datos que nos pasaron
        Bundle args = getArguments();
        int id_pelicula = args.getInt("id_pelicula");

        // Recuperación de los datos del servicio
        clienteThemoviedbApi = ApiUtils.creaThemoviedbApi();
        // Recuperar datos para mostrarlos
        realizarPeticionReparto(clienteThemoviedbApi, id_pelicula);

        return root;
    }

    /**
     * Realiza una petición a la API: lista de participantes, para obtener el reparto,
     * de forma asíncrona y procesa el resultado
     * @param clienteThemoviedbApi
     * @param idPelicula
     */
    private void realizarPeticionReparto(ThemoviedbApi clienteThemoviedbApi, int idPelicula) {
        Call<Credits> call=
                clienteThemoviedbApi.getMovieCredits(idPelicula,API_KEY,LANGUAGE);

        // Petición asíncrona a la API
        call.enqueue(new Callback<Credits>() {
            @Override
            public void onResponse(Call<Credits> call, Response<Credits> response) {
                switch (response.code()) {
                    case 200:
                        Credits data= response.body();
                        List<Cast> cast= data.getCast();
                        Log.d("Peticion Reparto","ListaReparto: "+cast);
                        // convierte desde los objetos de data a los objetos modelo MovieData --> Pelicula
                        listaActores= ServerDataMapper.convertCastListToDomain(cast);

                        // mostrar datos
                        // Definición de onclik del actor
                        ListaActoresAdapter laAdapter = new ListaActoresAdapter(listaActores,
                                actor -> {
                                    //Si pulsamos sobre un actor nos llevará a su ficha en Imdb
                                    //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(actor.getUrlImdb())));
                                    Snackbar.make(root, R.string.msg_prox_mas_info,
                                            Snackbar.LENGTH_LONG)
                                            .show();
                                });

                        recycleActors.setAdapter(laAdapter);

                        break;
                    default:
                        call.cancel();
                        break;
                }
            }

            @Override
            public void onFailure(Call<Credits> call, Throwable t) {
                Log.e("Lista - error", t.toString());
            }

        });
    }
}
