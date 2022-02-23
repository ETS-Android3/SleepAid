package com.example.sleepaid;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.lifecycle.ViewModel;

import com.example.sleepaid.Database.Answer;
import com.example.sleepaid.Database.Option;
import com.example.sleepaid.Database.Question;
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

    private String[] sleepGraphTabsText;

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

    public void setSleepGraphTabsText(String[] graphTabsText) {
        this.sleepGraphTabsText = graphTabsText;
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
            case "week":
                return this.graphWeekLength;

            case "month":
                return this.graphMonthLength;

            case "year":
                return this.graphYearLength;

            default:
                return 0;
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

    public String[] getSleepGraphTabsText() {
        return this.sleepGraphTabsText;
    }

    public String getSleepGraphTabsText(int position) {
        return this.sleepGraphTabsText[position];
    }
}
