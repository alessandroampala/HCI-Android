package it.unito.ium_android.requests;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import it.unito.ium_android.MainActivity;
import it.unito.ium_android.R;

// Class for the adapter for prenota fragment cards
public class CardsContainerAdapter extends RecyclerView.Adapter<CardsContainerAdapter.CardsContainerViewHolder> {

    private final List<Lesson> data;
    private final MainActivity activity;

    // Constructor
    public CardsContainerAdapter(List<Lesson> data, MainActivity activity) {
        this.data = data;
        this.activity = activity;
    }

    // Inflates layout during the creation of the view holder for the cards
    @NonNull
    @Override
    public CardsContainerAdapter.CardsContainerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_design, parent, false);
        return new CardsContainerViewHolder(view);
    }

    // Populates the cards with the data and bind it
    @Override
    public void onBindViewHolder(@NonNull CardsContainerAdapter.CardsContainerViewHolder holder, int position) {
        Lesson lesson = this.data.get(position);

        holder.materia.setText(lesson.getCourse().getName());
        holder.docente.setText(String.format("%s %s", lesson.getTeacher().getName(), lesson.getTeacher().getSurname()));
        holder.prenota.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("lesson", lesson);
            Navigation.findNavController(activity, R.id.nav_host_fragment).navigate(R.id.nav_booking, bundle);
        });
    }

    // Returns the number of lessons in Prenota fragment
    @Override
    public int getItemCount() {
        return data.size();
    }

    // Class for the view holder
    public static class CardsContainerViewHolder extends RecyclerView.ViewHolder {

        TextView materia, docente;
        MaterialButton prenota;

        // Constructor
        public CardsContainerViewHolder(@NonNull View itemView) {
            super(itemView);

            materia = itemView.findViewById(R.id.materia);
            docente = itemView.findViewById(R.id.docente);
            prenota = itemView.findViewById(R.id.prenota);
        }
    }
}
