package it.unito.ium_android.requests;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;


public class SpinAdapterDocenti extends ArrayAdapter<Teacher> {
    private final List<Teacher> docenti;

    public SpinAdapterDocenti(Context context, int textViewResourceId, List<Teacher> docenti) {
        super(context, textViewResourceId, docenti);
        this.docenti = docenti;
    }

    @Override
    public int getCount() {
        return docenti.size();
    }

    @Override
    public Teacher getItem(int position) {
        return docenti.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextSize(20);
        label.setText(String.format("%s %s", docenti.get(position).getName(), docenti.get(position).getSurname()));
        label.setTag(docenti.get(position).getId());
        return label;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView label = (TextView) super.getView(position, convertView, parent);
        label.setTextSize(20);
        label.setText(String.format("%s %s", docenti.get(position).getName(), docenti.get(position).getSurname()));
        return label;
    }
}