package com.example.sleepaid.Fragment;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sleepaid.R;
import com.example.sleepaid.SharedViewModel;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SleepDataFragment extends Fragment {
    private SharedViewModel model;

    private GraphView sleepGraph;

    Calendar calendar;
    private String graphRangeMin;
    private String graphRangeMax;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sleep_data, container, false);
    }

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        int sizeInDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                25,
                getResources().getDisplayMetrics()
        );

        sleepGraph = getView().findViewById(R.id.sleepGraph);

        sleepGraph.setTitleTextSize(sizeInDp);
        sleepGraph.setTitleColor(getResources().getColor(R.color.white));

        sleepGraph.getGridLabelRenderer().setGridColor(getResources().getColor(R.color.white));
        sleepGraph.getGridLabelRenderer().setVerticalLabelsVisible(false);

        sleepGraph.getViewport().setXAxisBoundsManual(true);
        sleepGraph.getViewport().setYAxisBoundsManual(true);

        sleepGraph.getGridLabelRenderer().setHorizontalLabelsColor(getResources().getColor(R.color.white));

        sleepGraph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        //sleepGraph.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.HORIZONTAL);
        //sleepGraph.getViewport().setDrawBorder(true);
        sleepGraph.getViewport().setBorderColor(getResources().getColor(R.color.white));

        if (model.getGraphViewType() == null) {
            model.setGraphViewType("week");
        }

        calendar = Calendar.getInstance();
        graphRangeMax = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.getTime());

        switch (model.getGraphViewType()) {
            case "week":
                // Days are indexed from 1 starting with Sunday, so add 2 to get to Monday
                calendar.add(Calendar.DAY_OF_WEEK, -calendar.get(Calendar.DAY_OF_WEEK) + 2);
                break;

            case "month":
                calendar.add(Calendar.DAY_OF_MONTH, -calendar.get(Calendar.DAY_OF_MONTH) + 1);
                break;

            case "year":
                calendar.add(Calendar.DAY_OF_YEAR, -calendar.get(Calendar.DAY_OF_YEAR) + 1);
                break;
        }

        graphRangeMin = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.getTime());

        loadDurationGraph("This " + model.getGraphViewType());
    }

    private void loadDurationGraph(String period) {
        sleepGraph.removeAllSeries();
        sleepGraph.setTitle(period);

        switch(model.getGraphViewType()) {
            case "week":
                sleepGraph.getViewport().setMaxX(6);
                sleepGraph.getGridLabelRenderer().setNumHorizontalLabels(7);
                sleepGraph.getGridLabelRenderer().setLabelFormatter(this.getWeekLabelFormatter());
                break;

            case "month":
                sleepGraph.getViewport().setMaxX(3);
                sleepGraph.getGridLabelRenderer().setNumHorizontalLabels(4);
                sleepGraph.getGridLabelRenderer().setLabelFormatter(this.getMonthLabelFormatter(graphRangeMin, graphRangeMax));
                break;

            case "year":
                sleepGraph.getViewport().setMaxX(11);
                sleepGraph.getGridLabelRenderer().setNumHorizontalLabels(12);
                sleepGraph.getGridLabelRenderer().setLabelFormatter(this.getYearLabelFormatter());
                break;
        }

        //TODO load from database based on current range
        model.setSleepDurationLineSeries(
                new LineGraphSeries<>(new DataPoint[]{
                        new DataPoint(0, 1),
                        new DataPoint(1, 3),
                        new DataPoint(2, 4),
                        new DataPoint(3, 9),
                        new DataPoint(4, 6),
                        new DataPoint(5, 3),
                        new DataPoint(6, 6)
                }),
                getResources().getColor(R.color.white),
                getResources().getColor(R.color.white)
        );

        model.setSleepDurationPointsSeries(
                new PointsGraphSeries<>(new DataPoint[]{
                        new DataPoint(0, 1.5),
                        new DataPoint(0.85, 3.4),
                        new DataPoint(1.85, 4.4),
                        new DataPoint(2.85, 9.4),
                        new DataPoint(3.85, 6.4),
                        new DataPoint(4.85, 3.4),
                        new DataPoint(5.85, 6.4)
                }),
                getResources().getColor(R.color.white)
        );

        sleepGraph.getViewport().setMaxY(10);

        sleepGraph.addSeries(model.getSleepDurationLineSeries());
        sleepGraph.addSeries(model.getSleepDurationPointsSeries());
    }

    private DefaultLabelFormatter getWeekLabelFormatter() {
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

    private DefaultLabelFormatter getMonthLabelFormatter(String graphRangeMin, String graphRangeMax) {
        String[] weeks = {graphRangeMin, graphRangeMax};

        return new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show months of the year
                    return weeks[(int) value];
                } else {
                    return "";
                }
            }
        };
    }

    private DefaultLabelFormatter getYearLabelFormatter() {
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
}