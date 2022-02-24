package com.example.sleepaid;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.lifecycle.ViewModel;

import com.example.sleepaid.Database.Answer.Answer;
import com.example.sleepaid.Database.Option.Option;
import com.example.sleepaid.Database.Question.Question;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.List;

public class SharedViewModel extends ViewModel {
    private List<Question> questions;
    private List<Option> options;
    private List<Answer> answers;

    private int currentQuestionId;

    private String graphViewType;

    private int graphWeekLength;
    private int graphMonthLength;
    private int graphYearLength;

    private LineGraphSeries<DataPoint> sleepDurationLineSeries;
    private PointsGraphSeries<DataPoint> sleepDurationPointsSeries;

    private LineGraphSeries<DataPoint> wakeupTimeLineSeries;
    private PointsGraphSeries<DataPoint> wakeupTimePointsSeries;

    private LineGraphSeries<DataPoint> bedtimeLineSeries;
    private PointsGraphSeries<DataPoint> bedtimePointsSeries;

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public void setCurrentQuestionId(int currentQuestionId) {
        this.currentQuestionId = currentQuestionId;
    }

    public void setGraphViewType(String graphViewType) {
        this.graphViewType = graphViewType;
    }

    public void setGraphWeekLength(int graphWeekLength) {
        this.graphWeekLength = graphWeekLength;
    }

    public void setGraphMonthLength(int graphMonthLength) {
        this.graphMonthLength = graphMonthLength;
    }

    public void setGraphYearLength(int graphYearLength) {
        this.graphYearLength = graphYearLength;
    }

    public void setSleepDurationLineSeries(LineGraphSeries<DataPoint> sleepDurationLineSeries, int backgroundColor, int lineColor) {
        this.sleepDurationLineSeries = sleepDurationLineSeries;

        this.sleepDurationLineSeries.setDrawBackground(true);
        this.sleepDurationLineSeries.setDrawDataPoints(true);
        //this.sleepDurationLineSeries.setBackgroundColor(backgroundColor);
        this.sleepDurationLineSeries.setColor(lineColor);
    }

    public void setSleepDurationPointsSeries(PointsGraphSeries<DataPoint> sleepDurationPointsSeries, int pointsColor) {
        this.sleepDurationPointsSeries = sleepDurationPointsSeries;

        this.sleepDurationPointsSeries.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setColor(pointsColor);
                paint.setTextSize(38);

                String text = (dataPoint.getY() - 0.5) % 1 == 0 ?
                        (int)(dataPoint.getY() - 0.5) + "h" :
                        String.format("%.1f", dataPoint.getY() - 0.5) + "h";

                canvas.drawText(text, x, y, paint);
            }
        });
    }

    public void setWakeupTimeLineSeries(LineGraphSeries<DataPoint> wakeupTimeLineSeries, int backgroundColor, int lineColor) {
        this.wakeupTimeLineSeries = wakeupTimeLineSeries;

        this.wakeupTimeLineSeries.setDrawBackground(true);
        this.wakeupTimeLineSeries.setDrawDataPoints(true);
        //this.sleepDurationLineSeries.setBackgroundColor(backgroundColor);
        this.wakeupTimeLineSeries.setColor(lineColor);
    }

    public void setWakeupTimePointsSeries(PointsGraphSeries<DataPoint> wakeupTimePointsSeries, int pointsColor) {
        this.wakeupTimePointsSeries = wakeupTimePointsSeries;

        this.wakeupTimePointsSeries.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setColor(pointsColor);
                paint.setTextSize(38);

                //TODO figure out if it's AM or PM
                String text = (dataPoint.getY() - 0.5) % 1 == 0 ?
                        (int)(dataPoint.getY() - 0.5) + "AM" :
                        String.format("%.1f", dataPoint.getY() - 0.5) + "AM";

                canvas.drawText(text, x, y, paint);
            }
        });
    }

    public void setBedtimeLineSeries(LineGraphSeries<DataPoint> bedtimeLineSeries, int backgroundColor, int lineColor) {
        this.bedtimeLineSeries = bedtimeLineSeries;

        this.bedtimeLineSeries.setDrawBackground(true);
        this.bedtimeLineSeries.setDrawDataPoints(true);
        //this.sleepDurationLineSeries.setBackgroundColor(backgroundColor);
        this.bedtimeLineSeries.setColor(lineColor);
    }

    public void setBedtimePointsSeries(PointsGraphSeries<DataPoint> bedtimePointsSeries, int pointsColor) {
        this.bedtimePointsSeries = bedtimePointsSeries;

        this.bedtimePointsSeries.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setColor(pointsColor);
                paint.setTextSize(38);

                //TODO figure out if it's AM or PM
                String text = (dataPoint.getY() - 0.5) % 1 == 0 ?
                        (int)(dataPoint.getY() - 0.5) + "PM" :
                        String.format("%.1f", dataPoint.getY() - 0.5) + "PM";

                canvas.drawText(text, x, y, paint);
            }
        });
    }

    public List<Question> getQuestions() {
        return this.questions;
    }

    public List<Option> getOptions() {
        return this.options;
    }

    public List<Answer> getAnswers() {
        return this.answers;
    }

    public int getCurrentQuestionId() {
        return this.currentQuestionId;
    }

    public String getGraphViewType() {
        return this.graphViewType;
    }

    public int getGraphPeriodLength() {
        switch (this.graphViewType) {
            case "month":
                return this.graphMonthLength;

            case "year":
                return this.graphYearLength;

            //"week"
            default:
                return this.graphWeekLength;
        }
    }

    public int getGraphWeekLength() {
        return this.graphWeekLength;
    }

    public int getGraphMonthLength() {
        return this.graphMonthLength;
    }

    public int getGraphYearLength() {
        return this.graphYearLength;
    }

    public LineGraphSeries<DataPoint> getSleepDurationLineSeries() {
        return this.sleepDurationLineSeries;
    }

    public PointsGraphSeries<DataPoint> getSleepDurationPointsSeries() {
        return this.sleepDurationPointsSeries;
    }

    public LineGraphSeries<DataPoint> getWakeupTimeLineSeries() {
        return this.wakeupTimeLineSeries;
    }

    public PointsGraphSeries<DataPoint> getWakeupTimePointsSeries() {
        return this.wakeupTimePointsSeries;
    }

    public LineGraphSeries<DataPoint> getBedtimeLineSeries() {
        return this.bedtimeLineSeries;
    }

    public PointsGraphSeries<DataPoint> getBedtimePointsSeries() {
        return this.bedtimePointsSeries;
    }
}
