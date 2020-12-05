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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.unito.ium_android.MainActivity;
import it.unito.ium_android.R;
import it.unito.ium_android.requests.Booking;
import it.unito.ium_android.requests.CardsArchiveContainerAdapter;
import it.unito.ium_android.requests.CardsContainerAdapter;
import it.unito.ium_android.requests.Course;
import it.unito.ium_android.requests.Lesson;
import it.unito.ium_android.requests.Requests;
import it.unito.ium_android.requests.SpinAdapterDocenti;
import it.unito.ium_android.requests.SpinAdapterMaterie;
import it.unito.ium_android.requests.Teacher;
import it.unito.ium_android.requests.jsonMessage;
import it.unito.ium_android.ui.prenotazioni.PrenotazioniFragment;

public class PrenotaFragment extends Fragment {

    private PrenotaViewModel prenotaViewModel;
    private String materia = "";
    private String docente = "";
    private int firstTimeSpinner = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        prenotaViewModel =
                new ViewModelProvider(this).get(PrenotaViewModel.class);
        View root = inflater.inflate(R.layout.fragment_prenota, container, false);
        RelativeLayout loadingLayout = (RelativeLayout) root.findViewById(R.id.loadingPanel);
        makeRequests(root);

        Spinner spinnerDocenti = (Spinner) root.findViewById(R.id.seleziona_docente);
        Spinner spinnerMaterie = (Spinner) root.findViewById(R.id.seleziona_materia);
        spinnerDocenti.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (firstTimeSpinner < 2) {
                    firstTimeSpinner++;
                    return;
                }
                loadingLayout.setVisibility(View.VISIBLE);
                docente = ((Integer) view.getTag()).toString();
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

