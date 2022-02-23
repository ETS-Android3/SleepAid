package com.example.sleepaid.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.example.sleepaid.App;
import com.example.sleepaid.DataHandler;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Database.SleepData;
import com.example.sleepaid.R;
import com.example.sleepaid.SharedViewModel;
import com.example.sleepaid.TextBox;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jjoe64.graphview.DefaultLabelFormatter;

import org.w3c.dom.Text;

import java.util.Calendar;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SleepDataFragment extends Fragment {
    private SharedViewModel model;

    private AppDatabase db;

    protected SleepDataGraphFragment graphFragment;

    Button previousButton;
    Button nextButton;

    protected Calendar today;

    protected Calendar rangeMax;
    protected Calendar rangeMin;

    protected String graphRangeMin;
    protected String graphRangeMax;

    protected String todayDuration;
    protected String todayWakeupTime;
    protected String todayBedTime;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sleep_data, container, false);
    }

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        NavHostFragment navHostFragment = (NavHostFragment) getChildFragmentManager().findFragmentById(R.id.graph);

        NavController navController = navHostFragment.getNavController();
        BottomNavigationView bottomMenu = getView().findViewById(R.id.bottomMenu);

        NavigationUI.setupWithNavController(bottomMenu, navController);

        model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        db = AppDatabase.getDatabase(App.getContext());

        previousButton = getView().findViewById(R.id.previousButton);
        previousButton.setOnClickListener(loadPeriod);

        nextButton = getView().findViewById(R.id.nextButton);
        nextButton.setOnClickListener(loadPeriod);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(
                App.getContext(),
                R.array.calendarOptions,
                R.layout.calendar_dropdown
        );
        arrayAdapter.setDropDownViewResource(R.layout.calendar_item_dropdown);

        Spinner calendarDropdown = getView().findViewById(R.id.calendarDropdown);
        calendarDropdown.setAdapter(arrayAdapter);
        calendarDropdown.setOnItemSelectedListener(changeGraphViewType);

        if (model.getGraphViewType() == null) {
            model.setGraphViewType("week");

            model.setGraphWeekLength(7);
            model.setGraphMonthLength(5);
            model.setGraphYearLength(12);
        }

        today = Calendar.getInstance();

        getTodaysRange();

        getTodaysData();
    }

    private void getTodaysRange() {
        rangeMax = Calendar.getInstance();
        rangeMin = Calendar.getInstance();

        graphRangeMax = DataHandler.getFormattedDate(rangeMax.getTime());

        switch (model.getGraphViewType()) {
            case "month":
                rangeMin.add(Calendar.DAY_OF_MONTH, -rangeMax.get(Calendar.DAY_OF_MONTH) + 1);
                break;

            case "year":
                rangeMin.add(Calendar.DAY_OF_YEAR, -rangeMax.get(Calendar.DAY_OF_YEAR) + 1);
                break;

            //"week"
            default:
                // Days are indexed from 1 starting with Sunday, so add 2 to get to Monday
                rangeMin.add(Calendar.DAY_OF_WEEK, -rangeMax.get(Calendar.DAY_OF_WEEK) + 2);
                break;
        }

        graphRangeMin = DataHandler.getFormattedDate(rangeMin.getTime());
    }

    private void getTodaysData() {
        db.sleepDataDao()
                .loadAllByDates(new String[]{DataHandler.getSQLiteDate(today.getTime())})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sleepData -> {
                            todayDuration = "-";
                            todayWakeupTime = "-";
                            todayBedTime = "-";

                            for (SleepData s : sleepData) {
                                switch (s.getField()) {
                                    case "wake-up time":
                                        todayWakeupTime = s.getValue();
                                        break;

                                    case "bed time":
                                        todayBedTime = s.getValue();
                                        break;

                                    //"duration"
                                    default:
                                        todayDuration = s.getValue();
                                }
                            }

                            graphFragment.loadData();
                        },
                        Throwable::printStackTrace
                );
    }

    protected DefaultLabelFormatter getWeekLabelFormatter() {
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

        return new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show days of the week
                    return days[(int) value];
                } else {
                    return "";
                }
            }
        };
    }

    protected DefaultLabelFormatter getMonthLabelFormatter(String graphRangeMin, String graphRangeMax) {
        String[] weeks = {"1", "2", "3", "4", "5"};

        return new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show weeks of the month
                    return weeks[(int) value];
                } else {
                    return "";
                }
            }
        };
    }

    protected DefaultLabelFormatter getYearLabelFormatter() {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        return new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show months of the year
                    return months[(int) value];
                } else {
                    return "";
                }
            }
        };
    }

    private View.OnClickListener loadPeriod = new View.OnClickListener() {
        public void onClick(View view) {
            int direction = 1;

            if (view.getId() == R.id.previousButton) {
                direction = -1;
            }

            Calendar endOfRange = (Calendar) rangeMin.clone();

            switch (model.getGraphViewType()) {
                case "month":
                    // rangeMin will always be the 1st of the month, so we can just move by 1 month
                    rangeMin.add(Calendar.MONTH, direction);

                    endOfRange = (Calendar) rangeMin.clone();
                    endOfRange.set(Calendar.DAY_OF_MONTH, rangeMin.getActualMaximum(Calendar.DATE));
                    break;

                case "year":
                    // rangeMin will always be the 1st of January, so we can just move by 1 year
                    rangeMin.add(Calendar.YEAR, direction);

                    endOfRange = (Calendar) rangeMin.clone();
                    endOfRange.set(Calendar.MONTH, 11);
                    endOfRange.set(Calendar.DAY_OF_MONTH, 31);
                    break;

                //"week"
                default:
                    // rangeMin will always be a Monday, so we can just move by 7 days
                    rangeMin.add(Calendar.DAY_OF_WEEK, 7 * direction);

                    // We need to make sure we don't go past today
                    endOfRange = (Calendar) rangeMin.clone();
                    endOfRange.add(Calendar.DAY_OF_WEEK, 6);
                    break;
            }

            // We need to make sure we don't go past today when clicking next
            rangeMax = endOfRange.after(today) ? (Calendar) today.clone() : (Calendar) endOfRange.clone();

            graphRangeMin = DataHandler.getFormattedDate(rangeMin.getTime());
            graphRangeMax = DataHandler.getFormattedDate(rangeMax.getTime());

            graphFragment.loadGraph(rangeMin.getTime(), rangeMax.getTime());
        }
    };

    private AdapterView.OnItemSelectedListener changeGraphViewType = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            Object item = parent.getItemAtPosition(pos);
            model.setGraphViewType(item.toString().toLowerCase());

            getTodaysRange();
            graphFragment.loadGraph(rangeMin.getTime(), rangeMax.getTime());
        }

        public void onNothingSelected(AdapterView<?> parent) {}
    };
}