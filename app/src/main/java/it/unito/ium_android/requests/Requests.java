package it.unito.ium_android.requests;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.navigation.Navigation;

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
import java.util.List;

import it.unito.ium_android.MainActivity;
import it.unito.ium_android.R;
import it.unito.ium_android.ui.prenotazioni.PrenotazioniFragment;

public class Requests extends AsyncTask<String, String, String> {
    private final Activity activity;
    private final String className;
    private final View view;
    public final static String url = "http://10.0.2.2:8080/ProgettoTWEB_war_exploded/Controller";
    //public final static String url = "http://192.168.1.111:8080/ProgettoTWEB_war_exploded/Controller";

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
        StringBuilder concatStrings = new StringBuilder();

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
        } catch (NullPointerException e) {
            e.printStackTrace();
            return null;
        }
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; utf-8");
        connection.setRequestProperty("Content-Length", String.valueOf(strings[0].length()));
        if (this.className.equals("getSessionLogin") || this.className.equals("getUserBookings") || this.className.equals("oldUserBookings") || this.className.equals("cancelBooking") || this.className.equals("markBooking") || this.className.equals("logout") || this.className.equals("prenotazioniDocente") || this.className.equals("userBookings") || this.className.equals("bookLessons")) {
            SharedPreferences sharedPref = this.activity.getPreferences(Context.MODE_PRIVATE);
            String sessionId = "";
            if (sharedPref!=null && sharedPref.contains("sessionId"))
                sessionId = sharedPref.getString("sessionId", "");
            else
                sessionId = "";
            connection.setRequestProperty("cookie", sessionId);
        }
        connection.setDoInput(true);
        connection.setDoOutput(true);

        DataOutputStream out;
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
                concatStrings.append(decodedString);
            }

            if (this.className.equals("login")) {
                String cookie = connection.getHeaderField("set-cookie");
                SharedPreferences sharedPref = this.activity.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("sessionId", cookie.substring(0, cookie.indexOf(";")));
                editor.apply();
            } else if (this.className.equals("logout")) {
                SharedPreferences sharedPref = this.activity.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.clear();
                editor.apply();
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.disconnect();
        }

        return concatStrings.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (s == null) {
            Toast.makeText(activity.getBaseContext(), "Connection error", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (this.className) {
            case "login":
                login(s);
                break;
            case "getSessionLogin":
                sessionCheck(s);
                break;
            case "cancelBooking":
                cancelBooking(s);
                break;
            case "markBooking":
                markBooking(s);
                break;
            case "bookLessons":
                bookLessons(s);
                break;
            case "logout":
                logout("Logged out");
                break;
        }

    }

    private void login(String s) {
        jsonMessage<User> result = new Gson().fromJson(s, new TypeToken<jsonMessage<User>>() {
        }.getType());
        NavigationView navigationView = activity.findViewById(R.id.nav_view);
        TextView username = navigationView.findViewById(R.id.usernameTextView);
        if (result.getMessage().equals("OK")) {
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_prenotazioni).setVisible(true);
            navigationView.getMenu().getItem(0).setChecked(true);
            Navigation.findNavController(activity, R.id.nav_host_fragment).popBackStack();
            navigationView.setCheckedItem(R.id.nav_prenota);
            Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(R.id.nav_prenota);
            username.setText(result.getData().getUsername());
            ((MainActivity) activity).setLoggedIn(true);
            hideKeyboard(activity);
            navigationView.setCheckedItem(R.id.nav_prenota);
        } else {
            Toast.makeText(activity.getBaseContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_prenotazioni).setVisible(false);
            username.setText(R.string.ospite);
            ((MainActivity) activity).setLoggedIn(false);
        }
    }

    private void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void sessionCheck(String s) {
        jsonMessage<User> result = new Gson().fromJson(s, new TypeToken<jsonMessage<User>>() {
        }.getType());
        NavigationView navigationView = activity.findViewById(R.id.nav_view);
        TextView username = navigationView.findViewById(R.id.usernameTextView);
        if (result.getMessage().equals("Sessione valida")) {
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_prenotazioni).setVisible(true);
            navigationView.getMenu().getItem(0).setChecked(true);
            username.setText(result.getData().getUsername());
            ((MainActivity) activity).setLoggedIn(true);
        } else {
            navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_prenotazioni).setVisible(false);
            ((MainActivity) activity).setLoggedIn(false);
        }
    }

    private void cancelBooking(String s) {
        jsonMessage<List<Object>> result = new Gson().fromJson(s, new TypeToken<jsonMessage<List<Object>>>() {
        }.getType());
        if (result.getMessage().equals("OK")) {
            Requests userBookingsRequests = new Requests(activity, "getUserBookings", view);
            String data = "action=userBooking&isAndroid=true";
            String method = "GET";
            userBookingsRequests.execute(data, url, method);

            Requests oldUserBookingsRequests = new Requests(activity, "oldUserBookings", view);
            data = "action=oldUserBookings";
            method = "GET";
            oldUserBookingsRequests.execute(data, url, method);

            new PrenotazioniFragment.Task(view, activity).execute(userBookingsRequests, oldUserBookingsRequests);
        } else if (result.getMessage().equals("Not logged in")) {
            logout("Not logged in");
        } else {
            Toast.makeText(activity.getBaseContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void markBooking(String s) {
        jsonMessage<List<Object>> result = new Gson().fromJson(s, new TypeToken<jsonMessage<List<Object>>>() {
        }.getType());
        if (result.getMessage().equals("OK")) {
            Requests userBookingsRequests = new Requests(activity, "getUserBookings", view);
            String data = "action=userBooking&isAndroid=true";
            String method = "GET";
            userBookingsRequests.execute(data, url, method);

            Requests oldUserBookingsRequests = new Requests(activity, "oldUserBookings", view);
            data = "action=oldUserBookings";
            method = "GET";
            oldUserBookingsRequests.execute(data, url, method);

            new PrenotazioniFragment.Task(view, activity).execute(userBookingsRequests, oldUserBookingsRequests);
        } else if (result.getMessage().equals("Not logged in")) {
            logout("Not logged in");
        } else {
            Toast.makeText(activity.getBaseContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void bookLessons(String s) {
        jsonMessage<List<Object>> result = new Gson().fromJson(s, new TypeToken<jsonMessage<List<Object>>>() {
        }.getType());
        if (result.getMessage().equals("OK")) {
            Toast.makeText(activity.getBaseContext(), "Prenotazione effettuata", Toast.LENGTH_SHORT).show();
        } else if (result.getMessage().equals("Not logged in")) {
            logout("Not logged in");
        } else {
            Toast.makeText(activity.getBaseContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void logout(String s) {
        ((MainActivity) activity).setLoggedIn(false);
        NavigationView navigationView = activity.findViewById(R.id.nav_view);
        Navigation.findNavController(activity, R.id.nav_host_fragment).popBackStack();
        navigationView.setCheckedItem(R.id.nav_prenota);
        Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(R.id.nav_prenota);
        Toast.makeText(activity.getBaseContext(), s, Toast.LENGTH_SHORT).show();
        navigationView.getMenu().findItem(R.id.nav_login).setVisible(true);
        navigationView.getMenu().findItem(R.id.nav_logout).setVisible(false);
        navigationView.getMenu().findItem(R.id.nav_prenotazioni).setVisible(false);
        TextView username = navigationView.findViewById(R.id.usernameTextView);
        activity.findViewById(R.id.bookButton).setVisibility(View.GONE);
        username.setText(R.string.ospite);
    }
}
