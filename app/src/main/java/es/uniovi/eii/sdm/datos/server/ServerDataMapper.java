package es.uniovi.eii.sdm.datos.server;

import android.util.Log;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import es.uniovi.eii.sdm.datos.server.credits.Cast;
import es.uniovi.eii.sdm.datos.server.moviedetail.MovieDetail;
import es.uniovi.eii.sdm.datos.server.movielist.MovieData;
import es.uniovi.eii.sdm.datos.server.movielist.MovieListResult;
import es.uniovi.eii.sdm.modelo.Actor;
import es.uniovi.eii.sdm.modelo.Categoria;
import es.uniovi.eii.sdm.modelo.Pelicula;

public class ServerDataMapper {
    private static final String BASE_URL_IMG= "https://image.tmdb.org/t/p/";
    private static final String IMG_W342= "w342";
    private static final String IMG_ORIGINAL= "original";
    private static final String BASE_URL_YOUTUBE= "https://youtu.be/";
    private static final String SITE_YOUTUBE = "YouTube";

    public static List<Pelicula> convertToDomain(MovieListResult peliculas) {
        return convertMovieListToDomain(peliculas.getResults());
    }

    /**
     * Convierte datos de cada pelicula de la API: MovieData
     * en datos del dominio: Pelicula
     * @param movieData lista de películas de la API
     * @return lista de peliculas del dominio
     *
     * id               <-- id
     * titulo           <-- title
     * argumento        <-- overview
     * categoria        <-- genreIds (es una lista de id de generos)
     * duracion
     * fecha            <-- releaseDate
     * urlCaratula      <-- posterPath, completamos la url
     * urlFondo         <-- backdropPath
     * urlTrailer
     */
    public static List<Pelicula> convertMovieListToDomain(List<MovieData> movieData) {
        List<Pelicula> lpeliculas= new ArrayList<>();

        for (MovieData peliApi: movieData) {
            String urlCaratula;
            String urlFondo;

            if (peliApi.getPosterPath()==null) {
                urlCaratula= "";
            } else {
                urlCaratula= BASE_URL_IMG + IMG_W342 + peliApi.getPosterPath();
            }
            if (peliApi.getBackdropPath()==null) {
                urlFondo= "";
            } else {
                urlFondo = BASE_URL_IMG + IMG_ORIGINAL + peliApi.getBackdropPath();
            }

            lpeliculas.add(new Pelicula(peliApi.getId(),
                    peliApi.getTitle(),
                    peliApi.getOverview(),
                    new Categoria("",""),
                    "",
                    peliApi.getReleaseDate(),
                    urlCaratula,
                    urlFondo,
                    ""
            ));
        }

        return lpeliculas;
    }

    /**
     * Convierte datos de detalle de la película al dominio y completa el objeto película
     * categoria    <-- primer género que nos encontramos (si no hay -> vacío)
     * duracion    <-- conversión desde minutos(int) -> formato 1h 23m
     * urlTrailer    <-- url trailer en formato youtu.be
     * @param data
     * @param pelicula
     */
    public static void convertMovieDetailToDomain(MovieDetail data, Pelicula pelicula){
        // Género
        if (data.getGenres().get(0) == null)
            pelicula.setCategoria(new Categoria(data.getGenres().get(0).getName(), ""));
        else
            pelicula.setCategoria(new Categoria("", ""));

        // Duración
        if (data.getRuntime() == null)
            pelicula.setDuracion("");
        else
            pelicula.setDuracion(
                    ((data.getRuntime()/60 == 0)? "": data.getRuntime()/60 + "h ")
                    + data.getRuntime()%60 + "m");


        // Trailer
        if (!data.getVideo())
            pelicula.setUrlTrailer("");
         else
            // Sacamos el id del video de youtube
            pelicula.setUrlTrailer(BASE_URL_YOUTUBE + data.getVideos().getResults().get(0).getKey());

    }

    /**
     * Convierte datos de cada miembro del reparto de la API: Cast en datos del dominio: Actor
     * @param castList
     * @return
     *
     * id (no está en el constructor)   <-- castId
     * nombre_actor                     <-- name
     * nombre_personaje                 <-- character
     * imagen                           <-- profilePath
     * imdb (no tenemos nada equivalente)
     */
    public static List<Actor> convertCastListToDomain(List<Cast> castList){
        List<Actor> lactores = new ArrayList<>();
        // Actor actual
        Actor actor;

        for (Cast actorApi: castList) {
            actor = new Actor(actorApi.getName(), actorApi.getCharacter(),
                    BASE_URL_IMG + IMG_W342 + actorApi.getProfilePath(), "");
            actor.setId(actorApi.getCastId());
            lactores.add(actor);
        }

        return lactores;
    }
}
