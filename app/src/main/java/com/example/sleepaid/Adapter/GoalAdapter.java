package com.example.sleepaid.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sleepaid.App;
import com.example.sleepaid.Handler.DataHandler;
import com.example.sleepaid.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GoalAdapter extends BaseExpandableListAdapter {
    private Context context;

    private List<String> names;
    private List<String> values;
    private List<String> percentages;
    private HashMap<String, List<String>> options;

    public GoalAdapter(Context context,
                       List<String> names,
                       List<String> values,
                       List<String> percentages,
                       HashMap<String, List<String>> options) {
        this.context = context;
        this.names = names;
        this.values = values;
        this.percentages = percentages;
        this.options = options;
    }

    @Override
    // Gets the data associated with the given child within the given group.
    public Object getChild(int listPosition, int expanded_ListPosition) {
        return this.options.get(this.names.get(listPosition)).get(expanded_ListPosition);
    }

    @Override
    // Gets the ID for the given child within the given group.
    // This ID must be unique across all children within the group. Hence we can pick the child uniquely
    public long getChildId(int listPosition, int expanded_ListPosition) {
        return expanded_ListPosition;
    }

    @Override
    // Gets a View that displays the data for the given child within the given group.
    public View getChildView(int listPosition,
                             final int expanded_ListPosition,
                             boolean isLastChild,
                             View childRow,
                             ViewGroup parent) {
        final String expandedListText = (String) getChild(listPosition, expanded_ListPosition);

        if (childRow == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            childRow = layoutInflater.inflate(R.layout.goal_options_row, null);
        }

        TextView expandedListTextView = (TextView) childRow.findViewById(R.id.expandedGoal);
        expandedListTextView.setText(expandedListText);

        return childRow;
    }

    @Override
    // Gets the number of children in a specified group.
    public int getChildrenCount(int listPosition) {
        return this.options.get(this.names.get(listPosition)).size();
    }

    @Override
    // Gets the data associated with the given group.
    public List<String> getGroup(int listPosition) {
        return Arrays.asList(
                this.names.get(listPosition),
                this.values.get(listPosition),
                this.percentages.get(listPosition)
        );
    }

    @Override
    // Gets the number of groups.
    public int getGroupCount() {
        return this.names.size();
    }

    @Override
    // Gets the ID for the group at the given position. This group ID must be unique across groups.
    public long getGroupId(int listPosition) {
        return listPosition;
    }

    @Override
    // Gets a View that displays the given group.
    // This View is only for the group--the Views for the group's children
    // will be fetched using getChildView()
    public View getGroupView(int listPosition,
                             boolean isExpanded,
                             View row,
                             ViewGroup parent) {
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.goal_row, parent, false);
        }

        TextView namesText = row.findViewById(R.id.name);
        TextView valuesText = row.findViewById(R.id.value);
        TextView percentagesText = row.findViewById(R.id.percent);

        namesText.setText(getGroup(listPosition).get(0));
        valuesText.setText(getGroup(listPosition).get(1));
        percentagesText.setText(getGroup(listPosition).get(2) + "%");

        return row;
    }

    @Override
    // Indicates whether the child and group IDs are stable across changes to the underlying data.
    public boolean hasStableIds() {
        return false;
    }

    @Override
    // Whether the child at the specified position is selectable.
    public boolean isChildSelectable(int listPosition, int expandedListPosition) {
        return true;
    }
}
