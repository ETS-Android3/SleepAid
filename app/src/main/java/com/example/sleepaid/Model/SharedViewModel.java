package com.example.sleepaid.Model;

import androidx.lifecycle.ViewModel;

import com.example.sleepaid.Database.Alarm.Alarm;
import com.example.sleepaid.Database.Answer.Answer;
import com.example.sleepaid.Database.Option.Option;
import com.example.sleepaid.Database.Question.Question;
import com.example.sleepaid.Database.SleepData.SleepData;
import com.example.sleepaid.Handler.DataHandler;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


public class SharedViewModel extends ViewModel {
    private List<QuestionnaireModel> questionnaires = new ArrayList<>();

    private int currentQuestionId;

    private String graphViewType;

    private int graphWeekLength;
    private int graphMonthLength;
    private int graphYearLength;

    private int alarmViewType;
    private Alarm selectedAlarm;
    private Alarm selectedConfiguration;

    private List<SleepData> todaySleepData = new ArrayList<>();

    private List<GraphSeriesModel> graphSeries = new ArrayList<>();
    private List<GoalModel> goals = new ArrayList<>();
    private List<AlarmListModel> alarms = new ArrayList<>();

    public void setQuestionnaire(int questionnaireId) {
        QuestionnaireModel questionnaire = this.getQuestionnaireModel(questionnaireId);

        if (questionnaire == null) {
            this.questionnaires.add(new QuestionnaireModel(questionnaireId));
        }
    }

    public void setQuestions(int questionnaireId, List<Question> questions) {
        QuestionnaireModel questionnaire = this.getQuestionnaireModel(questionnaireId);

        if (questionnaire != null) {
            questionnaire.setQuestions(questions);
        } else {
            this.questionnaires.add(new QuestionnaireModel(questionnaireId));
            this.questionnaires.get(this.questionnaires.size() - 1).setQuestions(questions);
        }
    }

    public void setOptions(int questionnaireId, List<Option> options) {
        QuestionnaireModel questionnaire = this.getQuestionnaireModel(questionnaireId);

        if (questionnaire != null) {
            questionnaire.setOptions(options);
        } else {
            this.questionnaires.add(new QuestionnaireModel(questionnaireId));
            this.questionnaires.get(this.questionnaires.size() - 1).setOptions(options);
        }
    }

