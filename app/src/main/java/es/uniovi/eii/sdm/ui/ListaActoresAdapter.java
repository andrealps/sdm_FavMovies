package es.uniovi.eii.sdm.ui;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import es.uniovi.eii.sdm.R;
import es.uniovi.eii.sdm.modelo.Actor;

public class ListaActoresAdapter extends RecyclerView.Adapter<ListaActoresAdapter.ActorViewHolder>{
    private List<Actor> listaActores;
    private final ListaActoresAdapter.OnItemClickListener listener;

    public ListaActoresAdapter(List<Actor> listaActores, OnItemClickListener listener) {
        this.listaActores = listaActores;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ActorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Creamos la vista con el layout para un elemento
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.linea_recycler_view_actor, parent, false);
        return new ListaActoresAdapter.ActorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ActorViewHolder holder, int position) {
        // Extrae de la lista el elemento indicado por posición
        Actor actor = listaActores.get(position);
        Log.i("Lista actores","Visualiza elemento: "+actor);
        // llama al método de nuestro holder para asignar valores a los componentes
        // además, pasamos el listener del evento onClick
        holder.bindUser(actor, listener);
    }

    @Override
    public int getItemCount() {
        return listaActores.size();
    }

    /**************************************************************************************************/
    public interface OnItemClickListener{
        void onItemClick(Actor item);
    }

    public static class ActorViewHolder extends RecyclerView.ViewHolder{
        private TextView nombre_actor;
        private TextView nombre_personaje;
        private ImageView imagen_actor;

        public ActorViewHolder(View itemView) {
            super(itemView);
            this.nombre_actor = (TextView) itemView.findViewById(R.id.nombre_actor);
            this.nombre_personaje = (TextView) itemView.findViewById(R.id.nombre_personaje);
            this.imagen_actor = (ImageView) itemView.findViewById(R.id.imagen_actor);
        }

        public void bindUser(final Actor actor, final OnItemClickListener listener){
            nombre_actor.setText(actor.getNombre_actor());
            nombre_personaje.setText(actor.getNombre_personaje());
            Picasso.get().load(actor.getImagen()).into(imagen_actor);

            itemView.setOnClickListener(v -> {
                Log.i("ActoresAdapter", "en el bind user");
                listener.onItemClick(actor);
            });
        }
    }
}
