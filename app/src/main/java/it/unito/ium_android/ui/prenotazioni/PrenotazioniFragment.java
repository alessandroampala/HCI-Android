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

import it.unito.ium_android.R;

public class PrenotazioniFragment extends Fragment {

    private PrenotazioniViewModel prenotazioniViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        prenotazioniViewModel =
                new ViewModelProvider(this).get(PrenotazioniViewModel.class);
        View root = inflater.inflate(R.layout.fragment_prenotazioni, container, false);

        return root;
    }
}