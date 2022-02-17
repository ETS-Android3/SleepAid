package com.example.sleepaid.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.sleepaid.App;
import com.example.sleepaid.DataHandler;
import com.example.sleepaid.R;
import com.example.sleepaid.SharedViewModel;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.Calendar;

public class SleepDataFragment extends Fragment {
    private SharedViewModel model;

    private GraphView sleepGraph;

    Button previousButton;
    Button nextButton;

    private Calendar today;
    private Calendar rangeMax;
    private Calendar rangeMin;

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

        previousButton = getView().findViewById(R.id.previousButton);
        previousButton.setOnClickListener(loadPeriod);

        nextButton = getView().findViewById(R.id.nextButton);
        nextButton.setOnClickListener(loadPeriod);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                App.getContext(),
                R.array.calendarOptions,
                R.layout.calendar_dropdown
        );
        adapter.setDropDownViewResource(R.layout.calendar_item_dropdown);

        Spinner calendarDropdown = getView().findViewById(R.id.calendarDropdown);
        calendarDropdown.setAdapter(adapter);

        if (model.getGraphViewType() == null) {
            model.setGraphViewType("week");
        }

        today = Calendar.getInstance();
        getTodaysRange();

        loadDurationGraph("This " + model.getGraphViewType());
    }

    private void getTodaysRange() {
        rangeMax = Calendar.getInstance();
        rangeMin = Calendar.getInstance();

        graphRangeMax = DataHandler.getFormattedDate(rangeMax.getTime());

        switch (model.getGraphViewType()) {
            case "week":
                // Days are indexed from 1 starting with Sunday, so add 2 to get to Monday
                rangeMin.add(Calendar.DAY_OF_WEEK, -rangeMax.get(Calendar.DAY_OF_WEEK) + 2);
                break;

            case "month":
                rangeMin.add(Calendar.DAY_OF_MONTH, -rangeMax.get(Calendar.DAY_OF_MONTH) + 1);
                break;

            case "year":
                rangeMin.add(Calendar.DAY_OF_YEAR, -rangeMax.get(Calendar.DAY_OF_YEAR) + 1);
                break;
        }

        graphRangeMin = DataHandler.getFormattedDate(rangeMin.getTime());
    }

    private void loadDurationGraph(String period) {
        if (graphRangeMax.equals(DataHandler.getFormattedDate(today.getTime()))) {
            nextButton.setVisibility(View.INVISIBLE);
            period = "This " + model.getGraphViewType();
        }
        else {
            nextButton.setVisibility(View.VISIBLE);
        }

        sleepGraph.removeAllSeries();

        TextView graphTitle = getView().findViewById(R.id.graphTitle);
        graphTitle.setText(period);

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

    private View.OnClickListener loadPeriod = new View.OnClickListener() {
        public void onClick(View view) {
            int direction = 1;

            if (view.getId() == R.id.previousButton) {
                direction = -1;
            }

            Calendar endOfRange = (Calendar) rangeMin.clone();

            switch(model.getGraphViewType()) {
                case "week":
                    // rangeMin will always be a Monday, so we can just move by 7 days
                    rangeMin.add(Calendar.DAY_OF_WEEK, 7 * direction);

                    // We need to make sure we don't go past today
                    endOfRange = (Calendar) rangeMin.clone();
                    endOfRange.add(Calendar.DAY_OF_WEEK, 6);
                    break;

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
            }

            // We need to make sure we don't go past today when clicking next
            rangeMax = endOfRange.after(today) ? (Calendar) today.clone() : (Calendar) endOfRange.clone();

            graphRangeMin = DataHandler.getFormattedDate(rangeMin.getTime());
            graphRangeMax = DataHandler.getFormattedDate(rangeMax.getTime());

            loadDurationGraph(graphRangeMin + " - " + graphRangeMax);
        }
    };
}