package com.example.sleepaid.Model;

import androidx.lifecycle.ViewModel;

import com.example.sleepaid.Database.Alarm.Alarm;
import com.example.sleepaid.Database.Answer.Answer;
import com.example.sleepaid.Database.Option.Option;
import com.example.sleepaid.Database.Question.Question;
import com.example.sleepaid.Database.Questionnaire.Questionnaire;
import com.example.sleepaid.Database.SleepData.SleepData;
import com.example.sleepaid.Handler.DataHandler;
import com.google.common.collect.ImmutableMap;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class SharedViewModel extends ViewModel {
    private String userId;

    private final int[] questionnaireIds = new int[]{1, 2, 3, 6};
    private final HashMap<Integer, Integer> firstQuestionsIds = new HashMap<>(ImmutableMap.of(
            1, 1,
            2, 7,
            3, 9,
            6, 22
    ));

    private List<QuestionnaireModel> questionnaires = new ArrayList<>();
    private List<SleepDiaryModel> sleepDiaries = new ArrayList<>();

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

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setQuestionnaire(int questionnaireId, Questionnaire questionnaire) {
        QuestionnaireModel questionnaireModel = this.getQuestionnaireModel(questionnaireId);

        if (questionnaireModel != null) {
            questionnaireModel.setQuestionnaire(questionnaire);
        } else {
            this.questionnaires.add(new QuestionnaireModel(questionnaireId));
            this.questionnaires.get(this.questionnaires.size() - 1).setQuestionnaire(questionnaire);
        }
    }

    public void setQuestionnaireQuestions(int questionnaireId, List<Question> questions) {
        QuestionnaireModel questionnaire = this.getQuestionnaireModel(questionnaireId);

        if (questionnaire != null) {
            questionnaire.setQuestions(questions);
        } else {
            this.questionnaires.add(new QuestionnaireModel(questionnaireId));
            this.questionnaires.get(this.questionnaires.size() - 1).setQuestions(questions);
        }
    }

    public void setQuestionnaireOptions(int questionnaireId, List<Option> options) {
        QuestionnaireModel questionnaire = this.getQuestionnaireModel(questionnaireId);

        if (questionnaire != null) {
            questionnaire.setOptions(options);
        } else {
            this.questionnaires.add(new QuestionnaireModel(questionnaireId));
            this.questionnaires.get(this.questionnaires.size() - 1).setOptions(options);
        }
    }

    public void setQuestionnaireAnswers(List<Answer> answers) {
        for (int i : this.questionnaireIds) {
            List<Integer> questionIdsForQuestionnaire = this.getQuestionnaireQuestions(i).stream()
                    .map(Question::getId)
                    .collect(Collectors.toList());

            List<Answer> answersForQuestionnaire = answers.stream()
                    .filter(a -> questionIdsForQuestionnaire.contains(a.getQuestionId()))
                    .collect(Collectors.toList());

            QuestionnaireModel questionnaire = this.getQuestionnaireModel(i);

            if (questionnaire != null) {
                questionnaire.setAnswers(answersForQuestionnaire);
            } else {
                this.questionnaires.add(new QuestionnaireModel(i));
                this.questionnaires.get(this.questionnaires.size() - 1).setAnswers(answersForQuestionnaire);
            }
        }
    }

    public void setQuestionnaireAnswers(int questionnaireId, List<Answer> answers) {
        QuestionnaireModel questionnaire = this.getQuestionnaireModel(questionnaireId);

        if (questionnaire != null) {
            questionnaire.setAnswers(answers);
        } else {
            this.questionnaires.add(new QuestionnaireModel(questionnaireId));
            this.questionnaires.get(this.questionnaires.size() - 1).setAnswers(answers);
        }
    }

    public void setSleepDiaryQuestions(int questionnaireId, List<Question> questions) {
        SleepDiaryModel sleepDiary = this.getSleepDiaryModel(questionnaireId);

        if (sleepDiary != null) {
            sleepDiary.setQuestions(questions);
        } else {
            this.sleepDiaries.add(new SleepDiaryModel(questionnaireId));
            this.sleepDiaries.get(this.sleepDiaries.size() - 1).setQuestions(questions);
        }
    }

    public void setSleepDiaryHasOptions(int questionnaireId, boolean hasOptions) {
        SleepDiaryModel sleepDiary = this.getSleepDiaryModel(questionnaireId);

        if (sleepDiary != null) {
            sleepDiary.setHasOptions(hasOptions);
        } else {
            this.sleepDiaries.add(new SleepDiaryModel(questionnaireId));
            this.sleepDiaries.get(this.sleepDiaries.size() - 1).setHasOptions(hasOptions);
        }
    }

    public void setSleepDiaryOptions(int questionnaireId, List<Option> options) {
        SleepDiaryModel sleepDiary = this.getSleepDiaryModel(questionnaireId);

        if (sleepDiary != null) {
            sleepDiary.setOptions(options);
        } else {
            this.sleepDiaries.add(new SleepDiaryModel(questionnaireId));
            this.sleepDiaries.get(this.sleepDiaries.size() - 1).setOptions(options);
        }
    }

    public void setSleepDiaryAnswers(int questionnaireId, List<Answer> answers) {
        SleepDiaryModel sleepDiary = this.getSleepDiaryModel(questionnaireId);

        if (sleepDiary != null) {
            sleepDiary.setAnswers(answers);
        } else {
            this.sleepDiaries.add(new SleepDiaryModel(questionnaireId));
            this.sleepDiaries.get(this.sleepDiaries.size() - 1).setAnswers(answers);
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

    public void setLineSeries(String dataType, LineGraphSeries lineGraphSeries) {
        List<GraphSeriesModel> graphSeries = this.getSeriesModel(dataType);

        if (!graphSeries.isEmpty()) {
            for (GraphSeriesModel m : graphSeries) {
                m.setLineSeries(lineGraphSeries);
            }
        }
    }

    public void setSeries(String dataType,
                          List<Float> data,
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

    public void setGoalModel(String goalName, GoalModel model) {
        GoalModel goal = this.getGoalModel(goalName);

        if (goal != null) {
            if (model == null) {
                this.goals.remove(this.goals.indexOf(goal));
            } else {
                this.goals.set(this.goals.indexOf(goal), model);
            }
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

    public void setAlarmListModel(int alarmType, AlarmListModel model) {
        AlarmListModel alarm = this.getAlarmListModel(alarmType);

        if (alarm != null) {
            if (model == null) {
                this.alarms.remove(this.alarms.indexOf(alarm));
            } else {
                this.alarms.set(this.alarms.indexOf(alarm), model);
            }
        }
    }

    public String getUserId() {
        return this.userId;
    }

    public int[] getQuestionnaireIds() {
        return this.questionnaireIds;
    }

    public HashMap<Integer, Integer> getFirstQuestionsIds() {
        return this.firstQuestionsIds;
    }

    public QuestionnaireModel getQuestionnaireModel(int questionnaireId) {
        Optional<QuestionnaireModel> questionnaire = this.questionnaires.stream()
                .filter(q -> q.getQuestionnaireId() == questionnaireId)
                .findFirst();

        return questionnaire.orElse(null);

    }

    public List<Questionnaire> getQuestionnaires() {
        List<Questionnaire> questionnaires = new ArrayList<>();

        if (!this.questionnaires.isEmpty()) {
            for (QuestionnaireModel q : this.questionnaires) {
                if (q.getQuestionnaire() != null) {
                    questionnaires.add(q.getQuestionnaire());
                }
            }
        }

        return questionnaires.isEmpty() ? null : questionnaires;
    }

    public Questionnaire getQuestionnaire(int questionnaireId) {
        Optional<QuestionnaireModel> questionnaire = this.questionnaires.stream()
                .filter(q -> q.getQuestionnaireId() == questionnaireId)
                .findFirst();

        return questionnaire.map(QuestionnaireModel::getQuestionnaire).orElse(null);

    }

    public List<Question> getQuestionnaireQuestions() {
        List<Question> questions = new ArrayList<>();

        if (!this.questionnaires.isEmpty()) {
            for (QuestionnaireModel q : this.questionnaires) {
                if (q.getQuestions() != null) {
                    questions.addAll(q.getQuestions());
                }
            }
        }

        return questions.isEmpty() ? null : questions;
    }

    public List<Question> getQuestionnaireQuestions(int questionnaireId) {
        Optional<QuestionnaireModel> questionnaire = this.questionnaires.stream()
                .filter(q -> q.getQuestionnaireId() == questionnaireId)
                .findFirst();

        return questionnaire.map(QuestionnaireModel::getQuestions).orElse(null);

    }

    public List<Option> getQuestionnaireOptions() {
        List<Option> options = new ArrayList<>();

        for(QuestionnaireModel q : this.questionnaires){
            options.addAll(q.getOptions());
        }

        return options;
    }

    public List<Answer> getQuestionnaireAnswers() {
        List<Answer> answers = new ArrayList<>();

        if (!this.questionnaires.isEmpty()) {
            for (QuestionnaireModel q : this.questionnaires) {
                if (q.getAnswers() != null) {
                    answers.addAll(q.getAnswers());
                }
            }
        }

        return answers.isEmpty() ? null : answers;
    }

    public List<Answer> getQuestionnaireAnswers(int questionnaireId) {
        Optional<QuestionnaireModel> questionnaire = this.questionnaires.stream()
                .filter(q -> q.getQuestionnaireId() == questionnaireId)
                .findFirst();

        return questionnaire.map(QuestionnaireModel::getAnswers).orElse(null);

    }

    public SleepDiaryModel getSleepDiaryModel(int questionnaireId) {
        Optional<SleepDiaryModel> sleepDiary = this.sleepDiaries.stream()
                .filter(s -> s.getQuestionnaireId() == questionnaireId)
                .findFirst();

        return sleepDiary.orElse(null);

    }

    public List<Question> getSleepDiaryQuestions(int questionnaireId) {
        Optional<SleepDiaryModel> sleepDiary = this.sleepDiaries.stream()
                .filter(s -> s.getQuestionnaireId() == questionnaireId)
                .findFirst();

        return sleepDiary.map(SleepDiaryModel::getQuestions).orElse(null);

    }

    public boolean hasOptions(int questionnaireId) {
        Optional<SleepDiaryModel> sleepDiary = this.sleepDiaries.stream()
                .filter(s -> s.getQuestionnaireId() == questionnaireId)
                .findFirst();

        return sleepDiary.map(SleepDiaryModel::hasOptions).orElse(false);

    }

    public List<Option> getSleepDiaryOptions(int questionnaireId) {
        Optional<SleepDiaryModel> sleepDiary = this.sleepDiaries.stream()
                .filter(s -> s.getQuestionnaireId() == questionnaireId)
                .findFirst();

        return sleepDiary.map(SleepDiaryModel::getOptions).orElse(null);

    }

    public List<Answer> getSleepDiaryAnswers(int questionnaireId) {
        Optional<SleepDiaryModel> sleepDiary = this.sleepDiaries.stream()
                .filter(s -> s.getQuestionnaireId() == questionnaireId)
                .findFirst();

        return sleepDiary.map(SleepDiaryModel::getAnswers).orElse(null);

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

        return sleepData.orElse(null);

    }

    public List<SleepData> getTodaySleepData() {
        return this.todaySleepData;
    }

    private List<GraphSeriesModel> getSeriesModel(String dataType) {
        return this.graphSeries.stream()
                .filter(g -> g.getDataType().equals(dataType))
                .collect(Collectors.toList());
    }

    public GraphSeriesModel getSeriesModel(String dataType,
                                            String periodStart,
                                            String periodEnd) {
        Optional<GraphSeriesModel> graphSeries = this.graphSeries.stream()
                .filter(g -> g.getDataType().equals(dataType) &&
                        g.getPeriod().equals(periodStart + "-" + periodEnd))
                .findFirst();

        return graphSeries.orElse(null);

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

        return goal.orElse(null);

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

    public float getMaxY(String dataType,
                          String periodStart,
                          String periodEnd) {
        GraphSeriesModel graphSeriesModel = this.getSeriesModel(dataType, periodStart, periodEnd);
        List<Float> data = graphSeriesModel.getData();

        float maxValue = Collections.max(data);
        float goalValue = 0.f;

        GoalModel goalModel = this.getGoalModel(dataType);

        if (goalModel != null) {
            goalValue = DataHandler.getFloatFromTime(goalModel.getGoalMax());
            goalValue += goalModel.getTranslation(1);
        }

        return Math.max(goalValue, maxValue) + 1;
    }

    public AlarmListModel getAlarmListModel(int alarmType) {
        Optional<AlarmListModel> alarm = this.alarms.stream()
                .filter(a -> a.getAlarmType() == alarmType)
                .findFirst();

        return alarm.orElse(null);
    }

    public List<Alarm> getAlarmList(int alarmType) {
        AlarmListModel alarmListModel = this.getAlarmListModel(alarmType);

        if (alarmListModel != null) {
            return alarmListModel.getAlarmList();
        }

        return null;
    }
}
