package it.unito.ium_android.ui.prenota;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.unito.ium_android.MainActivity;
import it.unito.ium_android.R;
import it.unito.ium_android.requests.CardsContainerAdapter;
import it.unito.ium_android.requests.Course;
import it.unito.ium_android.requests.Lesson;
import it.unito.ium_android.requests.Requests;
import it.unito.ium_android.requests.SpinAdapterDocenti;
import it.unito.ium_android.requests.SpinAdapterMaterie;
import it.unito.ium_android.requests.Teacher;
import it.unito.ium_android.requests.jsonMessage;

// Prenota class
public class PrenotaFragment extends Fragment {

    private String materia = "";
    private String docente = "";
    private int firstTimeSpinner = 0;

    // On create inflates view, manages listeners for spinners and for the refresh action
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_prenota, container, false);
        RelativeLayout loadingLayout = root.findViewById(R.id.loadingPanel);
        makeRequests(root);

        Spinner spinnerDocenti = root.findViewById(R.id.seleziona_docente);
        Spinner spinnerMaterie = root.findViewById(R.id.seleziona_materia);
        spinnerDocenti.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (firstTimeSpinner < 2) {
                    firstTimeSpinner++;
                    return;
                }
                loadingLayout.setVisibility(View.VISIBLE);
                docente = view.getTag().toString();
                if (docente.equals("0"))
                    docente = "";

                makeSpinnerRequests(root);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerMaterie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (firstTimeSpinner < 2) {
                    firstTimeSpinner++;
                    return;
                }
                loadingLayout.setVisibility(View.VISIBLE);
                materia = ((TextView) view).getText().toString();
                if (materia.equals("Seleziona Materia"))
                    materia = "";

                makeSpinnerRequests(root);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        SwipeRefreshLayout refreshPanel = root.findViewById(R.id.refreshPanel);
        refreshPanel.setOnRefreshListener(() -> {
            makeRequests(root);
            refreshPanel.setRefreshing(false);
        });

        return root;
    }

    // Requests teachers, courses and lessons
    private void makeRequests(View root) {
        Requests teacherRequests = new Requests(getActivity(), "docenti", root);

        String data = "action=teachers";
        String method = "GET";
        teacherRequests.execute(data, Requests.url, method);

        Requests courseRequests = new Requests(getActivity(), "materie", root);

        data = "action=courses";
        method = "GET";

        courseRequests.execute(data, Requests.url, method);

        Requests lessonRequests = new Requests(getActivity(), "lessons", root);
        try {
            data = "course=" + URLEncoder.encode(materia, "UTF-8") + "&teacherId=" + URLEncoder.encode(docente, "UTF-8") + "&action=lessons";
            method = "POST";
            lessonRequests.execute(data, Requests.url, method);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        new Task(root, getActivity()).execute(teacherRequests, courseRequests, lessonRequests);
    }

    // Requests made by spinners every time an item is selected
    private void makeSpinnerRequests(View root) {
        Requests request = new Requests(getActivity(), "lessons", root);
        try {
            String data = "course=" + URLEncoder.encode(materia, "UTF-8") + "&teacherId=" + URLEncoder.encode(docente, "UTF-8") + "&action=lessons";
            String method = "POST";
            request.execute(data, Requests.url, method);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        new Task(root, getActivity()).execute(request);
    }

    // Resets firstTimeSpinner
    @Override
    public void onResume() {
        super.onResume();
        firstTimeSpinner = 0;
    }

    // Async task used to track the requests
    public static class Task extends AsyncTask<Requests, Void, ArrayList<String>> {
        private final View view;
        private final Activity activity;

        // Constructor
        public Task(View view, Activity activity) {
            this.view = view;
            this.activity = activity;
        }

        // Gets data from the requests and returns it to the onPostExecute
        @Override
        protected ArrayList<String> doInBackground(Requests... requests) {
            ArrayList<String> s = new ArrayList<>();
            if (requests.length > 1) {
                try {
                    s.add(requests[0].get());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    s.add(requests[1].get());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    s.add(requests[2].get());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    s.add(requests[0].get());
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return s;
        }

        // if s is null then there is no connection otherwise loads data
        @Override
        protected void onPostExecute(ArrayList<String> s) {
            super.onPostExecute(s);

            if (s == null) return;

            if (s.size() > 1) {
                if (s.get(0) == null || s.get(1) == null || s.get(2) == null) {
                    Toast.makeText(activity.getBaseContext(), "Connection error", Toast.LENGTH_SHORT).show();
                    return;
                }
                docenti(s.get(0));
                materie(s.get(1));
                lessons(s.get(2));
            } else {
                if (s.get(0) == null) {
                    Toast.makeText(activity.getBaseContext(), "Connection error", Toast.LENGTH_SHORT).show();
                    return;
                }
                lessons(s.get(0));
            }
        }

        // Creates an adapter with the lessons data for the cards
        private void lessons(String s) {
            jsonMessage<List<Lesson>> result = new Gson().fromJson(s, new TypeToken<jsonMessage<List<Lesson>>>() {
            }.getType());

            RelativeLayout loadingLayout = this.view.findViewById(R.id.loadingPanel);
            RecyclerView cardsContainer = this.view.findViewById(R.id.cardsContainer);
            TextView noLessons = this.view.findViewById(R.id.noLessons);

            if (result.getMessage().equals("OK")) {
                if (result.getData().isEmpty()) {
                    loadingLayout.setVisibility(View.GONE);
                    cardsContainer.setVisibility(View.GONE);
                    noLessons.setVisibility(View.VISIBLE);
                    return;
                }
                Spinner spinnerDocenti = this.view.findViewById(R.id.seleziona_docente);
                Spinner spinnerMaterie = this.view.findViewById(R.id.seleziona_materia);
                if (spinnerDocenti.getVisibility() == View.VISIBLE && spinnerMaterie.getVisibility() == View.VISIBLE && loadingLayout.getVisibility() == View.VISIBLE)
                    loadingLayout.setVisibility(View.GONE);
                noLessons.setVisibility(View.GONE);
                cardsContainer.setVisibility(View.VISIBLE);
                cardsContainer.setHasFixedSize(true);
                cardsContainer.setLayoutManager(new LinearLayoutManager(this.activity.getApplicationContext(), LinearLayoutManager.VERTICAL, false));
                RecyclerView.Adapter cardsContainerAdapter = new CardsContainerAdapter(result.getData(), (MainActivity) activity);

                cardsContainer.setAdapter(cardsContainerAdapter);
            } else {
                Toast.makeText(activity.getBaseContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                loadingLayout.setVisibility(View.GONE);
                cardsContainer.setVisibility(View.GONE);
                noLessons.setVisibility(View.VISIBLE);
            }
        }

        // Creates an adapter for spinner with teachers data
        private void docenti(String s) {
            jsonMessage<List<Teacher>> result = new Gson().fromJson(s, new TypeToken<jsonMessage<List<Teacher>>>() {
            }.getType());

            if (result.getMessage().equals("OK")) {
                RecyclerView cardsContainer = this.view.findViewById(R.id.cardsContainer);
                Spinner spinnerDocenti = this.view.findViewById(R.id.seleziona_docente);
                Spinner spinnerMaterie = this.view.findViewById(R.id.seleziona_materia);
                if (result.getData().size() <= 1) {
                    spinnerDocenti.setVisibility(View.GONE);
                    spinnerMaterie.setVisibility(View.GONE);
                    return;
                }
                RelativeLayout loadingLayout = this.view.findViewById(R.id.loadingPanel);
                if (cardsContainer.getVisibility() == View.VISIBLE && spinnerMaterie.getVisibility() == View.VISIBLE)
                    loadingLayout.setVisibility(View.GONE);
                spinnerDocenti.setVisibility(View.VISIBLE);
                SpinAdapterDocenti spinAdapterDocenti = new SpinAdapterDocenti(this.activity.getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, result.getData());
                spinnerDocenti.setAdapter(spinAdapterDocenti);
            } else {
                Toast.makeText(activity.getBaseContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        // Creates an adapter for spinner with courses data
        private void materie(String s) {
            jsonMessage<List<Course>> result = new Gson().fromJson(s, new TypeToken<jsonMessage<List<Course>>>() {
            }.getType());
            if (result.getMessage().equals("OK")) {
                RecyclerView cardsContainer = this.view.findViewById(R.id.cardsContainer);
                Spinner spinnerDocenti = this.view.findViewById(R.id.seleziona_docente);
                Spinner spinnerMaterie = this.view.findViewById(R.id.seleziona_materia);
                if (result.getData().size() <= 1) {
                    spinnerDocenti.setVisibility(View.GONE);
                    spinnerMaterie.setVisibility(View.GONE);
                    return;
                }
                RelativeLayout loadingLayout = this.view.findViewById(R.id.loadingPanel);
                if (cardsContainer.getVisibility() == View.VISIBLE && spinnerDocenti.getVisibility() == View.VISIBLE)
                    loadingLayout.setVisibility(View.GONE);
                spinnerMaterie.setVisibility(View.VISIBLE);
                SpinAdapterMaterie spinAdapterMaterie = new SpinAdapterMaterie(this.activity.getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, result.getData());
                spinnerMaterie.setAdapter(spinAdapterMaterie);
            } else {
                Toast.makeText(activity.getBaseContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}