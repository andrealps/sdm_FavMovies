package es.uniovi.eii.sdm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import es.uniovi.eii.sdm.datos.ActoresDataSource;
import es.uniovi.eii.sdm.datos.PeliculasDataSource;
import es.uniovi.eii.sdm.datos.RepartoPeliculaDataSource;
import es.uniovi.eii.sdm.modelo.Actor;
import es.uniovi.eii.sdm.modelo.Categoria;
import es.uniovi.eii.sdm.modelo.Pelicula;
import es.uniovi.eii.sdm.modelo.RepartoPelicula;

public class MainRecycler extends AppCompatActivity {
    public static final String PELICULA_SELECCIONADA = "peli_seleccionada";
    public static final String PELICULA_CREADA = "peli_creada";
    private static final int GESTION_ACTIVITY = 1;

    // Para elegir las categorías a mostrar en Settings y filtrar las películas
    public static String filtroCategoria = null;

    RecyclerView listaPeliView;
    List<Pelicula> listaPeli;
    Pelicula peli;

    List<Actor> listaActor;
    List<RepartoPelicula> listaRepartoPelicula;

    // Gestión de las notificaciones
    NotificationCompat.Builder mBuilder;
    NotificationManager mNotificationManager;

    private boolean primeraEjecucion = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recycler);

        // Se lanza la notificación pero no se activa hasta que no se le indique al Builder
        // Esto será dentro de la tarea asíncrona
        ConstruirNotificacion(getString(R.string.app_name), "Cargada la base de datos");

        DownloadFilesTask task = new DownloadFilesTask();
        task.execute();
    }

 /*   // click del item del adapter
    private void clickOnItem(Pelicula peli) {
        Intent intent = new Intent(MainRecycler.this, MainActivity.class);
        intent.putExtra(PELICULA_SELECCIONADA, peli);
        startActivity(intent);
    }*/

    public void clickOnItem(Pelicula peli) {
        Intent intent = new Intent(MainRecycler.this, ShowMovie.class);
        intent.putExtra(PELICULA_SELECCIONADA, peli);
        // Transición de barrido
        startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    private void rellenarLista() {
        listaPeli = new ArrayList<>();
        Categoria cataccion = new Categoria("Acción", "PelisAccion");
        Pelicula peli = new Pelicula("Tenet", "Una acción épica que gira en torno al espionaje internacional, " +
                "los viajes en el tiempo y la evolución, en la que un agente secreto debe prevenir la Tercera Guerra Mundial.",
                cataccion, "150", "26/8/2020");
        listaPeli.add(peli);
    }

    public void crearPeliNuevaFab(View v) {
        Intent intent = new Intent(MainRecycler.this, NewMovie.class);
        startActivityForResult(intent, GESTION_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Comprobamos a qué petición se está respondiendo
        if (requestCode == GESTION_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                peli = data.getParcelableExtra(PELICULA_CREADA);
                listaPeli.add(peli);

                ListaPeliculaAdapter listaPeliculaAdapter = new ListaPeliculaAdapter(listaPeli,
                        this::clickOnItem);
            }
        }
    }

    // Comprobará las películas con el filtro
    @Override
    protected void onResume() {
        super.onResume();

        // Pensado para volver del SettingsActivity con el filtro
        if (!primeraEjecucion)
            cargarView();
    }

    // Gestión del menu de Settings
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflae the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    // Invoca la activity SettingsActivity
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.settings) {
            Intent intentSettingsActivity = new Intent(MainRecycler.this, SettingsActivity.class);
            startActivity(intentSettingsActivity);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

     // Mismo contenido que antiguo onResume
    protected void cargarView() {
        listaPeliView = (RecyclerView) findViewById(R.id.recyclerView);
        listaPeliView.setHasFixedSize(true);

        // Obtenemos la lista de películas de la DB
        PeliculasDataSource peliculasDataSource = new PeliculasDataSource(getApplicationContext());
        peliculasDataSource.open();
        if (filtroCategoria == null || filtroCategoria.equals("Sin definir"))
            listaPeli = peliculasDataSource.getAllValorations();
        else
            // Cargo las películas en la lista que cumplen con el filtro
            listaPeli = peliculasDataSource.getFilteredValorations(filtroCategoria);
        peliculasDataSource.close();

        // Con la lista de películas iniciamos el RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        listaPeliView.setLayoutManager(layoutManager);
        ListaPeliculaAdapter lpAdapter = new ListaPeliculaAdapter(listaPeli, this::clickOnItem);
        listaPeliView.setAdapter(lpAdapter);

        primeraEjecucion = false;
    }

    private void crearNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "CANAL";
            String description = "DESCRIPCION CANAL";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("M_CH_ID", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void ConstruirNotificacion(String titulo, String contenido) {
        crearNotificationChannel(); //Para la versión Oreo es necesario primero crear el canal
        //Instancia del servicio de notificaciones
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //construcción de la notificación
        mBuilder = new NotificationCompat.Builder(getApplicationContext(), "M_CH_ID");
        mBuilder.setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(titulo)
                .setContentText(contenido);
    }

    /**
     * Clase asíncrona interna: AsyncTask <Parametro de entrada, Tipo de valor del progreso, Tipo del return>
     */
    private class DownloadFilesTask extends AsyncTask<Void, Integer, String> {
        // Barra de progreso
        private ProgressDialog progressDialog;

        /**
         * Variables necesarias para llevar a cabo el proceso de carga
         * Fórmula porcentaje leído: (numeroDeLineasLeidas/lineasALeer)*100
         * Esta división requiere que el tipo de las variables sea floatante para poder obtener
         * decimales que al multiplicar por 100 nos den el porcentaje
         *
         * En caso de usar enteros pasaría de 0 a 100 sin los intermedios, llevando una cuenta inválida
         */
        private float lineasALeer = 0.0f;
        float numeroLineasLeidas = 0.0f;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            this.progressDialog = new ProgressDialog(MainRecycler.this);
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();

            // Iniciamos el lineasALeer, con un repaso a la cantidad de líneas que tienen los ficheros
            lineasALeer = (float) (lineasFichero("peliculas.csv"));
            lineasALeer = (float) (lineasALeer + lineasFichero("peliculas-reparto.csv"));
            lineasALeer = (float) (lineasALeer + lineasFichero("reparto.csv"));
        }

        @Override
        protected String doInBackground(Void... voids) {
            String mensaje = "";

            try {
                cargarPeliculas();
                cargarReparto();
                cargarRepartoPelicula();

                mensaje = "BD Actualizada";
            } catch (Exception e) {
                mensaje = "Error en la carga de la base de datos";
            }

            // Lanzamos notificación
            mNotificationManager.notify(001, mBuilder.build());
            return mensaje;
        }

        /**
         * Actualiza la barra de progreso
         * El Integer se corresponde al parámetro indicado en el cabezado de la clase
         *
         * @param progress
         */
        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String message) {
            // Descartar el mensaje después de que la BD haya sido actualizada
            this.progressDialog.dismiss();
            // Avisamos de que la BD se cargó satisfactoriamente (o hubo error)
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            /*
            Cargamos el RecyclerView por primera vez
            Este método ya no tiene sentido llamarlo desde el onCreate u onResume, pues necesitamos
            asegurarnos de haber cargado la BD antes de lanzarla
             */
            cargarView();
        }

        /**
         * Devuelve un entero con las líneas que contiene un fiohero, cuyo nombre recibe
         * por parámetro
         *
         * @param fichero
         * @return número de lineas del fichero
         */
        private int lineasFichero(String fichero) {
            InputStream file;
            InputStreamReader reader;
            BufferedReader bufferedReader = null;

            int numeroLineas = 0;
            try {
                file = getAssets().open(fichero);
                reader = new InputStreamReader(file);
                bufferedReader = new BufferedReader(reader);

                while (bufferedReader.readLine() != null) {
                    numeroLineas++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            return numeroLineas;
        }

        /**
         * Lee lista de películas desde el fichero csv en assets
         * Crea listaPeli como un ArrayList<Pelicula>
         */
        protected void cargarPeliculas() {
            Pelicula peli;
            listaPeli = new ArrayList<Pelicula>();
            InputStream file = null;
            InputStreamReader reader = null;
            BufferedReader bufferedReader = null;

            try {
                file = getAssets().open("peliculas.csv");
                reader = new InputStreamReader(file);
                bufferedReader = new BufferedReader(reader);

                String line = null;

                // Leemos primera linea, no aporta nada, es el encabezado
                bufferedReader.readLine();

                while ((line = bufferedReader.readLine()) != null) {
                    String[] data = line.split(";");
                    if (data != null) {
                        if (data.length == 9) {
                            peli = new Pelicula(Integer.parseInt(data[0]), data[1], data[2], new Categoria(data[3], ""), data[4],
                                    data[5], data[6], data[7], data[8]);
                        } else {
                            peli = new Pelicula(Integer.parseInt(data[0]), data[1], data[2], new Categoria(data[3], ""), data[4],
                                    data[5], "", "", "");
                        }

                        Log.d("cargarPeliculas", peli.toString());
                        listaPeli.add(peli);


                        // Metemos la pelicula en la BD
                        PeliculasDataSource peliculasDataSource = new PeliculasDataSource(getApplicationContext());
                        peliculasDataSource.open();
                        peliculasDataSource.createpelicula(peli);
                        peliculasDataSource.close();

                        // Actualizamos el progreso
                        numeroLineasLeidas++;
                        publishProgress((int) ((numeroLineasLeidas / lineasALeer) * 100));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        // Método sobrecargado
        // Igual que el cargarPeliculas normal pero con 2 if extra
        protected void cargarPeliculas(String filtro) {
            Pelicula peli = null;
            listaPeli = new ArrayList<Pelicula>();
            InputStream file = null;
            InputStreamReader reader = null;
            BufferedReader bufferedReader = null;

            try {
                file = getAssets().open("peliculas.csv");
                reader = new InputStreamReader(file);
                bufferedReader = new BufferedReader(reader);

                String line = null;

                // Leemos primera linea, no aporta nada, es el encabezado
                bufferedReader.readLine();

                while ((line = bufferedReader.readLine()) != null) {
                    String[] data = line.split(";");
                    if (data != null) {
                        if (data.length == 9) {
                            if (data[3].equals(filtro)) {
                                peli = new Pelicula(Integer.parseInt(data[0]), data[1], data[2], new Categoria(data[3], ""), data[4],
                                        data[5], data[6], data[7], data[8]);
                            }
                        } else {
                            if (data[3].equals(filtro)) {
                                peli = new Pelicula(Integer.parseInt(data[0]), data[1], data[2], new Categoria(data[3], ""), data[4],
                                        data[5], "", "", "");
                            }
                        }

                        Log.d("cargarPeliculas", peli.toString());
                        listaPeli.add(peli);


                        // Metemos la pelicula en la BD
                        PeliculasDataSource peliculasDataSource = new PeliculasDataSource(getApplicationContext());
                        peliculasDataSource.open();
                        peliculasDataSource.createpelicula(peli);
                        peliculasDataSource.close();

                        // Actualizamos el progreso
                        numeroLineasLeidas++;
                        publishProgress((int) ((numeroLineasLeidas / lineasALeer) * 100));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void cargarReparto() {
            Actor actor = null;
            listaActor = new ArrayList<Actor>();
            InputStream file = null;
            InputStreamReader reader = null;
            BufferedReader bufferedReader = null;

            try {
                file = getAssets().open("reparto.csv");
                reader = new InputStreamReader(file);
                bufferedReader = new BufferedReader(reader);

                String line = null;

                // Leemos primera linea, no aporta nada, es el encabezado
                bufferedReader.readLine();

                while ((line = bufferedReader.readLine()) != null) {
                    String[] data = line.split(";");
                    if (data != null) {
                        if (data.length == 4) {
                            actor = new Actor(Integer.parseInt(data[0]), data[1], data[2], data[3]);
                        }

                        Log.d("cargarActores", actor.toString());
                        listaActor.add(actor);

                        // Metemos actor en la BD
                        ActoresDataSource actoresDataSource = new ActoresDataSource(getApplicationContext());
                        actoresDataSource.open();
                        actoresDataSource.createactor(actor);
                        actoresDataSource.close();

                        // Actualizamos el progreso
                        numeroLineasLeidas++;
                        publishProgress((int) ((numeroLineasLeidas / lineasALeer) * 100));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void cargarRepartoPelicula() {
            RepartoPelicula repPeli = null;
            listaRepartoPelicula = new ArrayList<>();
            InputStream file = null;
            InputStreamReader reader = null;
            BufferedReader bufferedReader = null;

            try {
                file = getAssets().open("peliculas-reparto.csv");
                reader = new InputStreamReader(file);
                bufferedReader = new BufferedReader(reader);

                String line = null;

                // Leemos primera linea, no aporta nada, es el encabezado
                bufferedReader.readLine();

                while ((line = bufferedReader.readLine()) != null) {
                    String[] data = line.split(";");
                    if (data != null) {
                        if (data.length == 3) {
                            repPeli = new RepartoPelicula(Integer.parseInt(data[0]), Integer.parseInt(data[1]), data[2]);
                        }

                        Log.d("cargarActores", repPeli.toString());
                        listaRepartoPelicula.add(repPeli);

                        // Metemos actor en la BD
                        RepartoPeliculaDataSource repartoPeliDataSource = new RepartoPeliculaDataSource(getApplicationContext());
                        repartoPeliDataSource.open();
                        repartoPeliDataSource.createrepartopeli(repPeli);
                        repartoPeliDataSource.close();

                        // Actualizamos el progreso
                        numeroLineasLeidas++;
                        publishProgress((int) ((numeroLineasLeidas / lineasALeer) * 100));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }


    }
}