    public void setAnswers(int questionnaireId, List<Answer> answers) {
        QuestionnaireModel questionnaire = this.getQuestionnaireModel(questionnaireId);

        if (questionnaire != null) {
            questionnaire.setAnswers(answers);
        } else {
            this.questionnaires.add(new QuestionnaireModel(questionnaireId));
            this.questionnaires.get(this.questionnaires.size() - 1).setAnswers(answers);
        }
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

    public void setAlarmViewType(int alarmViewType) {
        this.alarmViewType = alarmViewType;
    }

    public void setSelectedAlarm(Alarm selectedAlarm) {
        this.selectedAlarm = selectedAlarm;
    }

    public void setSelectedConfiguration(Alarm selectedConfiguration) {
        this.selectedConfiguration = selectedConfiguration;
    }

    public void setTodaySleepData(List<SleepData> sleepData) {
        for (SleepData s : sleepData) {
            SleepData currentSleepData = this.getTodaySleepData(s.getField());

            if (currentSleepData != null) {
                this.todaySleepData.set(this.todaySleepData.indexOf(currentSleepData), s);
            } else {
                this.todaySleepData.add(s);
            }
        }
    }

    public void setSeries(String dataType,
                          List<Double> data,
                          String periodStart,
                          String periodEnd,
                          int backgroundColor,
                          int lineColor,
                          int pointsColor) {
        GraphSeriesModel graphSeries = this.getSeriesModel(dataType, periodStart, periodEnd);

        if (graphSeries != null) {
            graphSeries.update(
                    data,
                    periodStart,
                    periodEnd,
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
        GoalModel goal = this.getGoalModel(goalName);

        if (goal != null) {
            goal.update(
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

    public void setAlarms(int alarmType, List<Alarm> alarmList) {
        AlarmListModel alarm = this.getAlarmListModel(alarmType);

        if (alarm != null) {
            alarm.update(alarmList);
        } else {
            this.alarms.add(new AlarmListModel(
                    alarmType,
                    alarmList
            ));
        }
    }

    public QuestionnaireModel getQuestionnaireModel(int questionnaireId) {
        Optional<QuestionnaireModel> questionnaire = this.questionnaires.stream()
                .filter(q -> q.getQuestionnaireId() == questionnaireId)
                .findFirst();

        if (questionnaire.isPresent()) {
            return questionnaire.get();
        }

        return null;
    }

    public List<Question> getQuestions(int questionnaireId) {
        Optional<QuestionnaireModel> questionnaire = this.questionnaires.stream()
                .filter(q -> q.getQuestionnaireId() == questionnaireId)
                .findFirst();

        if (questionnaire.isPresent()) {
            return questionnaire.get().getQuestions();
        }

        return null;
    }

    public List<Option> getOptions(int questionnaireId) {
        Optional<QuestionnaireModel> questionnaire = this.questionnaires.stream()
                .filter(q -> q.getQuestionnaireId() == questionnaireId)
                .findFirst();

        if (questionnaire.isPresent()) {
            return questionnaire.get().getOptions();
        }

        return null;
    }

    public List<Answer> getAnswers(int questionnaireId) {
        Optional<QuestionnaireModel> questionnaire = this.questionnaires.stream()
                .filter(q -> q.getQuestionnaireId() == questionnaireId)
                .findFirst();

        if (questionnaire.isPresent()) {
            return questionnaire.get().getAnswers();
        }

        return null;
    }

    public int getCurrentQuestionId() {
        return this.currentQuestionId;
    }

    public String getGraphViewType() {
        return this.graphViewType;
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

    public int getAlarmViewType() {
        return this.alarmViewType;
    }

    public Alarm getSelectedAlarm() {
        return this.selectedAlarm;
    }

    public Alarm getSelectedConfiguration() {
        return this.selectedConfiguration;
    }

    public SleepData getTodaySleepData(String field) {
        Optional<SleepData> sleepData = this.todaySleepData.stream()
                .filter(s -> s.getField().equals(field))
                .findFirst();

        if (sleepData.isPresent()) {
            return sleepData.get();
        }

        return null;
    }

    public List<SleepData> getTodaySleepData() {
        return this.todaySleepData;
    }

    private GraphSeriesModel getSeriesModel(String dataType,
                                            String periodStart,
                                            String periodEnd) {
        Optional<GraphSeriesModel> graphSeries = this.graphSeries.stream()
                .filter(g -> g.getDataType().equals(dataType) &&
                        g.getPeriod().equals(periodStart + "-" + periodEnd))
                .findFirst();

        if (graphSeries.isPresent()) {
            return graphSeries.get();
        }

        return null;
    }

    public LineGraphSeries<DataPoint> getLineSeries(String dataType,
                                                    String periodStart,
                                                    String periodEnd) {
        GraphSeriesModel graphSeriesModel = this.getSeriesModel(dataType, periodStart, periodEnd);

        if (graphSeriesModel != null) {
            return graphSeriesModel.getLineSeries();
        }

        return null;
    }

    public PointsGraphSeries<DataPoint> getPointsSeries(String dataType,
                                                        String periodStart,
                                                        String periodEnd) {
        GraphSeriesModel graphSeriesModel = this.getSeriesModel(dataType, periodStart, periodEnd);

        if (graphSeriesModel != null) {
            return graphSeriesModel.getPointsSeries();
        }

        return null;
    }

    private GoalModel getGoalModel(String goalName) {
        Optional<GoalModel> goal = this.goals.stream()
                .filter(g -> g.getGoalName().equals(goalName))
                .findFirst();

        if (goal.isPresent()) {
            return goal.get();
        }

        return null;
    }

    public String getGoalMin(String goalName) {
        GoalModel goalModel = this.getGoalModel(goalName);

        if (goalModel != null) {
            return goalModel.getGoalMin();
        }

        return null;
    }

    public LineGraphSeries<DataPoint> getGoalMinLine(String goalName) {
        GoalModel goalModel = this.getGoalModel(goalName);

        if (goalModel != null) {
            return goalModel.getGoalMinLine();
        }

        return null;
    }

    public PointsGraphSeries<DataPoint> getGoalMinPoint(String goalName) {
        GoalModel goalModel = this.getGoalModel(goalName);

        if (goalModel != null) {
            return goalModel.getGoalMinPoint();
        }

        return null;
    }

    public String getGoalMax(String goalName) {
        GoalModel goalModel = this.getGoalModel(goalName);

        if (goalModel != null) {
            return goalModel.getGoalMax();
        }

        return null;
    }

    public LineGraphSeries<DataPoint> getGoalMaxLine(String goalName) {
        GoalModel goalModel = this.getGoalModel(goalName);

        if (goalModel != null) {
            return goalModel.getGoalMaxLine();
        }

        return null;
    }

    public PointsGraphSeries<DataPoint> getGoalMaxPoint(String goalName) {
        GoalModel goalModel = this.getGoalModel(goalName);

        if (goalModel != null) {
            return goalModel.getGoalMaxPoint();
        }

        return null;
    }

    public double getMaxY(String dataType,
                          String periodStart,
                          String periodEnd) {
        GraphSeriesModel graphSeriesModel = this.getSeriesModel(dataType, periodStart, periodEnd);
        List<Double> data = graphSeriesModel.getData();

        double maxValue = Collections.max(data);

        GoalModel goalModel = this.getGoalModel(dataType);

        double goalValue = DataHandler.getDoubleFromTime(goalModel.getGoalMax());
        goalValue += goalModel.getTranslation(1);

        return Math.max(goalValue, maxValue) + 1;
    }

    public AlarmListModel getAlarmListModel(int alarmType) {
        Optional<AlarmListModel> alarm = this.alarms.stream()
                .filter(a -> a.getAlarmType() == alarmType)
                .findFirst();

        if (alarm.isPresent()) {
            return alarm.get();
        }

        return null;
    }

    public List<Alarm> getAlarmList(int alarmType) {
        AlarmListModel alarmListModel = this.getAlarmListModel(alarmType);

        if (alarmListModel != null) {
            return alarmListModel.getAlarmList();
        }

        return null;
    }
}
