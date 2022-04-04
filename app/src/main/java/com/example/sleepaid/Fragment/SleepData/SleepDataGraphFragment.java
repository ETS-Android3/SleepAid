package com.example.sleepaid.Fragment.SleepData;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sleepaid.App;
import com.example.sleepaid.Component.CircleBox;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Database.Notification.Notification;
import com.example.sleepaid.Database.SleepData.SleepData;
import com.example.sleepaid.Handler.DataHandler;
import com.example.sleepaid.Listener.OnSwipeTouchListener;
import com.example.sleepaid.Model.SharedViewModel;
import com.example.sleepaid.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;

import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@SuppressLint("ClickableViewAccessibility")
public class SleepDataGraphFragment extends Fragment {
    protected SleepDataFragment sleepDataFragment;

    private AppDatabase db;

    private SharedViewModel model;

    protected String fieldName;
    protected int graphColor;

    private GraphView graph;

    private int maxGraphSize;

    private int[] translation;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sleep_data_graph, container, false);
    }

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        sleepDataFragment = (SleepDataFragment) getParentFragment().getParentFragment();
        sleepDataFragment.graphFragment = this;

        db = AppDatabase.getDatabase(App.getContext());

        model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        graph = view.findViewById(R.id.sleepDataGraph);

        graph.getGridLabelRenderer().setGridColor(ContextCompat.getColor(App.getContext(), R.color.white));
        graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        graph.getViewport().setBorderColor(ContextCompat.getColor(App.getContext(), R.color.white));

        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);
        graph.getGridLabelRenderer().setHorizontalLabelsColor(ContextCompat.getColor(App.getContext(), R.color.white));

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);

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

        loadGraph(sleepDataFragment.rangeMin, sleepDataFragment.rangeMax);
    }

    protected void loadGraph(ZonedDateTime min, ZonedDateTime max) {
        // for February in non-leap years we only have 4 weeks
        if (sleepDataFragment.rangeMin.getMonthValue() == 2 &&
                sleepDataFragment.rangeMin.getYear() % 4 != 0 &&
                model.getGraphViewType().equals("month")) {
            this.maxGraphSize = model.getGraphPeriodLength() - 1;
        } else {
            this.maxGraphSize = model.getGraphPeriodLength();
        }

        String period;

        if (DataHandler.getFormattedDate(sleepDataFragment.rangeMax)
                .equals(DataHandler.getFormattedDate(sleepDataFragment.today))) {
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

        this.loadGoal();
    }

    protected void loadGoal() {
        Bitmap goalIcon = BitmapFactory.decodeResource(getResources(), R.drawable.trophy_icon);

        if (model.getGoalMinLine(this.fieldName) == null &&
                model.getGoalMaxLine(this.fieldName) == null) {
            this.db.goalDao()
                    .loadAllByNames(new String[]{this.fieldName})
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            goalData -> {
                                if (!goalData.isEmpty()) {
                                    model.setGoal(
                                            this.fieldName,
                                            goalData.get(0).getValueMin(),
                                            goalData.get(0).getValueMax(),
                                            ContextCompat.getColor(App.getContext(), R.color.white),
                                            ContextCompat.getColor(App.getContext(), R.color.white)
                                    );

                                    graph.addSeries(model.getGoalMaxLine(this.fieldName));
                                    graph.addSeries(model.getGoalMaxPoint(this.fieldName));

                                    if (!model.getGoalMin(this.fieldName).equals(model.getGoalMax(this.fieldName))) {
                                        graph.addSeries(model.getGoalMinLine(this.fieldName));
                                        graph.addSeries(model.getGoalMinPoint(this.fieldName));
                                    }
                                }

                                loadFromDatabase();
                            },
                            Throwable::printStackTrace
                    );
        } else {
            graph.addSeries(model.getGoalMaxLine(this.fieldName));
            graph.addSeries(model.getGoalMaxPoint(this.fieldName));

            if (!model.getGoalMin(this.fieldName).equals(model.getGoalMax(this.fieldName))) {
                graph.addSeries(model.getGoalMinLine(this.fieldName));
                graph.addSeries(model.getGoalMinPoint(this.fieldName));
            }

            loadFromDatabase();
        }
    }

    private void loadFromDatabase() {
        String periodStart = DataHandler.getSQLiteDate(sleepDataFragment.rangeMin);
        String periodEnd = DataHandler.getSQLiteDate(sleepDataFragment.rangeMax);

        if (model.getSeriesModel(this.fieldName, periodStart, periodEnd) == null ||
                model.getLineSeries(this.fieldName, periodStart, periodEnd) == null) {
            db.sleepDataDao()
                    .loadAllByDateRangeAndType(
                            periodStart,
                            periodEnd,
                            this.fieldName
                    )
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            sleepData -> {
                                this.setTranslation(sleepData);

                                List<Float> processedSleepData = this.processFromDatabase(sleepData);

                                model.setSeries(
                                        this.fieldName,
                                        processedSleepData,
                                        periodStart,
                                        periodEnd,
                                        this.graphColor,
                                        ContextCompat.getColor(App.getContext(), R.color.white),
                                        ContextCompat.getColor(App.getContext(), R.color.white)
                                );

                                graph.getViewport().setMaxY(model.getMaxY(this.fieldName, periodStart, periodEnd));

                                graph.addSeries(model.getLineSeries(this.fieldName, periodStart, periodEnd));
                                graph.addSeries(model.getPointsSeries(this.fieldName, periodStart, periodEnd));

                                loadTodayData();
                            },
                            Throwable::printStackTrace
                    );
        } else {
            graph.getViewport().setMaxY(model.getMaxY(this.fieldName, periodStart, periodEnd));

            graph.addSeries(model.getLineSeries(this.fieldName, periodStart, periodEnd));
            graph.addSeries(model.getPointsSeries(this.fieldName, periodStart, periodEnd));

            loadTodayData();
        }
    }

    private void setTranslation(List<SleepData> sleepData) {
        if (!sleepData.isEmpty()) {
            List<String> sleepDataValues = sleepData
                    .stream()
                    .map(s -> s.getValue())
                    .collect(Collectors.toList());

            List<Float> sleepDataNumberValues = DataHandler.getFloatsFromTimes(sleepDataValues);

            this.translation = new int[sleepDataNumberValues.size()];

            if (this.fieldName.equals("Bedtime")) {
                for (int i = 0; i < sleepDataNumberValues.size(); i++) {
                    if (sleepDataNumberValues.get(i) > 12 && sleepDataNumberValues.get(i) < 24) {
                        translation[i] = -12;
                    } else if (sleepDataNumberValues.get(i) >= 0) {
                        translation[i] = 12;
                    }
                }
            }
        }
    }

    private List<Float> processFromDatabase(List<SleepData> sleepData) {
        if (sleepData.isEmpty()) {
            List<Float> processedSleepData = new ArrayList(Arrays.asList(new Float[model.getGraphPeriodLength()]));
            Collections.fill(processedSleepData, -1.0f);

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

    private List<Float> processWeekData(List<SleepData> sleepData) {
        List<Float> processedSleepData = new ArrayList<>();

        ZonedDateTime day = sleepDataFragment.rangeMin;
        ZonedDateTime end = day.plusDays(6);

        while (!day.isAfter(end)) {
            String date = DataHandler.getSQLiteDate(day);

            Optional<SleepData> sleepDataForDay = sleepData
                    .stream()
                    .filter(s -> s.getDate().equals(date))
                    .findAny();

            if (sleepDataForDay.isPresent()) {
                float value = DataHandler.getFloatFromTime(sleepDataForDay.get().getValue());
                value += translation[sleepData.indexOf(sleepDataForDay.get())];

                processedSleepData.add(value);
            } else {
                processedSleepData.add(-1.0f);
            }

            day = day.plusDays(1);
        }

        return processedSleepData;
    }

    private List<Float> processMonthData(List<SleepData> sleepData) {
        List<Float> processedSleepData = new ArrayList<>();

        ZonedDateTime weekStart = sleepDataFragment.rangeMin;
        ZonedDateTime weekEnd = sleepDataFragment.rangeMin.plusDays(6);
        ZonedDateTime end = weekStart.withDayOfMonth(YearMonth.of(weekStart.getYear(), weekStart.getMonthValue()).lengthOfMonth());

        while (!weekStart.isAfter(end)) {
            String startDate = DataHandler.getSQLiteDate(weekStart);
            String endDate = DataHandler.getSQLiteDate(weekEnd);

            List<SleepData> sleepDataForWeek = sleepData
                    .stream()
                    .filter(s -> s.getDate().compareTo(startDate) >= 0 &&
                            s.getDate().compareTo(endDate) <= 0)
                    .collect(Collectors.toList());

            if (!sleepDataForWeek.isEmpty()) {
                List<Float> valuesForWeek = sleepDataForWeek
                        .stream()
                        .map(s -> DataHandler.getFloatFromTime(s.getValue()) +
                                translation[sleepData.indexOf(s)])
                        .collect(Collectors.toList());

                float weeklyAverage = (float) valuesForWeek
                        .stream()
                        .mapToDouble(v -> v)
                        .average()
                        .getAsDouble();

                processedSleepData.add(weeklyAverage);
            } else {
                processedSleepData.add(-1.0f);
            }

            weekStart = weekStart.plusDays(7);
            weekEnd = weekEnd.plusDays(7).isAfter(end) ?
                    end :
                    weekEnd.plusDays(7);
        }

        return processedSleepData;
    }

    private List<Float> processYearData(List<SleepData> sleepData) {
        List<Float> processedSleepData = new ArrayList<>();

        String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

        for (String m : months) {
            List<SleepData> sleepDataForMonth = sleepData
                    .stream()
                    .filter(s -> s.getDate().contains("-" + m + "-"))
                    .collect(Collectors.toList());

            if (!sleepDataForMonth.isEmpty()) {
                List<Float> valuesForMonth = sleepDataForMonth
                        .stream()
                        .map(s -> DataHandler.getFloatFromTime(s.getValue()) +
                                translation[sleepData.indexOf(s)])
                        .collect(Collectors.toList());

                float monthlyAverage = valuesForMonth
                        .stream()
                        .mapToDouble(v -> v)
                        .average()
                        .isPresent() ? (float) valuesForMonth
                        .stream()
                        .mapToDouble(v -> v)
                        .average()
                        .getAsDouble() :
                        0.0f;

                processedSleepData.add(monthlyAverage);
            } else {
                processedSleepData.add(-1.0f);
            }
        }

        return processedSleepData;
    }

    protected void loadTodayData() {
        CircleBox todayDataBox = sleepDataFragment.getView().findViewById(R.id.middleBox);

        String todayData = this.model.getTodaySleepData(this.fieldName) == null ||
                this.model.getTodaySleepData(this.fieldName).getValue() == null
                || this.model.getTodaySleepData(this.fieldName).getValue().isEmpty() ?
                "-" :
                this.model.getTodaySleepData(this.fieldName).getValue();

        todayDataBox.setText(todayData);

        if (!todayData.equals("-") && !this.fieldName.equals("Technology use")) {
            this.checkProgress(todayData);
        }
    }

    private void checkProgress(String value) {
        float goalMax;
        float goalMin;

        if (this.fieldName.equals("Bedtime")) {
            goalMax = DataHandler.getFloatFromTime(this.model.getGoalMax(this.fieldName)) > 12 ?
                    DataHandler.getFloatFromTime(this.model.getGoalMax(this.fieldName)) :
                    DataHandler.getFloatFromTime(this.model.getGoalMax(this.fieldName)) + 24;

            goalMin = DataHandler.getFloatFromTime(this.model.getGoalMin(this.fieldName)) > 12 ?
                    DataHandler.getFloatFromTime(this.model.getGoalMin(this.fieldName)) :
                    DataHandler.getFloatFromTime(this.model.getGoalMin(this.fieldName)) + 24;
        } else {
            goalMax = DataHandler.getFloatFromTime(this.model.getGoalMax(this.fieldName));
            goalMin = DataHandler.getFloatFromTime(this.model.getGoalMin(this.fieldName));
        }

        if (DataHandler.getFloatFromTime(value) <= goalMax && DataHandler.getFloatFromTime(value) >= goalMin) {
            Notification progressNotification = new Notification(
                    "Congratulations!",
                    "You\'ve achieved your " + this.fieldName.toLowerCase() + " goal today.\nYou're doing amazingly!",
                    DataHandler.getFormattedTime(12, 00),
                    0,
                    0
            );

            progressNotification.setId((int) System.currentTimeMillis());
            progressNotification.schedule(requireActivity());
        }
    }
}
