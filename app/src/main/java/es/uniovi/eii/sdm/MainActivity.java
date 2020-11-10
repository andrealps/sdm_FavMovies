package es.uniovi.eii.sdm;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import es.uniovi.eii.sdm.modelo.Categoria;
import es.uniovi.eii.sdm.modelo.Pelicula;

public class MainActivity extends AppCompatActivity {
    // Identifiadores de Intents
    public static final String POS_CATEGORIA_SELECCIONADA = "pos_categoria_seleccionada";
    public static final String CATEGORIA_SELECCIONADA = "categoria_seleccionada";
    public static final String CATEGORIA_MODIFICADA = "categoria_modificada";
    // Identificador de activity
    private static final int GESTION_CATEGORIA = 1;

    private Snackbar msgCreaCategoria;

    // Elementos de la vista
    private EditText tituloEdit;
    private EditText sinopsisEdit;
    private EditText duracionEdit;
    private EditText fechaEdit;
    private Spinner spinnerCategoria;

    // Array de categorías
    private List<Categoria> listaCategorias;

    boolean creandoCategoria;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Comprobamos a qué petición se está respondiendo
        if (requestCode == GESTION_CATEGORIA){
            // Nos aseguramos que el resultado fue OK
            if (resultCode == RESULT_OK){
                Categoria cateAux = data.getParcelableExtra(CATEGORIA_MODIFICADA);
                Log.d("FavMovies.MainActivity",cateAux.toString());

                if (creandoCategoria){
                    // Añadimos categoria a la lista
                    listaCategorias.add(cateAux);
                    introListaSpinner(spinnerCategoria, listaCategorias);
                } else {
                    // Busca la categoría del mismo nombre en la lista y cambia la descripción
                    for (Categoria cat: listaCategorias){
                        if (cat.getNombre().equals(cateAux.getNombre())){
                            cat.setDescripcion(cateAux.getDescripcion());
                            Log.d("FavMovies.MainActivity", "Modificada la descripción de " + cateAux.getNombre());
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Mis pelis favoritas");
        //Toast.makeText(getApplicationContext(),getString(R.string.OnCreate), Toast.LENGTH_SHORT).show();

        // Recupera campos edición
        tituloEdit = (EditText) findViewById(R.id.idTituloTexto);
        sinopsisEdit = (EditText) findViewById(R.id.idSinopsis);
        duracionEdit = (EditText) findViewById(R.id.idDuracionTexto);
        fechaEdit = (EditText) findViewById(R.id.idFecha);

        // Recepción de los datos de MainRecycler
        Intent intent = getIntent();
        Pelicula pelSel = intent.getParcelableExtra(MainRecycler.PELICULA_SELECCIONADA);
        final TextView tituloPeli = (TextView) findViewById(R.id.titulopeli);
        final TextView fechaEstreno = (TextView) findViewById(R.id.fechaestreno);

        tituloEdit.setText(pelSel.getTitulo());
        sinopsisEdit.setText(pelSel.getArgumento());
        duracionEdit.setText(pelSel.getDuracion());
        fechaEdit.setText(pelSel.getFecha());

        // Inicializa el modelo de datos
        listaCategorias = new ArrayList<Categoria>();

        // Añadimos la categoría de la pelicula sacada del MainRecycler
        //listaCategorias.add(pelSel.getCategoria());

        listaCategorias.add(new Categoria("Acción", "Películas de acción"));
        listaCategorias.add(new Categoria("Comedia", "Películas de comedia"));
        // Inicializa el spinner
        spinnerCategoria = (Spinner) findViewById(R.id.idSpinnerCategoria);
        // Pasamos la categoria sacada del MainRecycler
        introListaSpinner(spinnerCategoria, listaCategorias);

        // Elegimos la categoria de la peli seleccionada
        int i = 1; // a partir de 1 porque la 0 es "Sin definir"
        for (Categoria cat: listaCategorias){
            if (cat.getNombre().equals(pelSel.getCategoria().getNombre())){
                spinnerCategoria.setSelection(i);
                break;
            }
            i++;
        }

        /*
        Definir el observer para el evento click del ratón.
        Recuperamos referencia al botón
         */
        Button btnGuardar = (Button) findViewById(R.id.idGuardar);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Campos correctos
                if (validarCampos()) {
                    // TODO: coordinator layout
                    Snackbar.make(findViewById(R.id.layoutPrincipal), R.string.msg_guardado, Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        Button btnModifiCategoria  = (Button) findViewById(R.id.idBotonlapiz);

        btnModifiCategoria.setVisibility(View.GONE);

        btnModifiCategoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner spinerCategoria = (Spinner) findViewById(R.id.idSpinnerCategoria);

                // Sin definir categoría
                if (spinerCategoria.getSelectedItemPosition() == 0){
                    msgCreaCategoria = Snackbar.make(findViewById(R.id.layoutPrincipal), R.string.msg_crear_nueva_categoria, Snackbar.LENGTH_LONG);
                } else {
                    msgCreaCategoria = Snackbar.make(findViewById(R.id.layoutPrincipal), R.string.msg_modif_categoria, Snackbar.LENGTH_LONG);
                }

                /*
                // Acción de cancelar
                msgCreaCategoria.setAction(android.R.string.cancel, (v1) -> {
                    Snackbar.make(findViewById(R.id.layoutPrincipal), R.string.msg_accion_cancelada, Snackbar.LENGTH_LONG).show();
                });
                msgCreaCategoria.show();
                 */

                // Acción de crear una nueva categoría
                msgCreaCategoria.setAction(android.R.string.ok, new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        Snackbar.make(findViewById(R.id.layoutPrincipal), R.string.msg_accion_ok, Snackbar.LENGTH_LONG).show();
                        modificarCategoria();
                    }
                });
                msgCreaCategoria.show();
            }
        });
    }

    // Introduce las categorías en el spinner
    private void introListaSpinner(Spinner spinnerCategoria, List<Categoria> listaCategorias) {
        // Creamos un nuevo array sólo con los nombres de las categorías
        ArrayList<String> nombres = new ArrayList<>();
        nombres.add("Sin definir");
        for (Categoria categoria: listaCategorias)
            nombres.add(categoria.getNombre());

        // Crea un ArrayAdapter usando un array de string y el layout por defecto del spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nombres);
        // Especifica el layout para usar cuando aparece la lista de elecciones
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Aplicar el adapter al spinner
        spinnerCategoria.setAdapter(adapter);
    }

    // Redirige a la nueva activity CategoriaActivity para modificar la categoría
    private void modificarCategoria() {
        Intent categoriaIntent = new Intent(MainActivity.this, CategoriaActivity.class);
        // Paso de datos
        categoriaIntent.putExtra(POS_CATEGORIA_SELECCIONADA, spinnerCategoria.getSelectedItemPosition());
        creandoCategoria = true;
        // Sin definir categoría -> se crea
        if (spinnerCategoria.getSelectedItemPosition() > 0){
            creandoCategoria = false;
            categoriaIntent.putExtra(CATEGORIA_SELECCIONADA, listaCategorias.get(spinnerCategoria.getSelectedItemPosition() - 1));
        }
        // No se espera resultado
        //startActivity(categoriaIntent);

        // Lanzamos activity para gestionar categoría esperando por un resultado para MainActivity
        startActivityForResult(categoriaIntent, GESTION_CATEGORIA);
    }

    // Valida los campos vacíos. Si lo están se muestra un mensaje de error con un tooltip
    public boolean validarCampos(){
        // Título
        if (tituloEdit.getText().toString().isEmpty()){
            tituloEdit.setError("El título no puede estar vacío");
            tituloEdit.requestFocus();
            return false;
        }
        // Sinopsis
        if (sinopsisEdit.getText().toString().isEmpty()){
            sinopsisEdit.setError("La sinopsis no puede estar vacía");
            sinopsisEdit.requestFocus();
            return false;
        }
        // Duración
        if (duracionEdit.getText().toString().isEmpty()){
            duracionEdit.setError("La duración no puede estar vacía");
            duracionEdit.requestFocus();
            return false;
        }
        // Fecha
        if (fechaEdit.getText().toString().isEmpty()){
            fechaEdit.setError("La duración no puede estar vacía");
            fechaEdit.requestFocus();
            return false;
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Toast.makeText(getApplicationContext(),getString(R.string.OnStart), Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //Toast.makeText(getApplicationContext(),getString(R.string.OnResume), Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        //Toast.makeText(getApplicationContext(),getString(R.string.OnRestart), Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //Toast.makeText(getApplicationContext(),getString(R.string.OnPause), Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onStop() {
        super.onStop();
        //Toast.makeText(getApplicationContext(),getString(R.string.OnStop), Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Toast.makeText(getApplicationContext(),getString(R.string.OnDestroy), Toast.LENGTH_SHORT).show();
    }
}