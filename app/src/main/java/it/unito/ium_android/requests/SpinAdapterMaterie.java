package it.unito.ium_android.requests;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;


public class SpinAdapterMaterie extends ArrayAdapter<Course> {
    private Context context;
    private List<Course> materie;

    public SpinAdapterMaterie(Context context, int textViewResourceId, List<Course> materie) {
        super(context, textViewResourceId, materie);
        this.context = context;
        this.materie = materie;
    }

    @Override
    public int getCount() {
        return materie.size();
    }

    @Override
    public Course getItem(int position) {
        return materie.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextSize(20);
        label.setText(materie.get(position).getName());
        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }
}

