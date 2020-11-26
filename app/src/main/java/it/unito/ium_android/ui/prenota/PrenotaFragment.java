package it.unito.ium_android.ui.prenota;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import it.unito.ium_android.R;
import it.unito.ium_android.Request;

public class PrenotaFragment extends Fragment {

    private PrenotaViewModel prenotaViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        prenotaViewModel =
                new ViewModelProvider(this).get(PrenotaViewModel.class);
        View root = inflater.inflate(R.layout.fragment_prenota, container, false);

        Request post = new Request(getActivity().getApplicationContext(), "getSessionLogin");

        String data = "action=getSessionLogin";
        String url = "http://10.0.2.2:8080/ProgettoTWEB_war_exploded/Controller";
        String method = "POST";
        post.execute(data, url, method);

        return root;
    }
}