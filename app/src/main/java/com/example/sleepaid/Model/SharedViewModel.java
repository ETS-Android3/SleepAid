package com.example.sleepaid.Model;

import android.annotation.SuppressLint;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressLint("NewApi")
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

    private List<GoalModel> goals = new ArrayList<>();

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

    public void setGoal(String goalName,
                             String goalValueMin,
                             String goalValueMax,
                             int lineColor,
                             int pointColor) {
        Optional<GoalModel> goal = this.goals.stream()
                .filter(g -> g.getGoalName().equals(goalName))
                .findFirst();

        if (goal.isPresent()) {
            goal.get().update(
                    goalValueMin,
                    goalValueMax,
                    lineColor,
                    this.getGraphPeriodLength(),
                    pointColor
            );
        } else {
            this.goals.add(new GoalModel(
                    goalName,
                    goalValueMin,
                    goalValueMax,
                    lineColor,
                    this.getGraphPeriodLength(),
                    pointColor
            ));
        }
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

    public String getGoalMin(String goalName) {
        Optional<GoalModel> goal = this.goals.stream()
                .filter(g -> g.getGoalName().equals(goalName))
                .findFirst();

        if (goal.isPresent()) {
            return goal.get().getGoalMin();
        }

        return null;
    }

    public LineGraphSeries<DataPoint> getGoalMinLine(String goalName) {
        Optional<GoalModel> goal = this.goals.stream()
                .filter(g -> g.getGoalName().equals(goalName))
                .findFirst();

        if (goal.isPresent()) {
            return goal.get().getGoalMinLine();
        }

        return null;
    }

    public PointsGraphSeries<DataPoint> getGoalMinPoint(String goalName) {
        Optional<GoalModel> goal = this.goals.stream()
                .filter(g -> g.getGoalName().equals(goalName))
                .findFirst();

        if (goal.isPresent()) {
            return goal.get().getGoalMinPoint();
        }

        return null;
    }

    public String getGoalMax(String goalName) {
        Optional<GoalModel> goal = this.goals.stream()
                .filter(g -> g.getGoalName().equals(goalName))
                .findFirst();

        if (goal.isPresent()) {
            return goal.get().getGoalMax();
        }

        return null;
    }

    public LineGraphSeries<DataPoint> getGoalMaxLine(String goalName) {
        Optional<GoalModel> goal = this.goals.stream()
                .filter(g -> g.getGoalName().equals(goalName))
                .findFirst();

        if (goal.isPresent()) {
            return goal.get().getGoalMaxLine();
        }

        return null;
    }

    public PointsGraphSeries<DataPoint> getGoalMaxPoint(String goalName) {
        Optional<GoalModel> goal = this.goals.stream()
                .filter(g -> g.getGoalName().equals(goalName))
                .findFirst();

        if (goal.isPresent()) {
            return goal.get().getGoalMaxPoint();
        }

        return null;
    }
}
