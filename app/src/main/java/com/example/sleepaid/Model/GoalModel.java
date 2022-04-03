package com.example.sleepaid.Model;

import android.graphics.DashPathEffect;
import android.graphics.Paint;

import com.example.sleepaid.Handler.DataHandler;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

public class GoalModel {
    private String goalName;

    private int[] translation;

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

        this.setTranslation(goalValueMin, goalValueMax);

        this.setGoalMin(goalValueMin, lineColor, lineLength, pointColor);
        this.setGoalMax(goalValueMax, lineColor, lineLength, pointColor);
    }

    public void update(String goalValueMin,
                       String goalValueMax,
                       int lineColor,
                       int lineLength,
                       int pointColor) {
        this.setTranslation(goalValueMin, goalValueMax);

        this.setGoalMin(goalValueMin, lineColor, lineLength, pointColor);
        this.setGoalMin(goalValueMax, lineColor, lineLength, pointColor);
    }

    private void setTranslation(String goalValueMin, String goalValueMax) {
        this.translation = new int[2];

        double goalValueMinNumber = DataHandler.getDoubleFromTime(goalValueMin);
        double goalValueMaxNumber = DataHandler.getDoubleFromTime(goalValueMax);

        if (this.goalName.equals("Bedtime")) {
            if (goalValueMinNumber > 12 && goalValueMinNumber < 24) {
                translation[0] = -12;
            } else if (goalValueMinNumber >= 0) {
                translation[0] = 12;
            }

            if (goalValueMaxNumber > 12 && goalValueMaxNumber < 24) {
                translation[1] = -12;
            } else if (goalValueMinNumber >= 0) {
                translation[1] = 12;
            }
        }
    }

    private void setGoalMin(String goalValue,
                            int lineColor,
                            int lineLength,
                            int pointColor) {
        this.goalMin = goalValue;

        double goalValueNumber = DataHandler.getDoubleFromTime(goalValue);

        this.goalMinLine = this.createGoalLine(goalValueNumber, 0, lineColor, lineLength);
        this.goalMinPoint = this.createGoalPoint(goalValue, goalValueNumber, 0, -0.5, pointColor);
    }

    private void setGoalMax(String goalValue,
                            int lineColor,
                            int lineLength,
                            int pointColor) {
        this.goalMax = goalValue;

        double goalValueNumber = DataHandler.getDoubleFromTime(goalValue);

        this.goalMaxLine = this.createGoalLine(goalValueNumber, 1, lineColor, lineLength);
        this.goalMaxPoint = this.createGoalPoint(goalValue, goalValueNumber, 1, 0.25, pointColor);
    }

    public String getGoalName() {
        return this.goalName;
    }

    public int getTranslation(int i) {
        return this.translation[i];
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

    private LineGraphSeries<DataPoint> createGoalLine(double goalValue,
                                                      int goalType,
                                                      int lineColor,
                                                      int lineLength) {
        LineGraphSeries<DataPoint> goalLine = new LineGraphSeries<>();

        for (int i = 0; i < lineLength; i++) {
            goalLine.appendData(
                    new DataPoint(i, goalValue + this.translation[goalType]),
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

    private PointsGraphSeries<DataPoint> createGoalPoint(String goalText,
                                                         double goalValue,
                                                         int goalType,
                                                         double ratio,
                                                         int pointColor) {
        PointsGraphSeries<DataPoint> goalPoint = new PointsGraphSeries<>();

        goalPoint.appendData(
                new DataPoint(0, goalValue + this.translation[goalType] + ratio),
                true,
                1);

        this.styleGoalPoint(goalText, goalPoint, pointColor);

        return goalPoint;
    }

    private void styleGoalPoint(String goalText,
                                PointsGraphSeries<DataPoint> goalPoint,
                                int pointColor) {
        goalPoint.setCustomShape((canvas, paint, x, y, dataPoint) -> {
            paint.setColor(pointColor);
            paint.setTextSize(38);

            canvas.drawText("Goal: " + goalText, x, y, paint);
        });
    }
}
