package es.uniovi.eii.sdm.remote;

import retrofit2.Retrofit;

public class ApiUtils {
    public static String LANGUAGE = "es-ES";
    public static String API_KEY = "92f5c70751a28faeb6e96b05bd463b95";

    public static ThemoviedbApi creaThemoviedbApi(){
        Retrofit retrofit = RetrofitClient.getClient(ThemoviedbApi.BASE_URL);

        return retrofit.create(ThemoviedbApi.class);
    }
}
