package com.example.sleepaid.MainMenu.Fragment.SleepData;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sleepaid.CircleBox;
import com.example.sleepaid.DataHandler;
import com.example.sleepaid.R;
import com.example.sleepaid.TextBox;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BedtimeGraphFragment extends SleepDataGraphFragment {
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sleep_data_graph, container, false);
    }

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    protected void loadGraph(Date min, Date max) {
        super.loadGraph(min, max);

        db.sleepDataDao()
                .loadAllByDateRangeAndType(
                        DataHandler.getSQLiteDate(sleepDataFragment.rangeMin.getTime()),
                        DataHandler.getSQLiteDate(sleepDataFragment.rangeMax.getTime()),
                        "Bedtime"
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        sleepData -> {
                            //TODO make database composite primary key with date and field
                            //TODO fix this for less values than required
                            //TODO clear points for dates after today
                            List<Double> processedSleepData = processFromDatabase(sleepData);

                            LineGraphSeries<DataPoint> lineGraphSeries = new LineGraphSeries<>();
                            PointsGraphSeries<DataPoint> pointsGraphSeries = new PointsGraphSeries<>();

                            int maxGraphSize = model.getGraphPeriodLength();

                            for (int i = 0; i < Math.min(maxGraphSize, processedSleepData.size()); i++) {
                                lineGraphSeries.appendData(
                                        new DataPoint(i, processedSleepData.get(i)),
                                        true,
                                        maxGraphSize
                                );

                                if (processedSleepData.get(i) != 0) {
                                    pointsGraphSeries.appendData(
                                            new DataPoint(Math.max(0, i - 0.15), processedSleepData.get(i) + 0.5),
                                            true,
                                            maxGraphSize
                                    );
                                }
                            }

                            model.setBedtimeLineSeries(
                                    lineGraphSeries,
                                    getResources().getColor(R.color.white),
                                    getResources().getColor(R.color.white)
                            );

                            model.setBedtimePointsSeries(
                                    pointsGraphSeries,
                                    getResources().getColor(R.color.white)
                            );

                            graph.getViewport().setMaxY(Collections.max(processedSleepData) + 1);

                            graph.addSeries(model.getBedtimeLineSeries());
                            graph.addSeries(model.getBedtimePointsSeries());
                        },
                        Throwable::printStackTrace
                );
    }

    protected void loadData() {
//        TextBox durationBox = sleepDataFragment.getView().findViewById(R.id.rightBox);
//        durationBox.setText(sleepDataFragment.todayDuration);
//
//        TextBox wakeupTimeBox = sleepDataFragment.getView().findViewById(R.id.leftBox);
//        wakeupTimeBox.setText(sleepDataFragment.todayWakeupTime);

        CircleBox bedtimeBox = sleepDataFragment.getView().findViewById(R.id.middleBox);
        bedtimeBox.setText(sleepDataFragment.todayBedtime);
    }
}