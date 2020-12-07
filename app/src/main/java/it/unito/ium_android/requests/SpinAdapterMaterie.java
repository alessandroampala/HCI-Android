package it.unito.ium_android.requests;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

// Adapter for the spinner that represents the courses
public class SpinAdapterMaterie extends ArrayAdapter<Course> {
    private final List<Course> materie;

    // Constructor
    public SpinAdapterMaterie(Context context, int textViewResourceId, List<Course> materie) {
        super(context, textViewResourceId, materie);
        this.materie = materie;
    }

    // Returns the number of courses
    @Override
    public int getCount() {
        return materie.size();
    }

    // Returns the course at the int position
    @Override
    public Course getItem(int position) {
        return materie.get(position);
    }

    // Returns the view, in this case a label
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextSize(20);
        label.setText(materie.get(position).getName());
        return label;
    }

    // Returns the view for the drop down menu
    @Override
    public View getDropDownView(int position, View convertView, @NotNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}

