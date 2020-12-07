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

// Class for the adapter for prenotazioni fragment cards
public class CardsArchiveContainerAdapter extends RecyclerView.Adapter<CardsArchiveContainerAdapter.CardsContainerViewHolder> {

    private final List<Booking> data;

    // Constructor
    public CardsArchiveContainerAdapter(List<Booking> data) {
        this.data = data;
    }

    // Inflates layout during the creation of the view holder for the cards
    @NonNull
    @Override
    public CardsArchiveContainerAdapter.CardsContainerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_archive_design, parent, false);
        return new CardsContainerViewHolder(view);
    }

    // Populates the cards with the data and bind it
    @Override
    public void onBindViewHolder(@NonNull CardsArchiveContainerAdapter.CardsContainerViewHolder holder, int position) {
        Booking booking = this.data.get(position);

        holder.materia.setText(booking.getCourse());
        holder.docente.setText(String.format("%s %s", booking.getTeacher().getName(), booking.getTeacher().getSurname()));
        holder.data.setText(lessonSlotToString(booking.getLessonSlot()));
        if (String.valueOf(booking.status).equals("ACTIVE")) {
            holder.buttons.setVisibility(View.VISIBLE);
            holder.disdici.setOnClickListener(v -> {
                Requests requests = new Requests((Activity) v.getContext(), "cancelBooking", v.getRootView());
                try {
                    String data = "lessonSlot=" + URLEncoder.encode(String.valueOf(booking.getLessonSlot()), "UTF-8") + "&course=" + URLEncoder.encode(booking.getCourse(), "UTF-8") + "&teacherId=" + URLEncoder.encode(String.valueOf(booking.getTeacher().getId()), "UTF-8") + "&action=cancelBooking";
                    String method = "POST";
                    requests.execute(data, Requests.url, method);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });
            holder.svolta.setOnClickListener(v -> {
                Requests requests = new Requests((Activity) v.getContext(), "markBooking", v.getRootView());
                try {
                    String data = "lessonSlot=" + URLEncoder.encode(String.valueOf(booking.getLessonSlot()), "UTF-8") + "&course=" + URLEncoder.encode(booking.getCourse(), "UTF-8") + "&teacherId=" + URLEncoder.encode(String.valueOf(booking.getTeacher().getId()), "UTF-8") + "&action=markBooking";
                    String method = "POST";
                    requests.execute(data, Requests.url, method);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });
        } else {
            holder.status.setVisibility(View.VISIBLE);
            holder.status.setText(String.valueOf(booking.status));
        }
    }

    // Returns the number of lessons in Prenotazioni fragment
    @Override
    public int getItemCount() {
        return data.size();
    }

    // Class for the view holder
    public static class CardsContainerViewHolder extends RecyclerView.ViewHolder {

        TextView materia, docente, data, status;
        MaterialButton disdici, svolta;
        LinearLayout buttons;

        // Constructor
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

    // Transform lessonSlot to string
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
