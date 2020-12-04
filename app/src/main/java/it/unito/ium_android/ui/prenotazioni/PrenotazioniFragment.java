package it.unito.ium_android.ui.prenotazioni;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.unito.ium_android.R;
import it.unito.ium_android.requests.Booking;
import it.unito.ium_android.requests.CardsArchiveContainerAdapter;
import it.unito.ium_android.requests.Requests;
import it.unito.ium_android.requests.jsonMessage;
import it.unito.ium_android.ui.booking.BookingFragment;

public class PrenotazioniFragment extends Fragment {

    private PrenotazioniViewModel prenotazioniViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        prenotazioniViewModel =
                new ViewModelProvider(this).get(PrenotazioniViewModel.class);
        View root = inflater.inflate(R.layout.fragment_prenotazioni, container, false);

        Requests userBookingsRequests = new Requests(getActivity(), "getUserBookings", root);
        String data = "action=userBooking&isAndroid=true";
        String url = "http://10.0.2.2:8080/ProgettoTWEB_war_exploded/Controller";
        String method = "GET";
        userBookingsRequests.execute(data, url, method);

        Requests oldUserBookingsRequests = new Requests(getActivity(), "oldUserBookings", root);
        data = "action=oldUserBookings";
        url = "http://10.0.2.2:8080/ProgettoTWEB_war_exploded/Controller";
        method = "GET";
        oldUserBookingsRequests.execute(data, url, method);

        new Task(root, getActivity()).execute(userBookingsRequests, oldUserBookingsRequests);

        return root;
    }

    public static class Task extends AsyncTask<Requests, Void, jsonMessage<List<Booking>>[]> {
        private View view;
        private Activity activity;

        public Task(View view, Activity activity) {
            this.view = view;
            this.activity = activity;
        }

        @Override
        protected jsonMessage<List<Booking>>[] doInBackground(Requests... requests) {
            String s = "";
            jsonMessage<List<Booking>>[] result = new jsonMessage[2];

            try {
                s = requests[0].get();
                result[0] = new Gson().fromJson(s, new TypeToken<jsonMessage<List<Booking>>>() {
                }.getType());
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            try {
                s = requests[1].get();
                result[1] = new Gson().fromJson(s, new TypeToken<jsonMessage<List<Booking>>>() {
                }.getType());
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(jsonMessage<List<Booking>>[] result) {
            super.onPostExecute(result);
            RelativeLayout loadingLayout = (RelativeLayout) this.view.findViewById(R.id.loadingPanel);
            RecyclerView cardsContainer = (RecyclerView) this.view.findViewById(R.id.cardsContainer);
            ConcatAdapter concatAdapter = new ConcatAdapter();

            if (result[0].getMessage().equals("OK"))
                lessonsArchive(result[0].getData(), concatAdapter);
            else {
                Toast.makeText(activity.getApplicationContext(), result[0].getMessage(), Toast.LENGTH_SHORT).show();
            }
            if (result[1].getMessage().equals("OK"))
                oldLessonsArchive(result[1].getData(), concatAdapter);
            else {
                Toast.makeText(activity.getApplicationContext(), result[1].getMessage(), Toast.LENGTH_SHORT).show();
                loadingLayout.setVisibility(view.GONE);
            }

            cardsContainer.setVisibility(view.VISIBLE);
            cardsContainer.setHasFixedSize(true);
            cardsContainer.setLayoutManager(new LinearLayoutManager(this.activity.getApplicationContext(), LinearLayoutManager.VERTICAL, false));
            cardsContainer.setAdapter(concatAdapter);
        }

        private void lessonsArchive(List<Booking> userBookings, ConcatAdapter concatAdapter) {
            TextView noBooking = (TextView) this.view.findViewById(R.id.noBooking);
            if (userBookings.isEmpty()) {
                noBooking.setVisibility(view.VISIBLE);
                return;
            }
            Collections.sort(userBookings);
            RecyclerView.Adapter cardsContainerAdapter = new CardsArchiveContainerAdapter(userBookings);
            concatAdapter.addAdapter(cardsContainerAdapter);
        }

        private void oldLessonsArchive(List<Booking> oldUserBookings, ConcatAdapter concatAdapter) {
            RelativeLayout loadingLayout = (RelativeLayout) this.view.findViewById(R.id.loadingPanel);
            if (oldUserBookings.isEmpty()) {
                loadingLayout.setVisibility(view.GONE);
                return;
            }
            Collections.sort(oldUserBookings);
            loadingLayout.setVisibility(view.GONE);
            RecyclerView.Adapter oldCardsContainerAdapter = new CardsArchiveContainerAdapter(oldUserBookings);
            concatAdapter.addAdapter(oldCardsContainerAdapter);
        }
    }
}
