package com.example.sleepaid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class AlarmAdapter extends BaseAdapter {
    private Context context;

    private List<String> times;
    private List<String> days;

    public AlarmAdapter(Context context, List<String> times, List<String> days) {
        this.context = context;
        this.times = times;
        this.days = days;
    }

    public int getCount() {
        return times.size();
    }

    public Object getItem(int arg) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.alarm_row, parent, false);

        TextView timeText = row.findViewById(R.id.time);
        TextView daysText = row.findViewById(R.id.days);

        timeText.setText(times.get(position));
        daysText.setText(days.get(position));

        return row;
    }
}
