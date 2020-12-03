package it.unito.ium_android.requests;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import it.unito.ium_android.MainActivity;
import it.unito.ium_android.R;

public class Requests extends AsyncTask<String, String, String> {
    private Activity activity;
    private String className;
    private View view;

    public Requests(Activity activity, String className) {
        this.activity = activity;
        this.className = className;
        this.view = null;
    }

    public Requests(Activity activity, String className, View view) {
        this.activity = activity;
        this.className = className;
        this.view = view;
    }

    @Override
    protected String doInBackground(String... strings) {
        String concatStrings = "";

        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) new URL(strings[1]).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            connection.setRequestMethod(strings[2]);
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; utf-8");
        connection.setRequestProperty("Content-Length", String.valueOf(strings[0].length()));
        if (this.className.equals("getSessionLogin") || this.className.equals("getUserBookings") || this.className.equals("oldUserBookings") || this.className.equals("disdici") || this.className.equals("svolta") || this.className.equals("logout") || this.className.equals("prenotazioniDocente") || this.className.equals("userBookings") || this.className.equals("prenotaLezioni")) {
            SharedPreferences sharedPref = this.activity.getPreferences(this.activity.MODE_PRIVATE);
            String sessionId = "";
            if (sharedPref.contains("sessionId"))
                sessionId = sharedPref.getString("sessionId", "");
            connection.setRequestProperty("cookie", sessionId);
        }
        connection.setDoInput(true);
        connection.setDoOutput(true);

