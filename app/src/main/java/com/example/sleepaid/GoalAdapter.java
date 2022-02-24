package com.example.sleepaid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class GoalAdapter extends BaseAdapter {
    private Context context;

    private List<String> names;
    private List<String> values;
    private List<String> percentages;

    public GoalAdapter(Context context,
                       List<String> names,
                       List<String> values,
                       List<String> percentages) {
        this.context = context;
        this.names = names;
        this.values = values;
        this.percentages = percentages;
    }

    public int getCount() {
        return names.size();
    }

    public Object getItem(int arg) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.goal_row, parent, false);

        TextView namesText = row.findViewById(R.id.name);
        TextView valuesText = row.findViewById(R.id.value);
        TextView percentagesText = row.findViewById(R.id.percent);

        namesText.setText(names.get(position));
        valuesText.setText(values.get(position));
        percentagesText.setText(percentages.get(position) + "%");

        return row;
    }
}
