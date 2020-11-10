package es.uniovi.eii.sdm.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import es.uniovi.eii.sdm.R;

public class ArgumentoFragment extends Fragment {

    public static final String ARGUMENTO_PELI = "Argumento";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){

        View root = inflater.inflate(R.layout.fragment_argumento, container, false);

        // Referencias componentes
        final TextView targumento = root.findViewById(R.id.text_argumento);

        // Cogemos los datos que nos pasaron
        Bundle args = getArguments();
        if (args != null){
            targumento.setText(args.getString(ARGUMENTO_PELI));
        }
        return root;
    }
}
