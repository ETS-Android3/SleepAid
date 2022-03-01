package com.example.sleepaid;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.provider.ContactsContract;

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

    private int sleepDurationGoalMin;
    private int sleepDurationGoalMax;
    private LineGraphSeries<DataPoint> sleepDurationGoalMinLine;
    private LineGraphSeries<DataPoint> sleepDurationGoalMaxLine;
    private PointsGraphSeries<DataPoint> sleepDurationGoalMinPoint;
    private PointsGraphSeries<DataPoint> sleepDurationGoalMaxPoint;

    private LineGraphSeries<DataPoint> wakeupTimeLineSeries;
    private PointsGraphSeries<DataPoint> wakeupTimePointsSeries;

    private int wakeupTimeGoalMin;
    private int wakeupTimeGoalMax;
    private LineGraphSeries<DataPoint> wakeupTimeGoalMinLine;
    private LineGraphSeries<DataPoint> wakeupTimeGoalMaxLine;
    private PointsGraphSeries<DataPoint> wakeupTimeGoalMinPoint;
    private PointsGraphSeries<DataPoint> wakeupTimeGoalMaxPoint;

    private LineGraphSeries<DataPoint> bedtimeLineSeries;
    private PointsGraphSeries<DataPoint> bedtimePointsSeries;

    private int bedtimeGoalMin;
    private int bedtimeGoalMax;
    private LineGraphSeries<DataPoint> bedtimeGoalMinLine;
    private LineGraphSeries<DataPoint> bedtimeGoalMaxLine;
    private PointsGraphSeries<DataPoint> bedtimeGoalMinPoint;
    private PointsGraphSeries<DataPoint> bedtimeGoalMaxPoint;

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

    public void setLineSeries(String fieldName, LineGraphSeries<DataPoint> lineSeries, int backgroundColor, int lineColor) {
        switch (fieldName) {
            case "Wake-up time":
                this.wakeupTimeLineSeries = lineSeries;
                break;

            case "Bedtime":
                this.bedtimeLineSeries = lineSeries;
                break;

            //"Sleep duration"
            default:
                this.sleepDurationLineSeries = lineSeries;
                break;
        }

        this.styleLineSeries(lineSeries, backgroundColor, lineColor);
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

    public void setGoalMin(String goalName, int goalValue, int lineColor, int pointColor) {
        switch (goalName) {
            case "Wake-up time":
                this.wakeupTimeGoalMin = goalValue;
                break;

            case "Bedtime":
                this.bedtimeGoalMin = goalValue;
                break;

            //"Sleep duration"
            default:
                this.sleepDurationGoalMin = goalValue;
                break;
        }

        LineGraphSeries<DataPoint> goalMinLine = new LineGraphSeries<>();

        int maxGraphSize = this.getGraphPeriodLength();

        for (int i = 0; i < maxGraphSize; i++) {
            goalMinLine.appendData(
                    new DataPoint(i, goalValue),
                    true,
                    maxGraphSize
            );
        }

        this.setGoalMinLine(goalName, goalMinLine, lineColor);

        PointsGraphSeries<DataPoint> goalMinPoint = new PointsGraphSeries<>();
        goalMinPoint.appendData(
                new DataPoint(0, goalValue + 0.25),
                true,
                1);

        this.setGoalMinPointsSeries(goalName, goalMinPoint, pointColor);
    }

    private void setGoalMinLine(String goalName, LineGraphSeries<DataPoint> goalMinLine, int lineColor) {
        switch (goalName) {
            case "Wake-up time":
                this.wakeupTimeGoalMinLine = goalMinLine;
                break;

            case "Bedtime":
                this.bedtimeGoalMinLine = goalMinLine;
                break;

            //"Sleep duration"
            default:
                this.sleepDurationGoalMinLine = goalMinLine;
                break;
        }

        this.styleGoalLine(goalMinLine, lineColor);
    }

    public void setGoalMinPointsSeries(String goalName, PointsGraphSeries<DataPoint> pointsSeries, int pointColor) {
        switch (goalName) {
            case "Wake-up time":
                this.wakeupTimeGoalMinPoint = pointsSeries;
                this.styleWakeupTimeGoalPoint(pointsSeries, pointColor);
                break;

            case "Bedtime":
                this.bedtimeGoalMinPoint = pointsSeries;
                this.styleBedtimeGoalPoint(pointsSeries, pointColor);
                break;

            //"Sleep duration"
            default:
                this.sleepDurationGoalMinPoint = pointsSeries;
                this.styleSleepDurationGoalPoint(pointsSeries, pointColor);
                break;
        }
    }

    public void setGoalMax(String goalName, int goalValue, int lineColor, int pointColor) {
        switch (goalName) {
            case "Wake-up time":
                this.wakeupTimeGoalMax = goalValue;
                break;

            case "Bedtime":
                this.bedtimeGoalMax = goalValue;
                break;

            //"Sleep duration"
            default:
                this.sleepDurationGoalMax = goalValue;
                break;
        }

        LineGraphSeries<DataPoint> goalMaxLine = new LineGraphSeries<>();

        int maxGraphSize = this.getGraphPeriodLength();

        for (int i = 0; i < maxGraphSize; i++) {
            goalMaxLine.appendData(
                    new DataPoint(i, goalValue),
                    true,
                    maxGraphSize
            );
        }

        this.setGoalMaxLine(goalName, goalMaxLine, lineColor);

        PointsGraphSeries<DataPoint> goalMaxPoint = new PointsGraphSeries<>();
        goalMaxPoint.appendData(
                new DataPoint(0, goalValue + 0.25),
                true,
                1);

        this.setGoalMaxPointsSeries(goalName, goalMaxPoint, pointColor);
    }

    private void setGoalMaxLine(String goalName, LineGraphSeries<DataPoint> goalMaxLine, int lineColor) {
        switch (goalName) {
            case "Wake-up time":
                this.wakeupTimeGoalMaxLine = goalMaxLine;
                break;

            case "Bedtime":
                this.bedtimeGoalMaxLine = goalMaxLine;
                break;

            //"Sleep duration"
            default:
                this.sleepDurationGoalMaxLine = goalMaxLine;
                break;
        }

        this.styleGoalLine(goalMaxLine, lineColor);
    }

    public void setGoalMaxPointsSeries(String goalName, PointsGraphSeries<DataPoint> pointsSeries, int pointColor) {
        switch (goalName) {
            case "Wake-up time":
                this.wakeupTimeGoalMaxPoint = pointsSeries;
                this.styleWakeupTimeGoalPoint(pointsSeries, pointColor);
                break;

            case "Bedtime":
                this.bedtimeGoalMaxPoint = pointsSeries;
                this.styleBedtimeGoalPoint(pointsSeries, pointColor);
                break;

            //"Sleep duration"
            default:
                this.sleepDurationGoalMaxPoint = pointsSeries;
                this.styleSleepDurationGoalPoint(pointsSeries, pointColor);
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

    private void styleSleepDurationGoalPoint(PointsGraphSeries<DataPoint> sleepDurationGoalPoint, int pointColor) {
        sleepDurationGoalPoint.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setColor(pointColor);
                paint.setTextSize(38);

                int hours = (int)(dataPoint.getY() - 0.25);
                int minutes = (int) (((dataPoint.getY() - 0.25) - hours) * 60);

                String text = minutes == 0 ?
                        hours + "h" :
                        hours + "h" + minutes + "m";

                canvas.drawText(text, x, y, paint);
            }
        });
    }

    private void styleWakeupTimeGoalPoint(PointsGraphSeries<DataPoint> wakeupTimeGoalPoint, int pointColor) {
        wakeupTimeGoalPoint.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setColor(pointColor);
                paint.setTextSize(38);

                int hours = (int)(dataPoint.getY() - 0.25);
                int minutes = (int) (((dataPoint.getY() - 0.25) - hours) * 60);

                String delimiter = minutes < 10 ? ":0" : ":";

                //TODO figure out if it's AM or PM
                String text = hours + delimiter + minutes + "AM";

                canvas.drawText(text, x, y, paint);
            }
        });
    }

    private void styleBedtimeGoalPoint(PointsGraphSeries<DataPoint> bedtimeGoalPoint, int pointColor) {
        bedtimeGoalPoint.setCustomShape(new PointsGraphSeries.CustomShape() {
            @Override
            public void draw(Canvas canvas, Paint paint, float x, float y, DataPointInterface dataPoint) {
                paint.setColor(pointColor);
                paint.setTextSize(38);

                int hours = (int)(dataPoint.getY() - 0.25);
                int minutes = (int) (((dataPoint.getY() - 0.25) - hours) * 60);

                String delimiter = minutes < 10 ? ":0" : ":";

                //TODO figure out if it's AM or PM
                String text = hours + delimiter + minutes + "PM";

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

    public int getGoalMin(String goalName) {
        switch (goalName) {
            case "Wake-up time":
                return this.wakeupTimeGoalMin;

            case "Bedtime":
                return this.bedtimeGoalMin;

            //"Sleep duration"
            default:
                return this.sleepDurationGoalMin;
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

    public PointsGraphSeries<DataPoint> getGoalMinPoint(String goalName) {
        switch (goalName) {
            case "Wake-up time":
                return this.wakeupTimeGoalMinPoint;

            case "Bedtime":
                return this.bedtimeGoalMinPoint;

            //"Sleep duration"
            default:
                return this.sleepDurationGoalMinPoint;
        }
    }

    public int getGoalMax(String goalName) {
        switch (goalName) {
            case "Wake-up time":
                return this.wakeupTimeGoalMax;

            case "Bedtime":
                return this.bedtimeGoalMax;

            //"Sleep duration"
            default:
                return this.sleepDurationGoalMax;
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

    public PointsGraphSeries<DataPoint> getGoalMaxPoint(String goalName) {
        switch (goalName) {
            case "Wake-up time":
                return this.wakeupTimeGoalMaxPoint;

            case "Bedtime":
                return this.bedtimeGoalMaxPoint;

            //"Sleep duration"
            default:
                return this.sleepDurationGoalMaxPoint;
        }
    }
}
