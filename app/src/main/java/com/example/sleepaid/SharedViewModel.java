package com.example.sleepaid;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
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
    private LineGraphSeries<DataPoint> sleepDurationGoalMinLine;
    private LineGraphSeries<DataPoint> sleepDurationGoalMaxLine;

    private LineGraphSeries<DataPoint> wakeupTimeLineSeries;
    private PointsGraphSeries<DataPoint> wakeupTimePointsSeries;
    private LineGraphSeries<DataPoint> wakeupTimeGoalMinLine;
    private LineGraphSeries<DataPoint> wakeupTimeGoalMaxLine;

    private LineGraphSeries<DataPoint> bedtimeLineSeries;
    private PointsGraphSeries<DataPoint> bedtimePointsSeries;
    private LineGraphSeries<DataPoint> bedtimeGoalMinLine;
    private LineGraphSeries<DataPoint> bedtimeGoalMaxLine;

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

    private void setSleepDurationLineSeries(LineGraphSeries<DataPoint> sleepDurationLineSeries, int backgroundColor, int lineColor) {
        this.sleepDurationLineSeries = sleepDurationLineSeries;

        this.styleLineSeries(this.sleepDurationLineSeries, backgroundColor, lineColor);
    }

    private void setWakeupTimeLineSeries(LineGraphSeries<DataPoint> wakeupTimeLineSeries, int backgroundColor, int lineColor) {
        this.wakeupTimeLineSeries = wakeupTimeLineSeries;

        this.styleLineSeries(this.wakeupTimeLineSeries, backgroundColor, lineColor);
    }

    private void setBedtimeLineSeries(LineGraphSeries<DataPoint> bedtimeLineSeries, int backgroundColor, int lineColor) {
        this.bedtimeLineSeries = bedtimeLineSeries;

        this.styleLineSeries(this.bedtimeLineSeries, backgroundColor, lineColor);
    }

    public void setLineSeries(String fieldName, LineGraphSeries<DataPoint> lineSeries, int backgroundColor, int lineColor) {
        switch (fieldName) {
            case "Wake-up time":
                this.setWakeupTimeLineSeries(lineSeries, backgroundColor, lineColor);
                break;

            case "Bedtime":
                this.setBedtimeLineSeries(lineSeries, backgroundColor, lineColor);
                break;

            //"Sleep duration"
            default:
                this.setSleepDurationLineSeries(lineSeries, backgroundColor, lineColor);
                break;
        }
    }

    private void styleLineSeries(LineGraphSeries<DataPoint> lineSeries, int backgroundColor, int lineColor) {
        lineSeries.setDrawBackground(true);
        lineSeries.setDrawDataPoints(true);

        lineSeries.setBackgroundColor(backgroundColor);
        lineSeries.setColor(lineColor);
    }

    private void setSleepDurationPointsSeries(PointsGraphSeries<DataPoint> sleepDurationPointsSeries, int pointsColor) {
        this.sleepDurationPointsSeries = sleepDurationPointsSeries;

        this.sleepDurationPointsSeries.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setColor(pointsColor);
                paint.setTextSize(38);

                int hours = (int)(dataPoint.getY() - 0.5);
                int minutes = (int) (((dataPoint.getY() - 0.5) - hours) * 60);

                String text = minutes == 0 ?
                        hours + "h" :
                        hours + "h" + minutes + "m";

                canvas.drawText(text, x, y, paint);
            }
        });
    }

    private void setWakeupTimePointsSeries(PointsGraphSeries<DataPoint> wakeupTimePointsSeries, int pointsColor) {
        this.wakeupTimePointsSeries = wakeupTimePointsSeries;

        this.wakeupTimePointsSeries.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setColor(pointsColor);
                paint.setTextSize(38);

                int hours = (int)(dataPoint.getY() - 0.5);
                int minutes = (int) (((dataPoint.getY() - 0.5) - hours) * 60);

                String delimiter = minutes < 10 ? ":0" : ":";

                //TODO figure out if it's AM or PM
                String text = hours + delimiter + minutes + "AM";

                canvas.drawText(text, x, y, paint);
            }
        });
    }

    private void setBedtimePointsSeries(PointsGraphSeries<DataPoint> bedtimePointsSeries, int pointsColor) {
        this.bedtimePointsSeries = bedtimePointsSeries;

        this.bedtimePointsSeries.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setColor(pointsColor);
                paint.setTextSize(38);

                int hours = (int)(dataPoint.getY() - 0.5);
                int minutes = (int) (((dataPoint.getY() - 0.5) - hours) * 60);

                String delimiter = minutes < 10 ? ":0" : ":";

                //TODO figure out if it's AM or PM
                String text = hours + delimiter + minutes + "PM";

                canvas.drawText(text, x, y, paint);
            }
        });
    }

    public void setPointsSeries(String fieldName, PointsGraphSeries<DataPoint> pointsSeries, int pointsColor) {
        switch (fieldName) {
            case "Wake-up time":
                this.setWakeupTimePointsSeries(pointsSeries, pointsColor);
                break;

            case "Bedtime":
                this.setBedtimePointsSeries(pointsSeries, pointsColor);
                break;

            //"Sleep duration"
            default:
                this.setSleepDurationPointsSeries(pointsSeries, pointsColor);
                break;
        }
    }

    private void setSleepDurationGoalMinLine(LineGraphSeries<DataPoint> sleepDurationGoalMinLine, int lineColor) {
        this.sleepDurationGoalMinLine = sleepDurationGoalMinLine;

        styleGoalLine(this.sleepDurationGoalMinLine, lineColor);
    }

    private void setWakeupTimeGoalMinLine(LineGraphSeries<DataPoint> wakeupTimeGoalMinLine, int lineColor) {
        this.wakeupTimeGoalMinLine = wakeupTimeGoalMinLine;

        styleGoalLine(this.wakeupTimeGoalMinLine, lineColor);
    }

    private void setBedtimeGoalMinLine(LineGraphSeries<DataPoint> bedtimeGoalMinLine, int lineColor) {
        this.bedtimeGoalMinLine = bedtimeGoalMinLine;

        styleGoalLine(this.bedtimeGoalMinLine, lineColor);
    }

    public void setGoalMinLine(String goalName, LineGraphSeries<DataPoint> goalMinLine, int lineColor) {
        switch (goalName) {
            case "Wake-up time":
                this.setWakeupTimeGoalMinLine(goalMinLine, lineColor);
                break;

            case "Bedtime":
                this.setBedtimeGoalMinLine(goalMinLine, lineColor);
                break;

            //"Sleep duration"
            default:
                this.setSleepDurationGoalMinLine(goalMinLine, lineColor);
                break;
        }
    }

    private void setSleepDurationGoalMaxLine(LineGraphSeries<DataPoint> sleepDurationGoalMaxLine, int lineColor) {
        this.sleepDurationGoalMaxLine = sleepDurationGoalMaxLine;

        styleGoalLine(this.sleepDurationGoalMaxLine, lineColor);
    }

    private void setWakeupTimeGoalMaxLine(LineGraphSeries<DataPoint> wakeupTimeGoalMaxLine, int lineColor) {
        this.wakeupTimeGoalMaxLine = wakeupTimeGoalMaxLine;

        styleGoalLine(this.wakeupTimeGoalMaxLine, lineColor);
    }

    private void setBedtimeGoalMaxLine(LineGraphSeries<DataPoint> bedtimeGoalMaxLine, int lineColor) {
        this.bedtimeGoalMaxLine = bedtimeGoalMaxLine;

        styleGoalLine(this.bedtimeGoalMaxLine, lineColor);
    }

    public void setGoalMaxLine(String goalName, LineGraphSeries<DataPoint> goalMaxLine, int lineColor) {
        switch (goalName) {
            case "Wake-up time":
                this.setWakeupTimeGoalMaxLine(goalMaxLine, lineColor);
                break;

            case "Bedtime":
                this.setBedtimeGoalMaxLine(goalMaxLine, lineColor);
                break;

            //"Sleep duration"
            default:
                this.setSleepDurationGoalMaxLine(goalMaxLine, lineColor);
                break;
        }
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

    public int getGraphWeekLength() {
        return this.graphWeekLength;
    }

    public int getGraphMonthLength() {
        return this.graphMonthLength;
    }

    public int getGraphYearLength() {
        return this.graphYearLength;
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

    public LineGraphSeries<DataPoint> getLineSeries(String fieldName) {
        switch (fieldName) {
            case "Wake-up time":
                return this.wakeupTimeLineSeries;

            case "Bedtime":
                return this.bedtimeLineSeries;

            //"Sleep duration"
            default:
                return this.sleepDurationLineSeries;
        }
    }

    public PointsGraphSeries<DataPoint> getPointsSeries(String fieldName) {
        switch (fieldName) {
            case "Wake-up time":
                return this.wakeupTimePointsSeries;

            case "Bedtime":
                return this.bedtimePointsSeries;

            //"Sleep duration"
            default:
                return this.sleepDurationPointsSeries;
        }
    }

    public LineGraphSeries<DataPoint> getGoalMinLine(String goalName) {
        switch (goalName) {
            case "Wake-up time":
                return this.wakeupTimeGoalMinLine;

            case "Bedtime":
                return this.bedtimeGoalMinLine;

            //"Sleep duration"
            default:
                return this.sleepDurationGoalMinLine;
        }
    }

    public LineGraphSeries<DataPoint> getGoalMaxLine(String goalName) {
        switch (goalName) {
            case "Wake-up time":
                return this.wakeupTimeGoalMaxLine;

            case "Bedtime":
                return this.bedtimeGoalMaxLine;

            //"Sleep duration"
            default:
                return this.sleepDurationGoalMaxLine;
        }
    }
}
