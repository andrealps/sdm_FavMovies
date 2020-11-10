package es.uniovi.eii.sdm;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    String name;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        ActionBar actionBar = getSupportActionBar();
        // Comprobación para evitar errores
        if (actionBar != null){
            // En la ActionBar poner una flecha para volver hacia atrás
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat{
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    // Se ejecuta al clicar la flecha de volver hacia atrás
    @Override
    protected void onPause() {
        super.onPause();

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        // Cogemos la opción seleccionada por el usuario en Settings
        name = sharedPreferences.getString("keyCategoria", "");
        Log.i("Categoria", name);

        MainRecycler.filtroCategoria = name;
    }
}
