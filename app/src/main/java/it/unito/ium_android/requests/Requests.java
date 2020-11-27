package it.unito.ium_android.requests;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import it.unito.ium_android.R;

public class Requests extends AsyncTask<String, String, String> {
    private Context context;
    private String className;
    private View view;

    public Requests(Context context, String className) {
        this.context = context;
        this.className = className;
        this.view = null;
    }

    public Requests(Context context, String className, View view) {
        this.context = context;
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
        }

    }

    private void login(String s) {
        jsonMessage<User> result = new Gson().fromJson(s, new TypeToken<jsonMessage<User>>() {
        }.getType());

        if (result.getMessage().equals("Ok")) {
            Toast.makeText(context, "login fatto", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void sessionCheck(String s) {
        jsonMessage<User> result = new Gson().fromJson(s, new TypeToken<jsonMessage<User>>() {
        }.getType());

        if (result.getMessage().equals("Sessione valida")) {
            Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void lessons(String s) {
        jsonMessage<List<Lesson>> result = new Gson().fromJson(s, new TypeToken<jsonMessage<List<Lesson>>>() {
        }.getType());

        if (result.getMessage().equals("Ok")) {
            RecyclerView cardsContainer = (RecyclerView) this.view.findViewById(R.id.cardsContainer);
            cardsContainer.setHasFixedSize(true);
            cardsContainer.setLayoutManager(new LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false));

            RecyclerView.Adapter cardsContainerAdapter = new CardsContainerAdapter(result.getData());

            cardsContainer.setAdapter(cardsContainerAdapter);
        } else {
            Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void docenti(String s) {
        jsonMessage<List<Teacher>> result = new Gson().fromJson(s, new TypeToken<jsonMessage<List<Teacher>>>() {
        }.getType());

        if (result.getMessage().equals("Ok")) {
            SpinAdapterDocenti spinAdapterDocenti = new SpinAdapterDocenti(this.context, android.R.layout.simple_spinner_dropdown_item, result.getData());
            Spinner spinnerDocenti = (Spinner) this.view.findViewById(R.id.seleziona_docente);
            spinnerDocenti.setAdapter(spinAdapterDocenti);
        } else {
            Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void materie(String s) {
        Log.e("POST", "re)");
        jsonMessage<List<Course>> result = new Gson().fromJson(s, new TypeToken<jsonMessage<List<Course>>>() {
        }.getType());
        Log.e("POST", "result.getMessage()");
        if (result.getMessage().equals("Ok")) {
            SpinAdapterMaterie spinAdapterMaterie = new SpinAdapterMaterie(this.context, android.R.layout.simple_spinner_dropdown_item, result.getData());
            Spinner spinnerMaterie = (Spinner) this.view.findViewById(R.id.seleziona_materia);
            spinnerMaterie.setAdapter(spinAdapterMaterie);
        } else {
            Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