    private void makeRequests(View root) {
        Requests teacherRequests = new Requests(getActivity(), "docenti", root);

        String data = "action=docenti";
        String url = "http://10.0.2.2:8080/ProgettoTWEB_war_exploded/Controller";
        String method = "GET";
        teacherRequests.execute(data, url, method);

        Requests courseRequests = new Requests(getActivity(), "materie", root);

        data = "action=materie";
        url = "http://10.0.2.2:8080/ProgettoTWEB_war_exploded/Controller";
        method = "GET";

        courseRequests.execute(data, url, method);

        Requests lessonRequests = new Requests(getActivity(), "lessons", root);
        try {
            data = "course=" + URLEncoder.encode(materia, "UTF-8") + "&teacherId=" + URLEncoder.encode(docente, "UTF-8") + "&action=lessons";
            url = "http://10.0.2.2:8080/ProgettoTWEB_war_exploded/Controller";
            method = "POST";
            lessonRequests.execute(data, url, method);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        new Task(root, getActivity()).execute(teacherRequests, courseRequests, lessonRequests);
    }

    private void makeSpinnerRequests(View root) {
        Requests request = new Requests(getActivity(), "lessons", root);
        try {
            String data = "course=" + URLEncoder.encode(materia, "UTF-8") + "&teacherId=" + URLEncoder.encode(docente, "UTF-8") + "&action=lessons";
            String url = "http://10.0.2.2:8080/ProgettoTWEB_war_exploded/Controller";
            String method = "POST";
            request.execute(data, url, method);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        new Task(root, getActivity()).execute(request);
    }

    @Override
    public void onResume() {
        super.onResume();
        firstTimeSpinner = 0;
    }

    public static class Task extends AsyncTask<Requests, Void, ArrayList<String>> {
        private View view;
        private Activity activity;

        public Task(View view, Activity activity) {
            this.view = view;
            this.activity = activity;
        }

        @Override
        protected ArrayList<String> doInBackground(Requests... requests) {
            ArrayList<String> s = new ArrayList<>();
            if (requests.length > 1) {
                try {
                    s.add( requests[0].get());
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    s.add( requests[1].get());
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    s.add( requests[2].get());
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    s.add( requests[0].get());
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return s;
        }

        @Override
        protected void onPostExecute(ArrayList<String> s) {
            super.onPostExecute(s);
            if (s.size() > 1) {
                docenti(s.get(0));
                materie(s.get(1));
                lessons(s.get(2));
            } else {
                lessons(s.get(0));
            }
        }

        private void lessons(String s) {
            jsonMessage<List<Lesson>> result = new Gson().fromJson(s, new TypeToken<jsonMessage<List<Lesson>>>() {
            }.getType());

            RelativeLayout loadingLayout = (RelativeLayout) this.view.findViewById(R.id.loadingPanel);
            RecyclerView cardsContainer = (RecyclerView) this.view.findViewById(R.id.cardsContainer);
            TextView noLessons = (TextView) this.view.findViewById(R.id.noLessons);

            if (result.getMessage().equals("OK")) {
                if (result.getData().isEmpty()) {
                    loadingLayout.setVisibility(view.GONE);
                    cardsContainer.setVisibility(view.GONE);
                    noLessons.setVisibility(view.VISIBLE);
                    return;
                }
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
                Toast.makeText(activity.getBaseContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                loadingLayout.setVisibility(view.GONE);
                cardsContainer.setVisibility(view.GONE);
                noLessons.setVisibility(view.VISIBLE);
            }
        }

        private void docenti(String s) {
            jsonMessage<List<Teacher>> result = new Gson().fromJson(s, new TypeToken<jsonMessage<List<Teacher>>>() {
            }.getType());

            if (result.getMessage().equals("OK")) {
                RecyclerView cardsContainer = (RecyclerView) this.view.findViewById(R.id.cardsContainer);
                Spinner spinnerDocenti = (Spinner) this.view.findViewById(R.id.seleziona_docente);
                Spinner spinnerMaterie = (Spinner) this.view.findViewById(R.id.seleziona_materia);
                if (result.getData().size() <= 1) {
                    spinnerDocenti.setVisibility(view.GONE);
                    spinnerMaterie.setVisibility(view.GONE);
                    return;
                }
                RelativeLayout loadingLayout = (RelativeLayout) this.view.findViewById(R.id.loadingPanel);
                if (cardsContainer.getVisibility() == view.VISIBLE && spinnerMaterie.getVisibility() == view.VISIBLE)
                    loadingLayout.setVisibility(view.GONE);
                spinnerDocenti.setVisibility(view.VISIBLE);
                SpinAdapterDocenti spinAdapterDocenti = new SpinAdapterDocenti(this.activity.getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, result.getData());
                spinnerDocenti.setAdapter(spinAdapterDocenti);
            } else {
                Toast.makeText(activity.getBaseContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }

        private void materie(String s) {
            jsonMessage<List<Course>> result = new Gson().fromJson(s, new TypeToken<jsonMessage<List<Course>>>() {
            }.getType());
            if (result.getMessage().equals("OK")) {
                RecyclerView cardsContainer = (RecyclerView) this.view.findViewById(R.id.cardsContainer);
                Spinner spinnerDocenti = (Spinner) this.view.findViewById(R.id.seleziona_docente);
                Spinner spinnerMaterie = (Spinner) this.view.findViewById(R.id.seleziona_materia);
                if (result.getData().size() <= 1) {
                    spinnerDocenti.setVisibility(view.GONE);
                    spinnerMaterie.setVisibility(view.GONE);
                    return;
                }
                RelativeLayout loadingLayout = (RelativeLayout) this.view.findViewById(R.id.loadingPanel);
                if (cardsContainer.getVisibility() == view.VISIBLE && spinnerDocenti.getVisibility() == view.VISIBLE)
                    loadingLayout.setVisibility(view.GONE);
                spinnerMaterie.setVisibility(view.VISIBLE);
                SpinAdapterMaterie spinAdapterMaterie = new SpinAdapterMaterie(this.activity.getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, result.getData());
                spinnerMaterie.setAdapter(spinAdapterMaterie);
            } else {
                Toast.makeText(activity.getBaseContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}