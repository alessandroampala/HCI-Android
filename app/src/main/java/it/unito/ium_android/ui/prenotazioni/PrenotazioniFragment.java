package it.unito.ium_android.ui.prenotazioni;

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
import it.unito.ium_android.requests.Requests;

public class PrenotazioniFragment extends Fragment {

    private PrenotazioniViewModel prenotazioniViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        prenotazioniViewModel =
                new ViewModelProvider(this).get(PrenotazioniViewModel.class);
        View root = inflater.inflate(R.layout.fragment_prenotazioni, container, false);

        Requests requests = new Requests(getActivity(), "getUserBookings", root);
        String data = "action=userBooking&isAndroid=true";
        String url = "http://10.0.2.2:8080/ProgettoTWEB_war_exploded/Controller";
        String method = "GET";
        requests.execute(data, url, method);

        requests = new Requests(getActivity(), "oldUserBookings", root);
        data = "action=oldUserBookings";
        url = "http://10.0.2.2:8080/ProgettoTWEB_war_exploded/Controller";
        method = "GET";
        requests.execute(data, url, method);

        return root;
    }
}