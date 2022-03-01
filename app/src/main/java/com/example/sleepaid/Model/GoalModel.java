package com.example.sleepaid.Model;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;

import com.example.sleepaid.DataHandler;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

public class GoalModel {
    private String goalName;

    private String goalMin;
    private String goalMax;

    private LineGraphSeries<DataPoint> goalMinLine;
    private LineGraphSeries<DataPoint> goalMaxLine;

    private PointsGraphSeries<DataPoint> goalMinPoint;
    private PointsGraphSeries<DataPoint> goalMaxPoint;

    public GoalModel(String goalName,
                     String goalValueMin,
                     String goalValueMax,
                     int lineColor,
                     int lineLength,
                     int pointColor) {
        this.goalName = goalName;

        this.setGoalMin(goalValueMin, lineColor, lineLength, pointColor);
        this.setGoalMax(goalValueMax, lineColor, lineLength, pointColor);
    }

    public void update(String goalValueMin,
                       String goalValueMax,
                       int lineColor,
                       int lineLength,
                       int pointColor) {
        this.setGoalMin(goalValueMin, lineColor, lineLength, pointColor);
        this.setGoalMin(goalValueMax, lineColor, lineLength, pointColor);
    }

    public void setGoalMin(String goalValue, int lineColor, int lineLength, int pointColor) {
        this.goalMin = goalValue;

        double goalValueNumber = DataHandler.getDoubleFromTime(goalValue);

        this.goalMinLine = this.createGoalLine(goalValueNumber, lineColor, lineLength);
        this.goalMinPoint = this.createGoalPoint(goalValue, goalValueNumber, -1, pointColor);
    }

    public void setGoalMax(String goalValue, int lineColor, int lineLength, int pointColor) {
        this.goalMax = goalValue;

        double goalValueNumber = DataHandler.getDoubleFromTime(goalValue);

        this.goalMaxLine = this.createGoalLine(goalValueNumber, lineColor, lineLength);
        this.goalMaxPoint = this.createGoalPoint(goalValue, goalValueNumber, 1, pointColor);
    }

    public String getGoalName() {
        return this.goalName;
    }

    public String getGoalMin() {
        return this.goalMin;
    }

    public LineGraphSeries<DataPoint> getGoalMinLine() {
        return this.goalMinLine;
    }

    public PointsGraphSeries<DataPoint> getGoalMinPoint() {
        return this.goalMinPoint;
    }

    public String getGoalMax() {
        return this.goalMax;
    }

    public LineGraphSeries<DataPoint> getGoalMaxLine() {
        return this.goalMaxLine;
    }

    public PointsGraphSeries<DataPoint> getGoalMaxPoint() {
        return this.goalMaxPoint;
    }

    private LineGraphSeries<DataPoint> createGoalLine(double goalValue, int lineColor, int lineLength) {
        LineGraphSeries<DataPoint> goalLine = new LineGraphSeries<>();

        for (int i = 0; i < lineLength; i++) {
            goalLine.appendData(
                    new DataPoint(i, goalValue),
                    true,
                    lineLength
            );
        }

        this.styleGoalLine(goalLine, lineColor);

        return goalLine;
    }

    private void styleGoalLine(LineGraphSeries<DataPoint> goalLine, int lineColor) {
        Paint paint = new Paint();

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.5f);

        paint.setPathEffect(new DashPathEffect(new float[]{8, 5}, 0));
        paint.setColor(lineColor);

        goalLine.setCustomPaint(paint);
        goalLine.setDrawAsPath(true);
    }

    public PointsGraphSeries<DataPoint> createGoalPoint(String goalText, double goalValue, int position, int pointColor) {
        PointsGraphSeries<DataPoint> goalPoint = new PointsGraphSeries<>();

        goalPoint.appendData(
                new DataPoint(0, goalValue + 0.25 * position),
                true,
                1);

        this.styleGoalPoint(goalText, goalPoint, pointColor);

        return goalPoint;
    }

    private void styleGoalPoint(String goalText, PointsGraphSeries<DataPoint> goalPoint, int pointColor) {
        goalPoint.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setColor(pointColor);
                paint.setTextSize(38);

                canvas.drawText(goalText, x, y, paint);
            }
        });
    }
}
