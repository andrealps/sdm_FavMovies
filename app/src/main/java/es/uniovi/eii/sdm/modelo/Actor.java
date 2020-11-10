package es.uniovi.eii.sdm.modelo;

import android.os.Parcel;
import android.os.Parcelable;

public class Actor implements Parcelable {
    private int id;

    private String nombre_actor;
    private String nombre_personaje;
    private String imagen;
    private String urlImdb;

    public Actor(String nombre_actor, String imagen, String urlImdb) {
        this.id = -1;
        this.nombre_actor = nombre_actor;
        this.imagen = imagen;
        this.urlImdb = urlImdb;
    }

    public Actor(int id, String nombre_actor, String imagen, String urlImdb) {
        this.id = id;
        this.nombre_actor = nombre_actor;
        //this.nombre_personaje = nombre_personje;
        this.imagen = imagen;
        this.urlImdb = urlImdb;
    }

    public Actor(String nombre_actor, String nombre_personaje, String imagen, String urlImdb) {
        this.id = -1;
        this.nombre_actor = nombre_actor;
        this.nombre_personaje = nombre_personaje;
        this.imagen = imagen;
        this.urlImdb = urlImdb;
    }

    public Actor() {
        this.id = -1;
        this.nombre_actor = "";
        this.nombre_personaje = "";
        this.imagen = "";
        this.urlImdb = "";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre_actor() {
        return nombre_actor;
    }

    public void setNombre_actor(String nombre_actor) {
        this.nombre_actor = nombre_actor;
    }

    public String getNombre_personaje() {
        return nombre_personaje;
    }

    public void setNombre_personaje(String nombre_personaje) {
        this.nombre_personaje = nombre_personaje;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getUrlImdb() {
        return urlImdb;
    }

    public void setUrlImdb(String urlImdb) {
        this.urlImdb = urlImdb;
    }

    protected Actor(Parcel in) {
        id = in.readInt();
        nombre_actor = in.readString();
        nombre_personaje = in.readString();
        imagen = in.readString();
        urlImdb = in.readString();
    }

    public static final Creator<Actor> CREATOR = new Creator<Actor>() {
        @Override
        public Actor createFromParcel(Parcel in) {
            return new Actor(in);
        }

        @Override
        public Actor[] newArray(int size) {
            return new Actor[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(nombre_actor);
        dest.writeString(nombre_personaje);
        dest.writeString(imagen);
        dest.writeString(urlImdb);
    }
}
