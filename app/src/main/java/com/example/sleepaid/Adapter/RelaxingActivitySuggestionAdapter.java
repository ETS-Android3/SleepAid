package com.example.sleepaid.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.sleepaid.R;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RelaxingActivitySuggestionAdapter extends BaseExpandableListAdapter {
    private Context context;

    private List<String> names;
    private HashMap<String, List<String>> information;

    public RelaxingActivitySuggestionAdapter(Context context,
                                             List<String> names,
                                             HashMap<String, List<String>> information) {
        this.context = context;
        this.names = names;
        this.information = information;
    }

    @Override
    // Gets the data associated with the given child within the given group.
    public Object getChild(int listPosition, int expanded_ListPosition) {
        return this.information.get(this.names.get(listPosition)).get(expanded_ListPosition);
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
            childRow = layoutInflater.inflate(R.layout.relaxing_activity_information_row, null);
        }

        TextView expandedListTextView = (TextView) childRow.findViewById(R.id.expandedActivity);
        expandedListTextView.setText(expandedListText);

        return childRow;
    }

    @Override
    // Gets the number of children in a specified group.
    public int getChildrenCount(int listPosition) {
        return this.information.get(this.names.get(listPosition)).size();
    }

    @Override
    // Gets the data associated with the given group.
    public String getGroup(int listPosition) {
        return this.names.get(listPosition);
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
            row = inflater.inflate(R.layout.relaxing_activity_row, parent, false);
        }

        TextView namesText = row.findViewById(R.id.name);
        namesText.setText(getGroup(listPosition));

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
