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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.unito.ium_android.R;
import it.unito.ium_android.requests.Booking;
import it.unito.ium_android.requests.CardsArchiveContainerAdapter;
import it.unito.ium_android.requests.Requests;
import it.unito.ium_android.requests.jsonMessage;

public class PrenotazioniFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_prenotazioni, container, false);
        SwipeRefreshLayout refreshPanel = root.findViewById(R.id.refreshPanel);

        makeRequests(root);

        refreshPanel.setOnRefreshListener(() -> {
            makeRequests(root);
            refreshPanel.setRefreshing(false);
        });


        return root;
    }

    private void makeRequests(View root) {
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
    }

    public static class Task extends AsyncTask<Requests, Void, jsonMessage<List<Booking>>[]> {
        private final View view;
        private final Activity activity;

        public Task(View view, Activity activity) {
            this.view = view;
            this.activity = activity;
        }

        @Override
        protected jsonMessage<List<Booking>>[] doInBackground(Requests... requests) {
            String s;
            jsonMessage<List<Booking>>[] result = new jsonMessage[2];

            try {
                s = requests[0].get();
                result[0] = new Gson().fromJson(s, new TypeToken<jsonMessage<List<Booking>>>() {
                }.getType());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            if(result[0].getMessage().equals("Not logged in"))
                requests[0].logout("Not logged in");

            try {
                s = requests[1].get();
                result[1] = new Gson().fromJson(s, new TypeToken<jsonMessage<List<Booking>>>() {
                }.getType());
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            if(result[1].getMessage().equals("Not logged in"))
                requests[1].logout("Not logged in");

            return result;
        }

        @Override
        protected void onPostExecute(jsonMessage<List<Booking>>[] result) {
            super.onPostExecute(result);
            RelativeLayout loadingLayout = this.view.findViewById(R.id.loadingPanel);
            RecyclerView cardsContainer = this.view.findViewById(R.id.cardsContainer);
            ConcatAdapter concatAdapter = new ConcatAdapter();

            if(result[0].getMessage().equals("Not logged in") || result[1].getMessage().equals("Not logged in"))
                return;

            if (result[0].getMessage().equals("OK"))
                lessonsArchive(result[0].getData(), concatAdapter);
            else {
                Toast.makeText(activity.getBaseContext(), result[0].getMessage(), Toast.LENGTH_SHORT).show();
            }
            if (result[1].getMessage().equals("OK"))
                oldLessonsArchive(result[1].getData(), concatAdapter);
            else {
                Toast.makeText(activity.getBaseContext(), result[1].getMessage(), Toast.LENGTH_SHORT).show();
                loadingLayout.setVisibility(View.GONE);
            }

            cardsContainer.setVisibility(View.VISIBLE);
            cardsContainer.setHasFixedSize(true);
            cardsContainer.setLayoutManager(new LinearLayoutManager(this.activity.getApplicationContext(), LinearLayoutManager.VERTICAL, false));
            cardsContainer.setAdapter(concatAdapter);
        }

        private void lessonsArchive(List<Booking> userBookings, ConcatAdapter concatAdapter) {
            TextView noBooking = this.view.findViewById(R.id.noBooking);
            if (userBookings.isEmpty()) {
                noBooking.setVisibility(View.VISIBLE);
                return;
            }
            Collections.sort(userBookings);
            RecyclerView.Adapter cardsContainerAdapter = new CardsArchiveContainerAdapter(userBookings);
            concatAdapter.addAdapter(cardsContainerAdapter);
        }

        private void oldLessonsArchive(List<Booking> oldUserBookings, ConcatAdapter concatAdapter) {
            RelativeLayout loadingLayout = this.view.findViewById(R.id.loadingPanel);
            if (oldUserBookings.isEmpty()) {
                loadingLayout.setVisibility(View.GONE);
                return;
            }
            Collections.sort(oldUserBookings);
            loadingLayout.setVisibility(View.GONE);
            RecyclerView.Adapter oldCardsContainerAdapter = new CardsArchiveContainerAdapter(oldUserBookings);
            concatAdapter.addAdapter(oldCardsContainerAdapter);
        }
    }
}
