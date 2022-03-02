package com.example.sleepaid.Model;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.List;

public class GraphSeriesModel {
    private String dataType;

    private List<Double> data;

    private LineGraphSeries<DataPoint> lineSeries;
    private PointsGraphSeries<DataPoint> pointsSeries;

    private String periodStart;
    private String periodEnd;

    public GraphSeriesModel(String dataType,
                            List<Double> data,
                            String periodStart,
                            String periodEnd,
                            int seriesLength,
                            int backgroundColor,
                            int lineColor,
                            int pointsColor) {
        this.dataType = dataType;

        this.data = data;

        this.periodStart = periodStart;
        this.periodEnd = periodEnd;

        this.setLineSeries(data, seriesLength, backgroundColor, lineColor);
        this.setPointsSeries(data, seriesLength, pointsColor);
    }

    public void update(List<Double> data,
                       String periodStart,
                       String periodEnd,
                       int seriesLength,
                       int backgroundColor,
                       int lineColor,
                       int pointsColor) {
        this.data = data;

        this.periodStart = periodStart;
        this.periodEnd = periodEnd;

        this.setLineSeries(data, seriesLength, backgroundColor, lineColor);
        this.setPointsSeries(data, seriesLength, pointsColor);
    }

    public void setLineSeries(List<Double> data,
                              int seriesLength,
                              int backgroundColor,
                              int lineColor) {
        this.lineSeries = this.createLineSeries(data, seriesLength, backgroundColor, lineColor);
    }

    public void setPointsSeries(List<Double> data,
                                int seriesLength,
                                int pointsColor) {
        this.pointsSeries = this.createPointsSeries(data, seriesLength, pointsColor);
    }

    public String getDataType() {
        return this.dataType;
    }

    public List<Double> getData() {
        return this.data;
    }

    public LineGraphSeries<DataPoint> getLineSeries() {
        return this.lineSeries;
    }

    public PointsGraphSeries<DataPoint> getPointsSeries() {
        return this.pointsSeries;
    }

    public String getPeriod() {
        return this.periodStart + "-" + this.periodEnd;
    }

    private LineGraphSeries<DataPoint> createLineSeries(List<Double> data,
                                                        int seriesLength,
                                                        int backgroundColor,
                                                        int lineColor) {
        LineGraphSeries<DataPoint> lineSeries = new LineGraphSeries<>();

        for (int i = 0; i < seriesLength; i++) {
            lineSeries.appendData(
                    new DataPoint(i, data.get(i)),
                    true,
                    seriesLength
            );
        }

        this.styleLineSeries(lineSeries, backgroundColor, lineColor);

        return lineSeries;
    }

    private void styleLineSeries(LineGraphSeries<DataPoint> lineSeries,
                                 int backgroundColor,
                                 int lineColor) {
        lineSeries.setDrawBackground(true);
        lineSeries.setDrawDataPoints(true);

        lineSeries.setBackgroundColor(backgroundColor);
        lineSeries.setColor(lineColor);
    }

    public PointsGraphSeries<DataPoint> createPointsSeries(List<Double> data,
                                                           int seriesLength,
                                                           int pointsColor) {
        PointsGraphSeries<DataPoint> pointsSeries = new PointsGraphSeries<>();

        for (int i = 0; i < seriesLength; i++) {
            if (data.get(i) != 0) {
                pointsSeries.appendData(
                        new DataPoint(i, data.get(i) + 0.5),
                        true,
                        seriesLength
                );
            }
        }

        this.stylePointsSeries(pointsSeries, pointsColor);

        return pointsSeries;
    }

    private void stylePointsSeries(PointsGraphSeries<DataPoint> pointsSeries,
                                   int pointsColor) {
        pointsSeries.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setColor(pointsColor);
                paint.setTextSize(38);

                int hours = (int)(dataPoint.getY() - 0.5);
                int minutes = (int) (((dataPoint.getY() - 0.5) - hours) * 60);

                String text = getPointText(hours, minutes);

                canvas.drawText(text, x, y, paint);
            }
        });
    }

    private String getPointText(int hours, int minutes) {
        String text;

        switch (this.dataType) {
            case "Sleep duration":
                text = minutes == 0 ?
                        hours + "h" :
                        hours + "h" + minutes + "m";
                break;

            //"Wake-up time" or "Bedtime
            default:
                String delimiter = minutes < 10 ? ":0" : ":";
                text = hours + delimiter + minutes;
                break;
        }

        return text;
    }
}
