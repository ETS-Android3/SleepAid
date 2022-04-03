package com.example.sleepaid.Fragment.SleepData;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.sleepaid.App;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Database.SleepData.SleepData;
import com.example.sleepaid.Fragment.MainMenuFragment;
import com.example.sleepaid.Handler.DataHandler;
import com.example.sleepaid.Model.SharedViewModel;
import com.example.sleepaid.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.jjoe64.graphview.DefaultLabelFormatter;

import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SleepDataFragment extends MainMenuFragment {
    private SharedViewModel model;

    private AppDatabase db;

    protected SleepDataGraphFragment graphFragment;

    Button previousButton;
    Button nextButton;

    protected ZonedDateTime today;

    protected ZonedDateTime rangeMax;
    protected ZonedDateTime rangeMin;

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
        BottomNavigationView bottomMenu = view.findViewById(R.id.sleepDataBottomMenu);

        NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.sleep_data_graph_graph);

        if (getArguments() != null && getArguments().containsKey("DESTINATION")) {
            navGraph.setStartDestination(getArguments().getInt("DESTINATION", R.id.sleepDurationGraphFragment));
        } else {
            navGraph.setStartDestination(R.id.sleepDurationGraphFragment);
        }

        navController.setGraph(navGraph);
        NavigationUI.setupWithNavController(bottomMenu, navController);

        this.model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        this.db = AppDatabase.getDatabase(App.getContext());

        this.previousButton = view.findViewById(R.id.previousButton);
        this.previousButton.setOnClickListener(this.loadPeriod);

        this.nextButton = view.findViewById(R.id.nextButton);
        this.nextButton.setOnClickListener(this.loadPeriod);

        ArrayAdapter<CharSequence> arrayAdapter = ArrayAdapter.createFromResource(
                App.getContext(),
                R.array.calendarOptions,
                R.layout.calendar_dropdown
        );
        arrayAdapter.setDropDownViewResource(R.layout.calendar_item_dropdown);

        Spinner calendarDropdown = view.findViewById(R.id.calendarDropdown);
        calendarDropdown.setAdapter(arrayAdapter);
        calendarDropdown.setOnItemSelectedListener(changeGraphViewType);

        if (this.model.getGraphViewType() == null) {
            this.model.setGraphViewType("week");

            this.model.setGraphWeekLength(7);
            this.model.setGraphMonthLength(5);
            this.model.setGraphYearLength(12);
        }

        this.today = ZonedDateTime.now();

        getTodaysRange();

        getTodayData();
    }

    private void getTodaysRange() {
        rangeMax = ZonedDateTime.now();
        rangeMin = ZonedDateTime.now();

        switch (model.getGraphViewType()) {
            case "month":
                rangeMin = rangeMin.minusDays(rangeMax.getDayOfMonth()).plusDays(1);
                break;

            case "year":
                rangeMin = rangeMin.minusDays(rangeMax.getDayOfYear()).plusDays(1);
                break;

            //"week"
            default:
                rangeMin = rangeMin.minusDays(rangeMax.getDayOfWeek().getValue()).plusDays(1);
                break;
        }
    }

    private void getTodayData() {
        String todayString = DataHandler.getSQLiteDate(today);
        List<SleepData> modelSleepData = model.getTodaySleepData();

        boolean doesSleepDataExist = modelSleepData.size() == 4;
        boolean isSleepDataForToday = true;

        for (SleepData s : modelSleepData) {
            if (!s.getDate().equals(todayString)) {
                isSleepDataForToday = false;
            }
        }

        if (!doesSleepDataExist || !isSleepDataForToday) {
            db.sleepDataDao()
                    .loadAllByDates(new String[]{todayString})
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            sleepData -> {
                                if (!sleepData.isEmpty()) {
                                    model.setTodaySleepData(sleepData);
                                }
                            },
                            Throwable::printStackTrace
                    );
        }
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

    protected DefaultLabelFormatter getMonthLabelFormatter(int numberOfWeeks) {
        String[] weeks = new String[numberOfWeeks];

        ZonedDateTime day = rangeMin;

        for (int i = 0; i < numberOfWeeks; i++) {
            String weekStart = DataHandler.getDay(day);

            if (i == numberOfWeeks - 1){
                day = day.withDayOfMonth(YearMonth.of(rangeMin.getYear(), rangeMin.getMonth()).lengthOfMonth());
            } else {
                day = day.plusDays(6);
            }

            String weekEnd = DataHandler.getDay(day);

            weeks[i] = weekStart + "-" + weekEnd;

            day = day.plusDays(1);
        }

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
        String[] months = {"Jan", "   Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov  ", "Dec"};

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

            ZonedDateTime endOfRange;

            switch (model.getGraphViewType()) {
                case "month":
                    // rangeMin will always be the 1st of the month, so we can just move by 1 month
                    rangeMin = rangeMin.plusMonths(direction);

                    endOfRange = rangeMin.withDayOfMonth(YearMonth.of(rangeMin.getYear(), rangeMin.getMonth()).lengthOfMonth());
                    break;

                case "year":
                    // rangeMin will always be the 1st of January, so we can just move by 1 year
                    rangeMin = rangeMin.plusYears(direction);

                    endOfRange = rangeMin.withMonth(12).withDayOfMonth(31);
                    break;

                //"week"
                default:
                    // rangeMin will always be a Monday, so we can just move by 7 days
                    rangeMin = rangeMin.plusDays(7 * direction);

                    endOfRange = rangeMin.plusDays(6);
                    break;
            }

            // We need to make sure we don't go past today when clicking next
            rangeMax = endOfRange.isAfter(today) ? today : endOfRange;

            graphFragment.loadGraph(rangeMin, rangeMax);
        }
    };

    private AdapterView.OnItemSelectedListener changeGraphViewType = new AdapterView.OnItemSelectedListener() {
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            Object item = parent.getItemAtPosition(pos);
            model.setGraphViewType(item.toString().toLowerCase());

            getTodaysRange();
            graphFragment.loadGraph(rangeMin, rangeMax);
        }

        public void onNothingSelected(AdapterView<?> parent) {}
    };
}
