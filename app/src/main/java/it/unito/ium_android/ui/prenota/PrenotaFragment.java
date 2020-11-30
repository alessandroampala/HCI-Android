package it.unito.ium_android.ui.prenota;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputLayout;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import it.unito.ium_android.R;
import it.unito.ium_android.requests.Requests;
import it.unito.ium_android.requests.SpinAdapterDocenti;
import it.unito.ium_android.requests.SpinAdapterMaterie;

public class PrenotaFragment extends Fragment {

    private PrenotaViewModel prenotaViewModel;
    private String materia = "";
    private String docente = "";
    private boolean firstTimeSpinner = true;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        prenotaViewModel =
                new ViewModelProvider(this).get(PrenotaViewModel.class);
        View root = inflater.inflate(R.layout.fragment_prenota, container, false);

        RelativeLayout loadingLayout = (RelativeLayout) root.findViewById(R.id.loadingPanel);



        Requests requests = new Requests(getActivity(), "docenti", root);

        String data = "";
        String url = "http://10.0.2.2:8080/ProgettoTWEB_war_exploded/Controller?action=docenti";
        String method = "GET";
        requests.execute(data, url, method);

        requests = new Requests(getActivity(), "materie", root);

        data = "";
        url = "http://10.0.2.2:8080/ProgettoTWEB_war_exploded/Controller?action=materie";
        method = "GET";
        requests.execute(data, url, method);

        Spinner spinnerDocenti = (Spinner) root.findViewById(R.id.seleziona_docente);
        Spinner spinnerMaterie = (Spinner) root.findViewById(R.id.seleziona_materia);

        spinnerDocenti.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (firstTimeSpinner) {
                    firstTimeSpinner = false;
                    return;
                }
                loadingLayout.setVisibility(root.VISIBLE);
                docente = ((Integer) view.getTag()).toString();
                if (docente.equals("0"))
                    docente = "";

                Requests requests = new Requests(getActivity(), "lessons", root);
                try {
                    String data = "course=" + URLEncoder.encode(materia, "UTF-8") + "&teacherId=" + URLEncoder.encode(docente, "UTF-8") + "&action=lessons";
                    String url = "http://10.0.2.2:8080/ProgettoTWEB_war_exploded/Controller";
                    String method = "POST";
                    requests.execute(data, url, method);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerMaterie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (firstTimeSpinner) {
                    firstTimeSpinner = false;
                    return;
                }
                loadingLayout.setVisibility(root.VISIBLE);
                materia = ((TextView) view).getText().toString();
                if (materia.equals("Seleziona Materia"))
                    materia = "";

                Requests requests = new Requests(getActivity(), "lessons", root);
                try {
                    String data = "course=" + URLEncoder.encode(materia, "UTF-8") + "&teacherId=" + URLEncoder.encode(docente, "UTF-8") + "&action=lessons";
                    String url = "http://10.0.2.2:8080/ProgettoTWEB_war_exploded/Controller";
                    String method = "POST";
                    requests.execute(data, url, method);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return root;
    }
}