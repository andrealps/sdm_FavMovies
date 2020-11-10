package es.uniovi.eii.sdm.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Conexion {
    private Context mContexto;

    public Conexion(Context mContexto){this.mContexto = mContexto;}

    public boolean CompruebaConexion(){
        boolean conectado = false;

        ConnectivityManager connectivityManager = (ConnectivityManager) mContexto.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        conectado = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

        return conectado;
    }
}
