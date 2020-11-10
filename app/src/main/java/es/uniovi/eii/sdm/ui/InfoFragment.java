package es.uniovi.eii.sdm.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import es.uniovi.eii.sdm.R;

public class InfoFragment extends Fragment {

    public static final String ESTRENO = "estreno";
    public static final String DURACION = "duracion";
    public static final String CARATULA = "caratula";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){

        View root = inflater.inflate(R.layout.fragment_info, container, false);

        // Referencias componentes
        final TextView testreno = root.findViewById(R.id.estreno);
        final TextView tduracion = root.findViewById(R.id.duracion);
        ImageView caratula = root.findViewById(R.id.caratula);

        // Cogemos los datos que nos pasaron
        Bundle args = getArguments();
        if (args != null){
            testreno.setText(args.getString(ESTRENO));
            tduracion.setText(args.getString(DURACION));

            Picasso.get().load(args.getString(CARATULA)).into(caratula);
        }
        return root;
    }
}
