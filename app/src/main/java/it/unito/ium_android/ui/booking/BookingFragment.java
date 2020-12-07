package it.unito.ium_android.ui.booking;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import it.unito.ium_android.MainActivity;
import it.unito.ium_android.R;
import it.unito.ium_android.requests.Booking;
import it.unito.ium_android.requests.Lesson;
import it.unito.ium_android.requests.Requests;
import it.unito.ium_android.requests.jsonMessage;

@SuppressLint("StaticFieldLeak")
public class BookingFragment extends Fragment implements View.OnClickListener {

    private final List<TextView> week = new ArrayList<>();
    private List<Booking> userBookings = null, teacherBookings = null;
    private Lesson lesson;
    private List<Integer> recordBookings;
    private Integer weekPosition = 0;
    private MaterialButton bookBtn;
    private BookingFragment bookingFragment;
    private DrawerLayout drawerLayout;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_booking, container, false);

        lesson = (Lesson) getArguments().getSerializable("lesson");
        bookBtn = requireActivity().findViewById(R.id.bookButton);
        recordBookings = new ArrayList<>();
        bookingFragment = this;
        drawerLayout = getActivity().findViewById(R.id.drawer_layout);

        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        if (bookingFragment.isAdded() && ((MainActivity) requireActivity()).isLoggedIn()) {
            bookBtn.setVisibility(View.VISIBLE);

            bookBtn.setOnClickListener(v -> {
                if (recordBookings.isEmpty()) {
                    Toast.makeText(requireActivity().getBaseContext(), "Prenotazioni non selezionate", Toast.LENGTH_SHORT).show();
                    return;
                }
                Requests prenotaLezioni = new Requests(getActivity(), "bookLessons");
                try {
                    String data = "course=" + URLEncoder.encode(lesson.getCourse().getName(), "UTF-8") + "&teacherId=" + URLEncoder.encode(String.valueOf(lesson.getTeacher().getId()), "UTF-8") + "&lessonSlots=" + URLEncoder.encode(recordBookings.toString(), "UTF-8") + "&action=bookLessons";
                    String method = "POST";
                    prenotaLezioni.execute(data, Requests.url, method);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... Void) {
                        try {
                            prenotaLezioni.get();
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }
                        recordBookings.clear();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        if (bookingFragment.isAdded()) {
                            root.findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
                            root.findViewById(R.id.hoursContainer).setVisibility(View.GONE);
                            executeQuery(root);
                        }
                    }
                }.execute();
            });
        }

        executeQuery(root);
        addWeekBtn(root);

        for (int i = 0; i < 5; i++) {
            week.get(i).setOnClickListener(this);
        }

        return root;
    }

    private void executeQuery(View root) {
        Requests prenotazioniDocenteRequests = new Requests(getActivity(), "prenotazioniDocente");
        try {
            String data = "course=" + URLEncoder.encode(lesson.getCourse().getName(), "UTF-8") + "&teacherId=" + URLEncoder.encode(String.valueOf(lesson.getTeacher().getId()), "UTF-8") + "&teacherId=" + URLEncoder.encode(String.valueOf(lesson.getTeacher().getId()), "UTF-8") + "&action=teacherBooking";
            String method = "GET";
            prenotazioniDocenteRequests.execute(data, Requests.url, method);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Requests userBookingsRequests = new Requests(getActivity(), "userBookings");
        String data = "action=userBooking&isAndroid=true";
        String method = "GET";
        userBookingsRequests.execute(data, Requests.url, method);

        new Task(this, root).execute(prenotazioniDocenteRequests, userBookingsRequests);
    }

    private void addWeekBtn(View root) {
        week.add(root.findViewById(R.id.lun));
        week.add(root.findViewById(R.id.mar));
        week.add(root.findViewById(R.id.mer));
        week.add(root.findViewById(R.id.gio));
        week.add(root.findViewById(R.id.ven));
        week.add(root.findViewById(R.id.date15));
        week.add(root.findViewById(R.id.date16));
        week.add(root.findViewById(R.id.date17));
        week.add(root.findViewById(R.id.date18));
        week.add(root.findViewById(R.id.date19));
    }

    private static class Task extends AsyncTask<Requests, Void, Void> {
        private final BookingFragment bookingFragment;
        private final View view;

        Task(BookingFragment bookingFragment, View view) {
            this.view = view;
            this.bookingFragment = bookingFragment;
        }

        @Override
        protected Void doInBackground(Requests... requests) {
            String s;
            jsonMessage<List<Booking>> result;
            List<Booking> userBookings = null, teacherBookings = null;
            try {
                s = requests[0].get();
                result = new Gson().fromJson(s, new TypeToken<jsonMessage<List<Booking>>>() {
                }.getType());
                teacherBookings = result.getData();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            try {
                s = requests[1].get();
                result = new Gson().fromJson(s, new TypeToken<jsonMessage<List<Booking>>>() {
                }.getType());
                userBookings = result.getData();
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
            bookingFragment.setBookings(userBookings, teacherBookings);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            view.findViewById(R.id.hoursContainer).setVisibility(View.VISIBLE);
            view.findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            bookingFragment.onClick(view.findViewById(R.id.lun));
        }

    }

    private void resetWeekColor(TextView v) {
        for (int i = 0; i < 5; i++)
            week.get(i).setTextColor(Color.GRAY);
        v.setTextColor(0xFF3F51B5);
    }

    @Override
    public void onClick(View v) {
        switch (((TextView) v).getText().toString()) {
            case "Lun":
                weekPosition = 0;
                resetWeekColor((TextView) v);
                updateBookings(0, 5);
                break;
            case "Mar":
                weekPosition = 5;
                resetWeekColor((TextView) v);
                updateBookings(5, 10);
                break;
            case "Mer":
                weekPosition = 10;
                resetWeekColor((TextView) v);
                updateBookings(10, 15);
                break;
            case "Gio":
                weekPosition = 15;
                resetWeekColor((TextView) v);
                updateBookings(15, 20);
                break;
            case "Ven":
                weekPosition = 20;
                resetWeekColor((TextView) v);
                updateBookings(20, 25);
                break;
            default:
                markBooking((TextView) v);
                break;
        }
    }

    private void markBooking(TextView textView) {
        if (!recordBookings.contains(lessonSlot(textView))) {
            textView.setBackgroundResource(R.drawable.dark_green);
            textView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_baseline_check_24, 0, 0);
            recordBookings.add(lessonSlot(textView));
        } else if (recordBookings.contains(lessonSlot(textView))) {
            textView.setBackgroundResource(R.drawable.green);
            textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            recordBookings.remove(lessonSlot(textView));
        }

    }

    private void restoreBookings(TextView textView) {
        if (recordBookings.contains(lessonSlot(textView))) {
            textView.setBackgroundResource(R.drawable.dark_green);
            textView.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_baseline_check_24, 0, 0);
        }
    }

    private void setBookings(List<Booking> userBookings, List<Booking> teacherBookings) {
        this.userBookings = userBookings;
        this.teacherBookings = teacherBookings;
    }

    private void updateBookings(int start, int end) {
        for (int i = 5; i < 10; i++) {
            week.get(i).setBackgroundResource(R.drawable.green);
            if (bookingFragment.isAdded() && ((MainActivity) requireActivity()).isLoggedIn())
                week.get(i).setOnClickListener(this);
            week.get(i).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }

        if (userBookings == null && teacherBookings == null) {
            return;
        }

        for (Integer booking : recordBookings) {
            if (booking >= start && booking < end)
                switch (booking % 5) {
                    case 0:
                        restoreBookings(week.get(5));
                        break;
                    case 1:
                        restoreBookings(week.get(6));
                        break;
                    case 2:
                        restoreBookings(week.get(7));
                        break;
                    case 3:
                        restoreBookings(week.get(8));
                        break;
                    case 4:
                        restoreBookings(week.get(9));
                        break;
                }
        }

        for (Booking booking : teacherBookings) {
            if (booking.getLessonSlot() >= start && booking.getLessonSlot() < end)
                switch (booking.getLessonSlot() % 5) {
                    case 0:
                        week.get(5).setBackgroundResource(R.drawable.red);
                        week.get(5).setOnClickListener(null);
                        break;
                    case 1:
                        week.get(6).setBackgroundResource(R.drawable.red);
                        week.get(6).setOnClickListener(null);
                        break;
                    case 2:
                        week.get(7).setBackgroundResource(R.drawable.red);
                        week.get(7).setOnClickListener(null);
                        break;
                    case 3:
                        week.get(8).setBackgroundResource(R.drawable.red);
                        week.get(8).setOnClickListener(null);
                        break;
                    case 4:
                        week.get(9).setBackgroundResource(R.drawable.red);
                        week.get(9).setOnClickListener(null);
                        break;
                }
        }

        if (userBookings != null)
            for (Booking booking : userBookings) {
                if (booking.getLessonSlot() >= start && booking.getLessonSlot() < end)
                    switch (booking.getLessonSlot() % 5) {
                        case 0:
                            if (booking.getCourse().equals(lesson.getCourse().getName()) && week.get(5).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.red).getConstantState()))
                                week.get(5).setBackgroundResource(R.drawable.blue);
                            else
                                week.get(5).setBackgroundResource(R.drawable.red);
                            week.get(5).setOnClickListener(null);
                            week.get(5).setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_baseline_check_box_24, 0, 0);
                            break;
                        case 1:
                            if (booking.getCourse().equals(lesson.getCourse().getName()) && week.get(6).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.red).getConstantState()))
                                week.get(6).setBackgroundResource(R.drawable.blue);
                            else
                                week.get(6).setBackgroundResource(R.drawable.red);
                            week.get(6).setOnClickListener(null);
                            week.get(6).setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_baseline_check_box_24, 0, 0);
                            break;
                        case 2:
                            if (booking.getCourse().equals(lesson.getCourse().getName()) && week.get(7).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.red).getConstantState()))
                                week.get(7).setBackgroundResource(R.drawable.blue);
                            else
                                week.get(7).setBackgroundResource(R.drawable.red);
                            week.get(7).setOnClickListener(null);
                            week.get(7).setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_baseline_check_box_24, 0, 0);
                            break;
                        case 3:
                            if (booking.getCourse().equals(lesson.getCourse().getName()) && week.get(8).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.red).getConstantState()))
                                week.get(8).setBackgroundResource(R.drawable.blue);
                            else
                                week.get(8).setBackgroundResource(R.drawable.red);
                            week.get(8).setOnClickListener(null);
                            week.get(8).setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_baseline_check_box_24, 0, 0);
                            break;
                        case 4:
                            if (booking.getCourse().equals(lesson.getCourse().getName()) && week.get(9).getBackground().getConstantState().equals(getResources().getDrawable(R.drawable.red).getConstantState()))
                                week.get(9).setBackgroundResource(R.drawable.blue);
                            else
                                week.get(9).setBackgroundResource(R.drawable.red);
                            week.get(9).setOnClickListener(null);
                            week.get(9).setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_baseline_check_box_24, 0, 0);
                            break;
                    }
            }
    }

    private Integer lessonSlot(TextView v) {
        switch (v.getText().toString()) {
            case "15-16":
                return weekPosition;
            case "16-17":
                return weekPosition + 1;
            case "17-18":
                return weekPosition + 2;
            case "18-19":
                return weekPosition + 3;
            case "19-20":
                return weekPosition + 4;
            default:
                return -1;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bookBtn.setOnClickListener(null);
        bookBtn.setVisibility(View.GONE);
        if (bookingFragment.isAdded() && ((MainActivity) requireActivity()).isLoggedIn()) {
            Requests requests = new Requests(getActivity(), "getSessionLogin");
            String data = "action=getSessionLogin";
            String method = "POST";
            requests.execute(data, Requests.url, method);
        }
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }


}