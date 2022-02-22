package com.example.sleepaid.Fragment;

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
import com.example.sleepaid.Database.SleepData;
import com.example.sleepaid.R;
import com.example.sleepaid.SharedViewModel;
import com.google.common.collect.Lists;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

@SuppressLint("NewApi")
public abstract class SleepDataGraphFragment extends Fragment {
    protected SleepDataFragment sleepDataFragment;

    protected AppDatabase db;

    protected SharedViewModel model;

    protected GraphView graph;

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        sleepDataFragment = (SleepDataFragment) getParentFragment().getParentFragment();

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

        //graph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        //graph.getViewport().setDrawBorder(true);

        loadGraph("This " + model.getGraphViewType());
    }

    protected void loadGraph(String period) {
        if (sleepDataFragment.graphRangeMax.equals(DataHandler.getFormattedDate(sleepDataFragment.today.getTime()))) {
            sleepDataFragment.nextButton.setVisibility(View.INVISIBLE);
            period = "This " + model.getGraphViewType();
        }
        else {
            sleepDataFragment.nextButton.setVisibility(View.VISIBLE);
        }

        graph.removeAllSeries();

        TextView graphTitle = sleepDataFragment.getView().findViewById(R.id.graphTitle);
        graphTitle.setText(period);

        switch (model.getGraphViewType()) {
            case "week":
                graph.getViewport().setMaxX(model.getGraphWeekLength() - 1);
                graph.getGridLabelRenderer().setNumHorizontalLabels(model.getGraphWeekLength());
                graph.getGridLabelRenderer().setLabelFormatter(sleepDataFragment.getWeekLabelFormatter());
                break;

            case "month":
                graph.getViewport().setMaxX(model.getGraphMonthLength() - 1);
                graph.getGridLabelRenderer().setNumHorizontalLabels(model.getGraphMonthLength());
                graph.getGridLabelRenderer().setLabelFormatter(sleepDataFragment.getMonthLabelFormatter(sleepDataFragment.graphRangeMin, sleepDataFragment.graphRangeMax));
                break;

            case "year":
                graph.getViewport().setMaxX(model.getGraphYearLength() - 1);
                graph.getGridLabelRenderer().setNumHorizontalLabels(model.getGraphYearLength());
                graph.getGridLabelRenderer().setLabelFormatter(sleepDataFragment.getYearLabelFormatter());
                break;
        }
    }

    protected List<Double> processFromDatabase(List<SleepData> sleepData) {
        if (sleepData.isEmpty()) {
            List<Double> processedSleepData = new ArrayList<>();

            for (int i = 0; i < model.getGraphPeriodLength(); i++) {
                processedSleepData.add(0.0);
            }

            return processedSleepData;
        }

        if (model.getGraphViewType().equals("week")) {
            return DataHandler.getDoublesFromSleepDataValues(sleepData);
        } else if (model.getGraphViewType().equals("month")) {
            List<Double> processedSleepData = new ArrayList<>();

            List<List<SleepData>> sleepDataForWeeks = Lists.partition(sleepData, 7);

            for (List<SleepData> s : sleepDataForWeeks) {
                List<Double> valuesForWeek = DataHandler.getDoublesFromSleepDataValues(s);

                double weeklyAverage = valuesForWeek
                        .stream()
                        .mapToDouble(v -> v)
                        .average()
                        .isPresent() ? valuesForWeek
                        .stream()
                        .mapToDouble(v -> v)
                        .average()
                        .getAsDouble() :
                        0.0;

                processedSleepData.add(weeklyAverage);
            }

            return processedSleepData;
        } else {
            List<Double> processedSleepData = new ArrayList<>();
            String[] months = {"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12"};

            for (String m : months) {
                List<SleepData> sleepDataForMonth = sleepData
                        .stream()
                        .filter(s -> s.getDate().contains("/" + m + "/"))
                        .collect(Collectors.toList());

                List<Double> valuesForMonth = DataHandler.getDoublesFromSleepDataValues(sleepDataForMonth);

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
            }

            return processedSleepData;
        }
    }
}
