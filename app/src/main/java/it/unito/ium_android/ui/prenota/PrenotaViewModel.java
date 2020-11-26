package it.unito.ium_android.ui.prenota;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class PrenotaViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public PrenotaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}