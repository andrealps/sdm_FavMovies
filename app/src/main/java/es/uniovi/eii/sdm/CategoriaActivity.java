package es.uniovi.eii.sdm;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import es.uniovi.eii.sdm.modelo.Categoria;

public class CategoriaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoria);

        // Recepción de los datos
        Intent intent = getIntent();
        int posCategoria = intent.getIntExtra(MainActivity.POS_CATEGORIA_SELECCIONADA, 0);
        Categoria categEntrada = null;

        // Si se ha elegido alguna categoría
        if (posCategoria > 0)
            categEntrada = intent.getParcelableExtra(MainActivity.CATEGORIA_SELECCIONADA);

        TextView textViewCrea = (TextView) findViewById(R.id.idNuevaCategoria);
        final EditText editNomCategoria = (EditText) findViewById(R.id.idNombreNuevaCategoriaText);
        final EditText editDescripcion = (EditText) findViewById(R.id.idDescripcionCategoriaText);

        // Recuperamos referencia al botón
        Button btnOk = (Button) findViewById(R.id.idOkNuevaCategoria);
        Button btnCancel = (Button) findViewById(R.id.idCancelNuevaCategoria);

        // Ponemos etiqueta título en función de si hay que crear/modificar categoría
        if (posCategoria == 0)
            textViewCrea.setText(R.string.msg_crear_nueva_categoria);
        else {
            textViewCrea.setText(R.string.msg_modif_categoria);
            editNomCategoria.setText(categEntrada.getNombre());
            editDescripcion.setText(categEntrada.getDescripcion());
            // No dejamos cambiar el nombre de la categoría
            editNomCategoria.setEnabled(false);
        }

        // Definir el observer para el evento click del botón guardar
        // Definimos listener
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Categoria categSalida = new Categoria(editNomCategoria.getText().toString(),
                        editDescripcion.getText().toString());
                Intent intentResultado = new Intent();
                intentResultado.putExtra(MainActivity.CATEGORIA_MODIFICADA, categSalida);
                setResult(RESULT_OK, intentResultado);
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }
}