        DataOutputStream out = null;
        try {
            out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(strings[0]);
            out.flush();
            out.close();

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            connection.getInputStream()));
            String decodedString;
            while ((decodedString = in.readLine()) != null) {
                concatStrings += decodedString;
            }

            if (this.className.equals("login")) {
                String cookie = connection.getHeaderField("set-cookie");
                SharedPreferences sharedPref = this.activity.getPreferences(this.activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("sessionId", cookie.substring(0, cookie.indexOf(";")));
                editor.apply();
            } else if (this.className.equals("logout")) {
                SharedPreferences sharedPref = this.activity.getPreferences(this.activity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                editor.apply();
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null)
                connection.disconnect();
        }

        return concatStrings;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        switch (this.className) {
            case "login":
                login(s);
                break;
            case "getSessionLogin":
                sessionCheck(s);
                break;
            case "lessons":
                lessons(s);
                break;
            case "docenti":
                docenti(s);
                break;
            case "materie":
                materie(s);
                break;
            case "getUserBookings":
                lessonsArchive(s);
                break;
            case "oldUserBookings":
                oldLessonsArchive(s);
                break;
            case "disdici":
                disdici(s);
                break;
            case "svolta":
                svolta(s);
                break;
            case "prenotaLezioni":
                prenotaLezioni(s);
                break;
            case "logout":
                logout(s);
                break;
        }

    }

    private void login(String s) {
        jsonMessage<User> result = new Gson().fromJson(s, new TypeToken<jsonMessage<User>>() {
        }.getType());
        NavigationView navigationView = activity.findViewById(R.id.nav_view);
        TextView username = navigationView.findViewById(R.id.usernameTextView);
        if (result.getMessage().equals("Ok")) {
            Toast.makeText(activity.getApplicationContext(), "login fatto", Toast.LENGTH_SHORT).show();
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_prenotazioni).setVisible(true);
            navigationView.getMenu().getItem(0).setChecked(true);
            Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(R.id.nav_prenota);
            username.setText(result.getData().getUsername());
        } else {
            Toast.makeText(activity.getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_prenotazioni).setVisible(false);
            username.setText("Ospite");
        }
    }

    private void sessionCheck(String s) {
        jsonMessage<User> result = new Gson().fromJson(s, new TypeToken<jsonMessage<User>>() {
        }.getType());

        NavigationView navigationView = activity.findViewById(R.id.nav_view);
        TextView username = navigationView.findViewById(R.id.usernameTextView);
        if (result.getMessage().equals("Sessione valida")) {
            Toast.makeText(activity.getApplicationContext(), result.getData().getUsername(), Toast.LENGTH_SHORT).show();
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_prenotazioni).setVisible(true);
            navigationView.getMenu().getItem(0).setChecked(true);
            username.setText(result.getData().getUsername());
        } else {
            Toast.makeText(activity.getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_prenotazioni).setVisible(false);
        }
    }

    private void lessons(String s) {
        jsonMessage<List<Lesson>> result = new Gson().fromJson(s, new TypeToken<jsonMessage<List<Lesson>>>() {
        }.getType());

        RelativeLayout loadingLayout = (RelativeLayout) this.view.findViewById(R.id.loadingPanel);
        RecyclerView cardsContainer = (RecyclerView) this.view.findViewById(R.id.cardsContainer);
        TextView noLessons = (TextView) this.view.findViewById(R.id.noLessons);

        if (result.getMessage().equals("Ok")) {
            Spinner spinnerDocenti = (Spinner) this.view.findViewById(R.id.seleziona_docente);
            Spinner spinnerMaterie = (Spinner) this.view.findViewById(R.id.seleziona_materia);
            if (spinnerDocenti.getVisibility() == view.VISIBLE && spinnerMaterie.getVisibility() == view.VISIBLE && loadingLayout.getVisibility() == view.VISIBLE)
                loadingLayout.setVisibility(view.GONE);
            noLessons.setVisibility(view.GONE);
            cardsContainer.setVisibility(view.VISIBLE);
            cardsContainer.setHasFixedSize(true);
            cardsContainer.setLayoutManager(new LinearLayoutManager(this.activity.getApplicationContext(), LinearLayoutManager.VERTICAL, false));
            RecyclerView.Adapter cardsContainerAdapter = new CardsContainerAdapter(result.getData(), (MainActivity) activity);

            cardsContainer.setAdapter(cardsContainerAdapter);
        } else {
            Toast.makeText(activity.getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
            loadingLayout.setVisibility(view.GONE);
            cardsContainer.setVisibility(view.GONE);
            noLessons.setVisibility(view.VISIBLE);
        }
    }

    private void docenti(String s) {
        jsonMessage<List<Teacher>> result = new Gson().fromJson(s, new TypeToken<jsonMessage<List<Teacher>>>() {
        }.getType());

        if (result.getMessage().equals("Ok")) {
            RecyclerView cardsContainer = (RecyclerView) this.view.findViewById(R.id.cardsContainer);
            Spinner spinnerDocenti = (Spinner) this.view.findViewById(R.id.seleziona_docente);
            Spinner spinnerMaterie = (Spinner) this.view.findViewById(R.id.seleziona_materia);
            RelativeLayout loadingLayout = (RelativeLayout) this.view.findViewById(R.id.loadingPanel);
            if (cardsContainer.getVisibility() == view.VISIBLE && spinnerMaterie.getVisibility() == view.VISIBLE)
                loadingLayout.setVisibility(view.GONE);
            spinnerDocenti.setVisibility(view.VISIBLE);
            SpinAdapterDocenti spinAdapterDocenti = new SpinAdapterDocenti(this.activity.getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, result.getData());
            spinnerDocenti.setAdapter(spinAdapterDocenti);
        } else {
            Toast.makeText(activity.getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void materie(String s) {
        jsonMessage<List<Course>> result = new Gson().fromJson(s, new TypeToken<jsonMessage<List<Course>>>() {
        }.getType());
        if (result.getMessage().equals("Ok")) {
            RecyclerView cardsContainer = (RecyclerView) this.view.findViewById(R.id.cardsContainer);
            Spinner spinnerDocenti = (Spinner) this.view.findViewById(R.id.seleziona_docente);
            Spinner spinnerMaterie = (Spinner) this.view.findViewById(R.id.seleziona_materia);
            RelativeLayout loadingLayout = (RelativeLayout) this.view.findViewById(R.id.loadingPanel);
            if (cardsContainer.getVisibility() == view.VISIBLE && spinnerDocenti.getVisibility() == view.VISIBLE)
                loadingLayout.setVisibility(view.GONE);
            spinnerMaterie.setVisibility(view.VISIBLE);
            SpinAdapterMaterie spinAdapterMaterie = new SpinAdapterMaterie(this.activity.getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, result.getData());
            spinnerMaterie.setAdapter(spinAdapterMaterie);
        } else {
            Toast.makeText(activity.getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void lessonsArchive(String s) {
        jsonMessage<List<Booking>> result = new Gson().fromJson(s, new TypeToken<jsonMessage<List<Booking>>>() {
        }.getType());

        RelativeLayout loadingLayout = (RelativeLayout) this.view.findViewById(R.id.loadingPanel);
        TextView noBooking = (TextView) this.view.findViewById(R.id.noBooking);
        RecyclerView cardsContainer = (RecyclerView) this.view.findViewById(R.id.cardsContainer);

        if (result.getMessage().equals("Ok")) {
            Collections.sort(result.getData());
            loadingLayout.setVisibility(view.GONE);
            cardsContainer.setVisibility(view.VISIBLE);
            cardsContainer.setHasFixedSize(true);
            cardsContainer.setLayoutManager(new LinearLayoutManager(this.activity.getApplicationContext(), LinearLayoutManager.VERTICAL, false));
            RecyclerView.Adapter cardsContainerAdapter = new CardsArchiveContainerAdapter(result.getData());
            cardsContainer.setAdapter(cardsContainerAdapter);
        } else {
            Toast.makeText(activity.getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
            loadingLayout.setVisibility(view.GONE);
            cardsContainer.setVisibility(view.GONE);
            noBooking.setVisibility(view.VISIBLE);
        }
    }

    private void oldLessonsArchive(String s) {
        jsonMessage<List<Booking>> result = new Gson().fromJson(s, new TypeToken<jsonMessage<List<Booking>>>() {
        }.getType());

        RelativeLayout loadingLayout = (RelativeLayout) this.view.findViewById(R.id.loadingPanel);
        RecyclerView oldCardsContainer = (RecyclerView) this.view.findViewById(R.id.oldCardsContainer);

        if (result.getMessage().equals("Ok")) {
            Collections.sort(result.getData());
            loadingLayout.setVisibility(view.GONE);
            oldCardsContainer.setVisibility(view.VISIBLE);
            oldCardsContainer.setHasFixedSize(true);
            oldCardsContainer.setLayoutManager(new LinearLayoutManager(this.activity.getApplicationContext(), LinearLayoutManager.VERTICAL, false));
            RecyclerView.Adapter oldCardsContainerAdapter = new CardsArchiveContainerAdapter(result.getData());
            oldCardsContainer.setAdapter(oldCardsContainerAdapter);
        } else {
            Toast.makeText(activity.getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
            loadingLayout.setVisibility(view.GONE);
            oldCardsContainer.setVisibility(view.GONE);
        }
    }

    private void disdici(String s) {
        jsonMessage<List<Object>> result = new Gson().fromJson(s, new TypeToken<jsonMessage<List<Object>>>() {
        }.getType());
        if (result.getMessage().equals("OK")) {
            Requests requests = new Requests(activity, "getUserBookings", view);
            String data = "action=userBooking&isAndroid=true";
            String url = "http://10.0.2.2:8080/ProgettoTWEB_war_exploded/Controller";
            String method = "GET";
            requests.execute(data, url, method);

            requests = new Requests(activity, "oldUserBookings", view);
            data = "action=oldUserBookings";
            url = "http://10.0.2.2:8080/ProgettoTWEB_war_exploded/Controller";
            method = "GET";
            requests.execute(data, url, method);
        } else {
            Toast.makeText(activity.getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void svolta(String s) {
        jsonMessage<List<Object>> result = new Gson().fromJson(s, new TypeToken<jsonMessage<List<Object>>>() {
        }.getType());
        if (result.getMessage().equals("OK")) {
            Requests requests = new Requests(activity, "getUserBookings", view);
            String data = "action=userBooking&isAndroid=true";
            String url = "http://10.0.2.2:8080/ProgettoTWEB_war_exploded/Controller";
            String method = "GET";
            requests.execute(data, url, method);

            requests = new Requests(activity, "oldUserBookings", view);
            data = "action=oldUserBookings";
            url = "http://10.0.2.2:8080/ProgettoTWEB_war_exploded/Controller";
            method = "GET";
            requests.execute(data, url, method);
        } else {
            Toast.makeText(activity.getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void prenotaLezioni(String s) {
        jsonMessage<List<Object>> result = new Gson().fromJson(s, new TypeToken<jsonMessage<List<Object>>>() {
        }.getType());
        if (result.getMessage().equals("OK")) {
            Toast.makeText(activity.getApplicationContext(), "Prenotazione effettuata", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity.getApplicationContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void logout(String s) {
        NavigationView navigationView = activity.findViewById(R.id.nav_view);
        TextView username = navigationView.findViewById(R.id.usernameTextView);
        Toast.makeText(activity.getApplicationContext(), "Logged out", Toast.LENGTH_SHORT).show();
        Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(R.id.nav_prenota);
        navigationView.setCheckedItem(R.id.nav_prenota);
        navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
        navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_prenotazioni).setVisible(false);
        username.setText("Ospite");
    }
}
