package com.example.sleepaid.MainMenu.Fragment.SleepData;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sleepaid.App;
import com.example.sleepaid.DataHandler;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Database.SleepData.SleepData;
import com.example.sleepaid.OnSwipeTouchListener;
import com.example.sleepaid.R;
import com.example.sleepaid.Model.SharedViewModel;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@SuppressLint("NewApi")
public abstract class SleepDataGraphFragment extends Fragment {
    protected SleepDataFragment sleepDataFragment;

    protected AppDatabase db;

    protected SharedViewModel model;

    protected GraphView graph;

    private int maxGraphSize;

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        sleepDataFragment = (SleepDataFragment) getParentFragment().getParentFragment();
        sleepDataFragment.graphFragment = this;

        db = AppDatabase.getDatabase(App.getContext());

        model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        graph = getView().findViewById(R.id.sleepDataGraph);

        graph.getGridLabelRenderer().setGridColor(getResources().getColor(R.color.white));
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        graph.getViewport().setBorderColor(getResources().getColor(R.color.white));

        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(getResources().getColor(R.color.white));

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);

        //graph.getViewport().setDrawBorder(true);
        //graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        graph.setOnTouchListener(new OnSwipeTouchListener(App.getContext()) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                if (sleepDataFragment.nextButton.getVisibility() == View.VISIBLE) {
                    sleepDataFragment.nextButton.performClick();
                }
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                sleepDataFragment.previousButton.performClick();
            }
        });

        loadGraph(sleepDataFragment.rangeMin.getTime(), sleepDataFragment.rangeMax.getTime());
        loadTodaysData();
    }

    protected void loadGraph(Date min, Date max) {
        // for February in non-leap years we only have 4 weeks
        if (sleepDataFragment.rangeMin.get(Calendar.MONTH) == 1 &&
                sleepDataFragment.rangeMin.get(Calendar.YEAR) % 4 != 0 &&
                model.getGraphViewType().equals("month")) {
            this.maxGraphSize = model.getGraphPeriodLength() - 1;
        } else {
            this.maxGraphSize = model.getGraphPeriodLength();
        }

        System.out.println(maxGraphSize);

        String period;

        if (DataHandler.getFormattedDate(sleepDataFragment.rangeMax.getTime())
                .equals(DataHandler.getFormattedDate(sleepDataFragment.today.getTime()))) {
            sleepDataFragment.nextButton.setVisibility(View.INVISIBLE);

            period = "This " + model.getGraphViewType();
        }
        else {
            sleepDataFragment.nextButton.setVisibility(View.VISIBLE);

            switch (model.getGraphViewType()) {
                case "month":
                    period = DataHandler.getMonth(min);
                    break;

                case "year":
                    period = DataHandler.getYear(min);
                    break;

                //"week"
                default:
                    period = DataHandler.getFormattedDate(min) + " - " + DataHandler.getFormattedDate(max);
                    break;
            }
        }

        graph.removeAllSeries();

        TextView graphTitle = sleepDataFragment.getView().findViewById(R.id.graphTitle);
        graphTitle.setText(period);

        graph.getViewport().setMaxX(this.maxGraphSize - 1);
        graph.getGridLabelRenderer().setNumHorizontalLabels(this.maxGraphSize);

        switch (model.getGraphViewType()) {
            case "month":
                graph.getGridLabelRenderer().setLabelFormatter(sleepDataFragment.getMonthLabelFormatter(this.maxGraphSize));
                break;

            case "year":
                graph.getGridLabelRenderer().setLabelFormatter(sleepDataFragment.getYearLabelFormatter());
                break;

            //"week"
            default:
                graph.getGridLabelRenderer().setLabelFormatter(sleepDataFragment.getWeekLabelFormatter());
                break;
        }
    }

    protected void loadGoal(String goalName) {
        if (model.getGoalMinLine(goalName) == null &&
                model.getGoalMaxLine(goalName) == null) {
            db.goalDao()
                    .loadAllByNames(new String[]{goalName})
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            goalData -> {
                                if (!goalData.isEmpty()) {
                                    model.setGoal(
                                            goalName,
                                            goalData.get(0).getValueMin(),
                                            goalData.get(0).getValueMax(),
                                            getResources().getColor(R.color.white_transparent),
                                            getResources().getColor(R.color.white_transparent)
                                    );

                                    graph.addSeries(model.getGoalMaxLine(goalName));
                                    graph.addSeries(model.getGoalMaxPoint(goalName));

                                    if (!model.getGoalMin(goalName).equals(model.getGoalMax(goalName))) {
                                        graph.addSeries(model.getGoalMinLine(goalName));
                                        graph.addSeries(model.getGoalMinPoint(goalName));
                                    }
                                }

                                loadFromDatabase(goalName);
                            },
                            Throwable::printStackTrace
                    );
        } else {
            graph.addSeries(model.getGoalMaxLine(goalName));
            graph.addSeries(model.getGoalMaxPoint(goalName));

            if (!model.getGoalMin(goalName).equals(model.getGoalMax(goalName))) {
                graph.addSeries(model.getGoalMinLine(goalName));
                graph.addSeries(model.getGoalMinPoint(goalName));
            }

            loadFromDatabase(goalName);
        }
    }

    protected void loadFromDatabase(String name) {
        String periodStart = DataHandler.getSQLiteDate(sleepDataFragment.rangeMin.getTime());
        String periodEnd = DataHandler.getSQLiteDate(sleepDataFragment.rangeMax.getTime());

        if (model.getLineSeries(name, periodStart, periodEnd) == null) {
            db.sleepDataDao()
                    .loadAllByDateRangeAndType(
                            periodStart,
                            periodEnd,
                            name
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            sleepData -> {
                                List<Double> processedSleepData = this.processFromDatabase(sleepData);

                                double goal = DataHandler.getDoubleFromTime(model.getGoalMax(name));
                                graph.getViewport().setMaxY(Math.max(goal, Collections.max(processedSleepData)) + 1);

                                int graphColor;

                                switch (name) {
                                    case "Wake-up time":
                                        graphColor = getResources().getColor(R.color.lightest_purple_sleep_transparent);
                                        break;

                                    case "Bedtime":
                                        graphColor = getResources().getColor(R.color.darkest_purple_sleep_transparent);
                                        break;

                                    //"Sleep duration"
                                    default:
                                        graphColor = getResources().getColor(R.color.purple_sleep_transparent);
                                        break;
                                }

                                model.setSeries(
                                        name,
                                        processedSleepData,
                                        periodStart,
                                        periodEnd,
                                        this.maxGraphSize,
                                        graphColor,
                                        getResources().getColor(R.color.white),
                                        getResources().getColor(R.color.white)
                                );

                                graph.addSeries(model.getLineSeries(name, periodStart, periodEnd));
                                //graph.addSeries(model.getPointsSeries(name, periodStart, periodEnd));
                            },
                            Throwable::printStackTrace
                    );
        } else {
            double goal = DataHandler.getDoubleFromTime(model.getGoalMax(name));
            graph.getViewport().setMaxY(Math.max(goal, Collections.max(model.getSeriesData(name, periodStart, periodEnd))) + 1);

            graph.addSeries(model.getLineSeries(name, periodStart, periodEnd));
            //graph.addSeries(model.getPointsSeries(name, periodStart, periodEnd));
        }
    }

    private List<Double> processFromDatabase(List<SleepData> sleepData) {
        if (sleepData.isEmpty()) {
            List<Double> processedSleepData = new ArrayList(Arrays.asList(new Double[model.getGraphPeriodLength()]));
            Collections.fill(processedSleepData, 0.0);

            return processedSleepData;
        }

        switch (model.getGraphViewType()) {
            case "month":
                return this.processMonthData(sleepData);

            case "year":
                return this.processYearData(sleepData);

            //"week"
            default:
                return this.processWeekData(sleepData);
        }
    }

    private List<Double> processWeekData(List<SleepData> sleepData) {
        List<Double> processedSleepData = new ArrayList<>();

        Calendar day = (Calendar) sleepDataFragment.rangeMin.clone();

        Calendar end = (Calendar) day.clone();
        end.add(Calendar.DAY_OF_WEEK, 6);

        while (!day.after(end)) {
            String date = DataHandler.getSQLiteDate(day.getTime());

            Optional<SleepData> sleepDataForDay = sleepData
                    .stream()
                    .filter(s -> s.getDate().equals(date))
                    .findAny();

            if (sleepDataForDay.isPresent()) {
                processedSleepData.add(DataHandler.getDoubleFromTime(sleepDataForDay.get().getValue()));
            } else {
                processedSleepData.add(0.0);
            }

            day.add(Calendar.DAY_OF_WEEK, 1);
        }

        return processedSleepData;
    }

    private List<Double> processMonthData(List<SleepData> sleepData) {
        List<Double> processedSleepData = new ArrayList<>();

        Calendar weekStart = (Calendar) sleepDataFragment.rangeMin.clone();
        Calendar weekEnd = (Calendar) sleepDataFragment.rangeMin.clone();
        weekEnd.add(Calendar.DAY_OF_MONTH, 6);

        Calendar end = (Calendar) weekStart.clone();
        end.set(Calendar.DAY_OF_MONTH, weekStart.getActualMaximum(Calendar.DATE));

        while (!weekEnd.after(end)) {
            String startDate = DataHandler.getSQLiteDate(weekStart.getTime());
            String endDate = DataHandler.getSQLiteDate(weekEnd.getTime());

            List<SleepData> sleepDataForWeek = sleepData
                    .stream()
                    .filter(s -> s.getDate().compareTo(startDate) >= 0 &&
                            s.getDate().compareTo(endDate) <= 0)
                    .collect(Collectors.toList());

            if (!sleepDataForWeek.isEmpty()) {
                List<String> sleepDataForWeekValues = sleepDataForWeek
                        .stream()
                        .map(s -> s.getValue())
                        .collect(Collectors.toList());

                List<Double> valuesForWeek = DataHandler.getDoublesFromTimes(sleepDataForWeekValues);

                double weeklyAverage = valuesForWeek
                        .stream()
                        .mapToDouble(v -> v)
                        .average()
                        .getAsDouble();

                processedSleepData.add(weeklyAverage);
            } else {
                processedSleepData.add(0.0);
            }

            weekStart.add(Calendar.DAY_OF_WEEK, 7);
            weekEnd.add(Calendar.DAY_OF_WEEK, 7);
        }

        return processedSleepData;
    }

    private List<Double> processYearData(List<SleepData> sleepData) {
        List<Double> processedSleepData = new ArrayList<>();

        String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

        for (String m : months) {
            List<SleepData> sleepDataForMonth = sleepData
                    .stream()
                    .filter(s -> s.getDate().contains("-" + m + "-"))
                    .collect(Collectors.toList());

            if (!sleepDataForMonth.isEmpty()) {
                List<String> sleepDataForMonthValues = sleepDataForMonth
                        .stream()
                        .map(s -> s.getValue())
                        .collect(Collectors.toList());

                List<Double> valuesForMonth = DataHandler.getDoublesFromTimes(sleepDataForMonthValues);

                double monthlyAverage = valuesForMonth
                        .stream()
                        .mapToDouble(v -> v)
                        .average()
                        .isPresent() ? valuesForMonth
                        .stream()
                        .mapToDouble(v -> v)
                        .average()
                        .getAsDouble() :
                        0.0;

                processedSleepData.add(monthlyAverage);
            } else {
                processedSleepData.add(0.0);
            }
        }

        return processedSleepData;
    }

    protected void loadTodaysData() {}
}
