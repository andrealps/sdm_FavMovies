package es.uniovi.eii.sdm.datos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import es.uniovi.eii.sdm.modelo.Actor;
import es.uniovi.eii.sdm.modelo.RepartoPelicula;

import java.util.ArrayList;
import java.util.List;

/**
 * Ejemplo <b>SQLite</b>. Ejemplo de uso de SQLite.
 * <p>
 * DAO para la tabla de actor.
 * Se encarga de abrir y cerrar la conexion, asi como hacer las consultas relacionadas con la tabla Actor
 */
public class RepartoPeliculaDataSource {
    /**
     * Referencia para manejar la base de datos. Este objeto lo obtenemos a partir de MyDBHelper
     * y nos proporciona metodos para hacer operaciones
     * CRUD (create, read, update and delete)
     */
    private SQLiteDatabase database;
    /**
     * Referencia al helper que se encarga de crear y actualizar la base de datos.
     */
    private MyDBHelper dbHelper;
    /**
     * Columnas de la tabla
     */
    private final String[] allColumns = {MyDBHelper.COLUMNA_ID_REPARTO, MyDBHelper.COLUMNA_ID_PELICULAS,
            MyDBHelper.COLUMNA_PERSONAJE};

    /**
     * Constructor.
     *
     * @param context
     */
    public RepartoPeliculaDataSource(Context context) {
        //el último parámetro es la versión
        dbHelper = new MyDBHelper(context, null, null, 1);
    }

    /**
     * Abre una conexion para escritura con la base de datos.
     * Esto lo hace a traves del helper con la llamada a getWritableDatabase. Si la base de
     * datos no esta creada, el helper se encargara de llamar a onCreate
     *
     * @throws SQLException
     */
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    /**
     * Cierra la conexion con la base de datos
     */
    public void close() {
        dbHelper.close();
    }


    public long createrepartopeli(RepartoPelicula repPeli) {
        // Establecemos los valores que se insertaran
        ContentValues values = new ContentValues();

        values.put(MyDBHelper.COLUMNA_ID_REPARTO, repPeli.getId_reparto());
        values.put(MyDBHelper.COLUMNA_ID_PELICULAS, repPeli.getId_pelicula());
        values.put(MyDBHelper.COLUMNA_PERSONAJE, repPeli.getNombre_personaje());

        // Insertamos la valoracion
        long insertId =
                database.insert(MyDBHelper.TABLA_PELICULAS_REPARTO, null, values);

        return insertId;
    }

    /**
     * Obtiene todas las valoraciones andadidas por los usuarios. Sin ninguna restricción SQL
     *
     * @return Lista de objetos de tipo RepartoPelicula
     */
    public List<RepartoPelicula> getAllValorations() {
        // Lista que almacenara el resultado
        List<RepartoPelicula> repPeliList = new ArrayList<>();
        //hacemos una query porque queremos devolver un cursor
        Cursor cursor = database.query(MyDBHelper.TABLA_REPARTO, allColumns,
                null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            final RepartoPelicula repPeli = new RepartoPelicula();
            repPeli.setId_reparto(cursor.getInt(0));
            repPeli.setId_pelicula(cursor.getInt(1));
            repPeli.setNombre_personaje(cursor.getString(2));

            repPeliList.add(repPeli);
            cursor.moveToNext();
        }

        cursor.close();

        // Una vez obtenidos todos los datos y cerrado el cursor, devolvemos la
        // lista.
        return repPeliList;
    }
}