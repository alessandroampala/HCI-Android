package it.unito.ium_android.ui.prenotazioni;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PrenotazioniViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PrenotazioniViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}