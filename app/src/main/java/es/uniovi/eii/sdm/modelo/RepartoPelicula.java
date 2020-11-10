package es.uniovi.eii.sdm.modelo;

import android.os.Parcel;
import android.os.Parcelable;

public class RepartoPelicula implements Parcelable {
    private int id_reparto;
    private int id_pelicula;
    private String nombre_personaje;

    public RepartoPelicula(int id_reparto, int id_pelicula, String nombre_personaje) {
        this.id_reparto = id_reparto;
        this.id_pelicula = id_pelicula;
        this.nombre_personaje = nombre_personaje;
    }

    public RepartoPelicula() {};

    protected RepartoPelicula(Parcel in) {
        id_reparto = in.readInt();
        id_pelicula = in.readInt();
        nombre_personaje = in.readString();
    }

    public static final Creator<RepartoPelicula> CREATOR = new Creator<RepartoPelicula>() {
        @Override
        public RepartoPelicula createFromParcel(Parcel in) {
            return new RepartoPelicula(in);
        }

        @Override
        public RepartoPelicula[] newArray(int size) {
            return new RepartoPelicula[size];
        }
    };

    public int getId_reparto() {
        return id_reparto;
    }

    public void setId_reparto(int id_reparto) {
        this.id_reparto = id_reparto;
    }

    public int getId_pelicula() {
        return id_pelicula;
    }

    public void setId_pelicula(int id_pelicula) {
        this.id_pelicula = id_pelicula;
    }

    public String getNombre_personaje() {
        return nombre_personaje;
    }

    public void setNombre_personaje(String nombre_personaje) {
        this.nombre_personaje = nombre_personaje;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id_reparto);
        dest.writeInt(id_pelicula);
        dest.writeString(nombre_personaje);
    }
}
