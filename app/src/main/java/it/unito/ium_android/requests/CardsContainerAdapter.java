package it.unito.ium_android.requests;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import it.unito.ium_android.R;
import it.unito.ium_android.requests.Lesson;

public class CardsContainerAdapter extends RecyclerView.Adapter<CardsContainerAdapter.CardsContainerViewHolder> {

    private List<Lesson> data;

    public CardsContainerAdapter(List<Lesson> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public CardsContainerAdapter.CardsContainerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_design, parent, false);
        CardsContainerAdapter.CardsContainerViewHolder cardsContainerViewHolder = new CardsContainerAdapter.CardsContainerViewHolder(view);
        return cardsContainerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CardsContainerAdapter.CardsContainerViewHolder holder, int position) {
        Lesson lesson = this.data.get(position);

        holder.materia.setText(lesson.getCourse().getName());
        holder.docente.setText(lesson.getTeacher().getName() + " " + lesson.getTeacher().getSurname());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class CardsContainerViewHolder extends RecyclerView.ViewHolder {

        TextView materia, docente;

        public CardsContainerViewHolder(@NonNull View itemView) {
            super(itemView);

            materia = itemView.findViewById(R.id.materia);
            docente = itemView.findViewById(R.id.docente);

        }
    }
}
