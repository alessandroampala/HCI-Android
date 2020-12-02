package it.unito.ium_android.ui.booking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import it.unito.ium_android.R;
import it.unito.ium_android.requests.Booking;
import it.unito.ium_android.requests.Requests;
import it.unito.ium_android.ui.login.LoginViewModel;


public class BookingFragment extends Fragment implements View.OnClickListener {

    private BookingViewModel bookingViewModel;
    private List<TextView> week = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        bookingViewModel =
                new ViewModelProvider(this).get(BookingViewModel.class);
        View root = inflater.inflate(R.layout.fragment_booking, container, false);

        addWeekBtn(root);

        for (TextView day : week)
            day.setOnClickListener(this);

        return root;
    }

    private void addWeekBtn(View root) {
        week.add(root.findViewById(R.id.lun));
        week.add(root.findViewById(R.id.mar));
        week.add(root.findViewById(R.id.mer));
        week.add(root.findViewById(R.id.gio));
        week.add(root.findViewById(R.id.ven));
    }

    @Override
    public void onClick(View v) {

        switch (((TextView) v).getText().toString()) {
            case "Lun":
                Toast.makeText(getContext(), ((TextView) v).getText().toString(), Toast.LENGTH_SHORT).show();
                break;
            case "Mar":
                Toast.makeText(getContext(), ((TextView) v).getText().toString(), Toast.LENGTH_SHORT).show();
                break;
            case "Mer":
                Toast.makeText(getContext(), ((TextView) v).getText().toString(), Toast.LENGTH_SHORT).show();
                break;
            case "Gio":
                Toast.makeText(getContext(), ((TextView) v).getText().toString(), Toast.LENGTH_SHORT).show();
                break;
            case "Ven":
                Toast.makeText(getContext(), ((TextView) v).getText().toString(), Toast.LENGTH_SHORT).show();
                break;
        }
    }
}