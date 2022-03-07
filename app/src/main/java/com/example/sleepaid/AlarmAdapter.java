package com.example.sleepaid;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class AlarmAdapter extends BaseAdapter {
    private Context context;

    private List<Integer> ids;
    private List<String> times;
    private List<String> days;

    private int colorSelected;
    private int colorActive;
    private int colorInactive;

    private HashMap<Integer, Boolean> selection = new HashMap<>();

    public AlarmAdapter(Context context,
                        List<Integer> ids,
                        List<String> times,
                        List<String> days,
                        int colorSelected,
                        int colorActive,
                        int colorInactive) {
        this.context = context;
        this.ids = ids;
        this.times = times;
        this.days = days;
        this.colorSelected = colorSelected;
        this.colorActive = colorActive;
        this.colorInactive = colorInactive;
    }

    public int getCount() {
        return times.size();
    }

    public Object getItem(int position) {
        return times.get(position);
    }

    public long getItemId(int position) {
        return this.ids.get(position);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(this.context);

        View row = convertView;

        if (row == null) {
            row = inflater.inflate(R.layout.alarm_row, parent, false);
        }

        row.setId(this.ids.get(position));

        TextView timeText = row.findViewById(R.id.time);
        timeText.setText(this.times.get(position));

        int[] dayIds = {R.id.monday, R.id.tuesday, R.id.wednesday, R.id.thursday, R.id.friday, R.id.saturday, R.id.sunday};

        for (int i = 0; i < 7; i++) {
            TextView day = row.findViewById(dayIds[i]);

            if (this.days.get(position).charAt(i) == '1') {
                day.setTextColor(this.colorActive);
                day.setTypeface(null, Typeface.BOLD);
            } else {
                day.setTextColor(this.colorInactive);
                day.setTypeface(Typeface.DEFAULT);
            }
        }

        row.getBackground().setTint(this.isPositionChecked(position) ? this.colorSelected : Color.WHITE);

        return row;
    }

    public void setNewSelection(int position, boolean value) {
        selection.put(position, value);
        this.notifyDataSetChanged();
    }

    public boolean isPositionChecked(int position) {
        Boolean result = selection.get(position);

        return result == null ? false : result;
    }

    public List<Integer> getSelectedIds() {
        return new ArrayList<>(selection.keySet());
    }

    public void removeSelection(int position) {
        selection.remove(position);
        this.notifyDataSetChanged();
    }

    public void clearSelection() {
        selection = new HashMap<>();
    }
}
