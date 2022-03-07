package com.example.sleepaid;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;

import com.example.sleepaid.MainMenu.Fragment.Alarms.AlarmListFragment;

public class ListMultiChoiceModeListener implements AbsListView.MultiChoiceModeListener {
    private int count = 0;

    private AlarmListFragment fragment;
    private AlarmAdapter alarmAdapter;

    public ListMultiChoiceModeListener(AlarmListFragment fragment, AlarmAdapter alarmAdapter) {
        this.fragment = fragment;
        this.alarmAdapter = alarmAdapter;
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        if (checked) {
            count++;
            this.alarmAdapter.setNewSelection(position, checked);
        } else {
            count--;
            this.alarmAdapter.removeSelection(position);
        }

        mode.setTitle(count + " selected");
    }

    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        // Respond to clicks on the actions in the CAB
        switch(item.getItemId()) {
            case R.id.turnOn:
                this.fragment.toggleAlarms(this.alarmAdapter.getSelectedIds(), 1);
                break;

            case R.id.turnOff:
                this.fragment.toggleAlarms(this.alarmAdapter.getSelectedIds(), 0);
                break;

            //"delete"
            default:
                this.fragment.deleteRows(this.alarmAdapter.getSelectedIds());
                break;
        }

        mode.finish();
        return false;
    }

    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.contextual_action_menu, menu);

        return true;
    }

    public void onDestroyActionMode(ActionMode mode) {
        // Here you can make any necessary updates to the activity when
        // the CAB is removed. By default, selected items are deselected/unchecked.
        this.alarmAdapter.clearSelection();
        this.count = 0;
    }

    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return false;
    }
}
