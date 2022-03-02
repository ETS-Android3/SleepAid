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

    private List<GraphSeriesModel> graphSeries = new ArrayList<>();
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

    public void setSeries(String dataType,
                          List<Double> data,
                          String periodStart,
                          String periodEnd,
                          int seriesLength,
                          int backgroundColor,
                          int lineColor,
                          int pointsColor) {
        Optional<GraphSeriesModel> graphSeries = this.graphSeries.stream()
                .filter(g -> g.getDataType().equals(dataType) &&
                        g.getPeriod().equals(periodStart + "-" + periodEnd))
                .findFirst();

        if (graphSeries.isPresent()) {
            graphSeries.get().update(
                    data,
                    periodStart,
                    periodEnd,
                    seriesLength,
                    backgroundColor,
                    lineColor,
                    pointsColor
            );
        } else {
            this.graphSeries.add(new GraphSeriesModel(
                    dataType,
                    data,
                    periodStart,
                    periodEnd,
                    seriesLength,
                    backgroundColor,
                    lineColor,
                    pointsColor
            ));
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
                    this.getGraphYearLength(),
                    pointColor
            );
        } else {
            this.goals.add(new GoalModel(
                    goalName,
                    goalValueMin,
                    goalValueMax,
                    lineColor,
                    this.getGraphYearLength(),
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

    public List<Double> getSeriesData(String dataType,
                                      String periodStart,
                                      String periodEnd) {
        Optional<GraphSeriesModel> graphSeries = this.graphSeries.stream()
                .filter(g -> g.getDataType().equals(dataType) &&
                        g.getPeriod().equals(periodStart + "-" + periodEnd))
                .findFirst();

        if (graphSeries.isPresent()) {
            return graphSeries.get().getData();
        }

        return null;
    }

    public LineGraphSeries<DataPoint> getLineSeries(String dataType,
                                                    String periodStart,
                                                    String periodEnd) {
        Optional<GraphSeriesModel> graphSeries = this.graphSeries.stream()
                .filter(g -> g.getDataType().equals(dataType) &&
                        g.getPeriod().equals(periodStart + "-" + periodEnd))
                .findFirst();

        if (graphSeries.isPresent()) {
            return graphSeries.get().getLineSeries();
        }

        return null;
    }

    public PointsGraphSeries<DataPoint> getPointsSeries(String dataType,
                                                        String periodStart,
                                                        String periodEnd) {
        Optional<GraphSeriesModel> graphSeries = this.graphSeries.stream()
                .filter(g -> g.getDataType().equals(dataType) &&
                        g.getPeriod().equals(periodStart + "-" + periodEnd))
                .findFirst();

        if (graphSeries.isPresent()) {
            return graphSeries.get().getPointsSeries();
        }

        return null;
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
