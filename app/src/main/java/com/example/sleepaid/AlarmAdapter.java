package com.example.sleepaid;

import android.content.Context;
import android.graphics.Typeface;
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

    private int colorActive;
    private int colorInactive;

    public AlarmAdapter(Context context,
                        List<String> times,
                        List<String> days,
                        int colorActive,
                        int colorInactive) {
        this.context = context;
        this.times = times;
        this.days = days;
        this.colorActive = colorActive;
        this.colorInactive = colorInactive;
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

        timeText.setText(times.get(position));

        int[] dayIds = {R.id.monday, R.id.tuesday, R.id.wednesday, R.id.thursday, R.id.friday, R.id.saturday, R.id.sunday};

        for (int i = 0; i < 7; i++) {
            TextView day = row.findViewById(dayIds[i]);

            if (days.get(position).charAt(i) == '1') {
                day.setTextColor(colorActive);
                day.setTypeface(null, Typeface.BOLD);
            } else {
                day.setTextColor(colorInactive);
                day.setTypeface(Typeface.DEFAULT);
            }
        }

        return row;
    }
}
