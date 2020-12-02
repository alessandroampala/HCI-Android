package it.unito.ium_android.ui.booking;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BookingViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public BookingViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

}