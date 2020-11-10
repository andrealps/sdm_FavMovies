package es.uniovi.eii.sdm;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import es.uniovi.eii.sdm.modelo.Pelicula;
import es.uniovi.eii.sdm.ui.ActorFragment;
import es.uniovi.eii.sdm.ui.ArgumentoFragment;
import es.uniovi.eii.sdm.ui.InfoFragment;
import es.uniovi.eii.sdm.util.Conexion;

public class ShowMovie extends AppCompatActivity {
    private Pelicula pelicula;

    CollapsingToolbarLayout toolbarLayout;
    ImageView imagenFondo;
    TextView categoria;
    TextView estreno;
    TextView duracion;
    TextView argumento;
    ImageView caratula;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_movie);

        // Recepción datos como activity secundaria
        Intent intentPeli = getIntent();
        pelicula = intentPeli.getParcelableExtra(MainRecycler.PELICULA_SELECCIONADA);

        // Gestion barra de la app
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarLayout = findViewById(R.id.toolbar_layout);
        toolbarLayout.setTitle(getTitle());
        imagenFondo = findViewById(R.id.imagenFondo);

        // Gestión de la botonera
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Le añado un listener
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        if (pelicula != null) {
            String fecha = pelicula.getFecha();
            toolbarLayout.setTitle(pelicula.getTitulo() + " (" + fecha.substring(fecha.lastIndexOf('/') + 1) + ")");
        }

        // Gestión de los controles que contienen los datos de la película
        categoria = findViewById(R.id.categoria);
        estreno = findViewById(R.id.estreno);
        duracion = findViewById(R.id.duracion);
        argumento = findViewById(R.id.argumento);
        caratula = findViewById(R.id.caratula);

        if (pelicula != null)
            mostrarDatos(pelicula);

        // Gestión del FAB
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> verTrailer(pelicula.getUrlTrailer()));
    }

    // Abre una Activy con Youtube y muestra el vídeo indicado en el parámetro
    private void verTrailer(String urlTrailer) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlTrailer)));
    }

    // Carga los datos que tenemos en la instancia en los componentes de la activity para mostrarlos
    private void mostrarDatos(Pelicula pelicula) {
        if (!pelicula.getTitulo().isEmpty()) {
            // Actualizar componentes con valores de la pelicula especificada
            String fecha = pelicula.getFecha();
            toolbarLayout.setTitle(pelicula.getTitulo() + " (" + fecha.substring(fecha.lastIndexOf('/') + 1) + ")");
            // Imagen de fondo
            Picasso.get().load(pelicula.getUrlFondo()).into(imagenFondo);

            // Pongo el fragment INFO por defecto
            InfoFragment info = new InfoFragment();
            Bundle args = new Bundle(); // lista de información por transacción que le pasamos al fragmento
            args.putString(InfoFragment.ESTRENO, pelicula.getFecha());
            args.putString(InfoFragment.DURACION, pelicula.getDuracion());
            args.putString(InfoFragment.CARATULA, pelicula.getUrlCaratula());
            info.setArguments(args);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, info).commit();

            /*
            categoria.setText(pelicula.getCategoria().getNombre());
            estreno.setText(pelicula.getFecha());
            duracion.setText(pelicula.getDuracion());
            argumento.setText(pelicula.getArgumento());

            // Imagen de la carátula
            Picasso.get().load(pelicula.getUrlCaratula()).into(caratula);
             */
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // para el menú antes
        /*
        if (id == R.id.action_settings) {
            return true;
        }
         */

        if (id == R.id.compartir) {
            Conexion conexion = new Conexion(getApplicationContext());
            if (conexion.CompruebaConexion()) {
                compartirPeli();
            } else
                Toast.makeText(getApplicationContext(), R.string.error_conexion, Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Abre el diálogo de compartir para que el usuario elija una app
     * Luego envia el texto que repreenta la pelicula
     */
    public void compartirPeli() {
        /* es necesario hacer un intent con la constate ACTION_SEND */
        /*Llama a cualquier app que haga un envío*/
        Intent itSend = new Intent(Intent.ACTION_SEND);
        /* vamos a enviar texto plano */
        itSend.setType("text/plain");
        // itSend.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{​​​​para}​​​​);
        itSend.putExtra(Intent.EXTRA_SUBJECT,
                getString(R.string.subject_compartir) + ": " + pelicula.getTitulo());
        itSend.putExtra(Intent.EXTRA_TEXT, getString(R.string.titulo)
                + ": " + pelicula.getTitulo() + "\n" +
                getString(R.string.argumento)
                + ": " + pelicula.getArgumento());

        /* iniciamos la actividad */
            /* puede haber más de una aplicacion a la que hacer un ACTION_SEND,
               nos sale un ventana que nos permite elegir una.
               Si no lo pongo y no hay activity disponible, pueda dar un error */
        Intent shareIntent = Intent.createChooser(itSend, null);

        startActivity(shareIntent);
    }

    // Gestión del menú
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_movie, menu);
        return true;
    }

    // Método para manejar el navegador
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (pelicula != null) {
                switch (item.getItemId()) {
                    case R.id.navigation_info:
                        //Creamos el framento de información
                        InfoFragment info = new InfoFragment();
                        Bundle args = new Bundle();
                        args.putString(InfoFragment.ESTRENO, pelicula.getFecha());
                        args.putString(InfoFragment.DURACION, pelicula.getDuracion());
                        args.putString(InfoFragment.CARATULA, pelicula.getUrlCaratula());
                        info.setArguments(args);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, info).commit();

                        return true;

                    case R.id.navigation_actores:
                        ActorFragment actorFragment = new ActorFragment();
                        Bundle args1 = new Bundle();

                        // Enviamos el id de la película, que es lo que necesitamos para encontrar a sus actores en la BD
                        args1.putInt("id_pelicula", pelicula.getId());
//                        args1.putString(ActorFragment.DESCRIPCION, pelicula.getCategoria().getDescripcion());
//                        args1.putString(ActorFragment.NOMBRE, pelicula.getCategoria().getNombre());
                        actorFragment.setArguments(args1);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, actorFragment).commit();


                        return true;

                    case R.id.navigation_argumento:

                        ArgumentoFragment argumentoFragment = new ArgumentoFragment();
                        //Le paso el argumento de la pelicula
                        Bundle args2 = new Bundle();
                        args2.putString(ArgumentoFragment.ARGUMENTO_PELI, pelicula.getArgumento());
                        argumentoFragment.setArguments(args2);
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, argumentoFragment).commit();

                        return true;
                    default:
                        throw new IllegalStateException("Unexpected value: " + item.getItemId());
                }
            }
            return false;
        }
    };
}
