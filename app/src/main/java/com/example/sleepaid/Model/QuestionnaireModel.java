package com.example.sleepaid.Model;

import com.example.sleepaid.Database.Answer.Answer;
import com.example.sleepaid.Database.Option.Option;
import com.example.sleepaid.Database.Question.Question;

import java.util.List;


public class QuestionnaireModel {
    private int questionnaireId;

    private List<Question> questions;
    private List<Option> options;
    private List<Answer> answers;

    public QuestionnaireModel(int questionnaireId) {
        this.questionnaireId = questionnaireId;
    }

    public void update(List<Question> questions,
                       List<Option> options,
                       List<Answer> answers) {
        this.questions = questions;
        this.options = options;
        this.answers = answers;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void setOptions(List<Option> options) {
        this.options = options;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public int getQuestionnaireId() {
        return this.questionnaireId;
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
}
