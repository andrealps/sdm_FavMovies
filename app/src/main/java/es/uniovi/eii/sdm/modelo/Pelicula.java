package es.uniovi.eii.sdm.modelo;

import android.os.Parcel;
import android.os.Parcelable;

public class Pelicula implements Parcelable {
    private int id;

    String titulo;
    String argumento;
    Categoria categoria;
    String duracion;
    String fecha;

    // Nuevas propiedades
    String urlCaratula;
    String urlFondo;
    String urlTrailer;

    public Pelicula() {}

    public Pelicula(String titulo, String argumento, Categoria categoria, String duracion, String fecha, String urlCaratula, String urlFondo, String urlTrailer) {
        this.id = -1;

        this.titulo = titulo;
        this.argumento = argumento;
        this.categoria = categoria;
        this.duracion = duracion;
        this.fecha = fecha;

        this.urlCaratula = urlCaratula;
        this.urlFondo = urlFondo;
        this.urlTrailer = urlTrailer;
    }


    public Pelicula(String titulo, String argumento, Categoria categoria, String duracion, String fecha) {
        this.id = -1;

        this.titulo = titulo;
        this.argumento = argumento;
        this.categoria = categoria;
        this.duracion = duracion;
        this.fecha = fecha;

        this.urlCaratula = "";
        this.urlFondo = "";
        this.urlTrailer = "";
    }

    public Pelicula(int id, String titulo, String argumento, Categoria categoria, String duracion, String fecha, String urlCaratula, String urlFondo, String urlTrailer) {
        this.id = id;
        this.titulo = titulo;
        this.argumento = argumento;
        this.categoria = categoria;
        this.duracion = duracion;
        this.fecha = fecha;
        this.urlCaratula = urlCaratula;
        this.urlFondo = urlFondo;
        this.urlTrailer = urlTrailer;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getArgumento() {
        return argumento;
    }

    public void setArgumento(String argumento) {
        this.argumento = argumento;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getDuracion() {
        return duracion;
    }

    public void setDuracion(String duracion) {
        this.duracion = duracion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getUrlCaratula() {
        return urlCaratula;
    }

    public void setUrlCaratula(String urlCaratula) {
        this.urlCaratula = urlCaratula;
    }

    public String getUrlFondo() {
        return urlFondo;
    }

    public void setUrlFondo(String urlFondo) {
        this.urlFondo = urlFondo;
    }

    public String getUrlTrailer() {
        return urlTrailer;
    }

    public void setUrlTrailer(String urlTrailer) {
        this.urlTrailer = urlTrailer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Pelicula{" +
                "titulo='" + titulo + '\'' +
                ", duracion='" + duracion + '\'' +
                ", fecha='" + fecha + '\'' +
                '}';
    }

    protected Pelicula(Parcel in) {
        id = in.readInt();

        titulo = in.readString();
        argumento = in.readString();
        categoria = in.readParcelable(Categoria.class.getClassLoader());
        duracion = in.readString();
        fecha = in.readString();

        urlCaratula = in.readString();
        urlFondo = in.readString();
        urlTrailer = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);

        dest.writeString(titulo);
        dest.writeString(argumento);
        dest.writeParcelable(categoria, flags);
        dest.writeString(duracion);
        dest.writeString(fecha);

        dest.writeString(urlCaratula);
        dest.writeString(urlFondo);
        dest.writeString(urlTrailer);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Pelicula> CREATOR = new Creator<Pelicula>() {
        @Override
        public Pelicula createFromParcel(Parcel in) {
            return new Pelicula(in);
        }

        @Override
        public Pelicula[] newArray(int size) {
            return new Pelicula[size];
        }
    };
}
