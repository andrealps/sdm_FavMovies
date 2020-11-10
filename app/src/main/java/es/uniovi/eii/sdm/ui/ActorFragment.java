package es.uniovi.eii.sdm.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import es.uniovi.eii.sdm.R;
import es.uniovi.eii.sdm.datos.ActoresDataSource;
import es.uniovi.eii.sdm.modelo.Actor;

public class ActorFragment extends Fragment {
    public static final String NOMBRE_ACTOR = "nombre_actor";
    public static final String NOMBRE_PERSONAJE = "nombre_personaje";
    public static final String IMAGEN_ACTOR = "imagen_actor";

    // Lista para el Recycler
    List<Actor> listaActores = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){

        View root = inflater.inflate(R.layout.fragment_actores, container, false);

        final RecyclerView recycleActors = root.findViewById(R.id.reciclerViewActores);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(root.getContext());
        recycleActors.setLayoutManager(layoutManager);
        recycleActors.setHasFixedSize(true);

        // Referencias componentes
        //final TextView tcategoria = root.findViewById(R.id.text_actores);

        // Cogemos los datos que nos pasaron
        Bundle args = getArguments();
        int id_pelicula = args.getInt("id_pelicula");
//        if (args != null){
//            tcategoria.setText(args.getString("Aquí van a ir los actores de la película"));
//        }

        // Creamos un actores data para llamar al método que por SQL nos localizará el reparto
        ActoresDataSource actoresDataSource = new ActoresDataSource(root.getContext());
        actoresDataSource.open();
        listaActores = actoresDataSource.actoresParticipantes(id_pelicula);
        actoresDataSource.close();

        // Adapter para los actores
        ListaActoresAdapter laAdapter = new ListaActoresAdapter(listaActores, (actor) ->{
            // Si pulsamos sobre un actor nos llevará a su ficha en IMDB
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(actor.getUrlImdb())));
        });
        recycleActors.setAdapter(laAdapter);

        return root;
    }
}
