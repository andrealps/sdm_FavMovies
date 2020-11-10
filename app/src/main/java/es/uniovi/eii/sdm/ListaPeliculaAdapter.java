package es.uniovi.eii.sdm;


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

import es.uniovi.eii.sdm.modelo.Pelicula;

public class ListaPeliculaAdapter extends RecyclerView.Adapter<ListaPeliculaAdapter.PeliculaViewHolder> {

    public interface OnItemClickListener{
        void onItemClick(Pelicula item);
    }

    private List<Pelicula> listaPeliculas;
    private final OnItemClickListener listener;

    public ListaPeliculaAdapter(List<Pelicula> listaPeliculas, OnItemClickListener listener) {
        this.listaPeliculas = listaPeliculas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PeliculaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Creamos la vista con el layout para un elemento
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.linea_recycler_view_pelicula, parent, false);
        return new PeliculaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PeliculaViewHolder holder, int position) {
        // Extrae de la lista el elemento indicado por posición
        Pelicula pelicula= listaPeliculas.get(position);
        Log.i("Lista","Visualiza elemento: "+pelicula);
        // llama al método de nuestro holder para asignar valores a los componentes
        // además, pasamos el listener del evento onClick
        holder.bindUser(pelicula, listener);
    }

    @Override
    public int getItemCount() {
        return listaPeliculas.size();
    }

    public static class PeliculaViewHolder extends RecyclerView.ViewHolder{
        private TextView titulo;
        private TextView fecha;
        private ImageView imagen;

        public PeliculaViewHolder(View itemView) {
            super(itemView);
            this.titulo = (TextView) itemView.findViewById(R.id.titulopeli);
            this.fecha = (TextView) itemView.findViewById(R.id.fechaestreno);
            this.imagen = (ImageView) itemView.findViewById(R.id.imagen);
        }

        public void bindUser(final Pelicula pelicula, final OnItemClickListener listener){
            titulo.setText(pelicula.getTitulo());
            fecha.setText(pelicula.getFecha());
            Picasso.get().load(pelicula.getUrlCaratula()).into(imagen);

            itemView.setOnClickListener(v -> {
                Log.i("Hola", "Hola");
                listener.onItemClick(pelicula);
            });
        }
    }
}
