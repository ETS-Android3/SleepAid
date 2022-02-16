package com.example.sleepaid;

import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.lifecycle.ViewModel;

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

    private LineGraphSeries<DataPoint> sleepDurationLineSeries;
    private PointsGraphSeries<DataPoint> sleepDurationPointsSeries;

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
                canvas.drawText((int) (dataPoint.getY() - 0.4) + "h", x, y, paint);
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

    public LineGraphSeries<DataPoint> getSleepDurationLineSeries() {
        return this.sleepDurationLineSeries;
    }

    public PointsGraphSeries<DataPoint> getSleepDurationPointsSeries() {
        return this.sleepDurationPointsSeries;
    }
}
