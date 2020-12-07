package it.unito.ium_android.requests;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import it.unito.ium_android.R;

public class CardsArchiveContainerAdapter extends RecyclerView.Adapter<CardsArchiveContainerAdapter.CardsContainerViewHolder> {

    private final List<Booking> data;

    public CardsArchiveContainerAdapter(List<Booking> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public CardsArchiveContainerAdapter.CardsContainerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_archive_design, parent, false);
        return new CardsContainerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardsArchiveContainerAdapter.CardsContainerViewHolder holder, int position) {
        Booking booking = this.data.get(position);

        holder.materia.setText(booking.getCourse());
        holder.docente.setText(String.format("%s %s", booking.getTeacher().getName(), booking.getTeacher().getSurname()));
        holder.data.setText(lessonSlotToString(booking.getLessonSlot()));
        if (String.valueOf(booking.status).equals("ACTIVE")) {
            holder.buttons.setVisibility(View.VISIBLE);
            holder.disdici.setOnClickListener(v -> {
                Requests requests = new Requests((Activity) v.getContext(), "disdici", v.getRootView());
                try {
                    String data = "lessonSlot=" + URLEncoder.encode(String.valueOf(booking.getLessonSlot()), "UTF-8") + "&course=" + URLEncoder.encode(booking.getCourse(), "UTF-8") + "&teacherId=" + URLEncoder.encode(String.valueOf(booking.getTeacher().getId()), "UTF-8") + "&action=disdici";
                    String url = "http://192.168.1.102:8080/ProgettoTWEB_war_exploded/Controller";
                    String method = "POST";
                    requests.execute(data, url, method);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });
            holder.svolta.setOnClickListener(v -> {
                Requests requests = new Requests((Activity) v.getContext(), "svolta", v.getRootView());
                try {
                    String data = "lessonSlot=" + URLEncoder.encode(String.valueOf(booking.getLessonSlot()), "UTF-8") + "&course=" + URLEncoder.encode(booking.getCourse(), "UTF-8") + "&teacherId=" + URLEncoder.encode(String.valueOf(booking.getTeacher().getId()), "UTF-8") + "&action=effettuata";
                    String url = "http://192.168.1.102:8080/ProgettoTWEB_war_exploded/Controller";
                    String method = "POST";
                    requests.execute(data, url, method);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });
        } else {
            holder.status.setVisibility(View.VISIBLE);
            holder.status.setText(String.valueOf(booking.status));
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class CardsContainerViewHolder extends RecyclerView.ViewHolder {

        TextView materia, docente, data, status;
        MaterialButton disdici, svolta;
        LinearLayout buttons;

        public CardsContainerViewHolder(@NonNull View itemView) {
            super(itemView);

            buttons = itemView.findViewById(R.id.buttons);
            materia = itemView.findViewById(R.id.materia);
            docente = itemView.findViewById(R.id.docente);
            data = itemView.findViewById(R.id.data);
            disdici = itemView.findViewById(R.id.disdici);
            svolta = itemView.findViewById(R.id.svolta);
            status = itemView.findViewById(R.id.status);
        }
    }

    private String lessonSlotToString(int lessonSlot) {
        String day = "";
        switch ((int) Math.floor(lessonSlot / 5)) {
            case 0:
                day = "Lun";
                break;
            case 1:
                day = "Mar";
                break;
            case 2:
                day = "Mer";
                break;
            case 3:
                day = "Gio";
                break;
            case 4:
                day = "Ven";
                break;
        }

        switch (lessonSlot % 5) {
            case 0:
                return day + ", 15-16";
            case 1:
                return day + ", 16-17";
            case 2:
                return day + ", 17-18";
            case 3:
                return day + ", 18-19";
            case 4:
                return day + ", 19-20";
        }
        return day;
    }
}
