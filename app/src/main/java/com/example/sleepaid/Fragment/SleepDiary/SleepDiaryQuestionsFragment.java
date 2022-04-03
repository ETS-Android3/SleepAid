package com.example.sleepaid.Fragment.SleepDiary;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sleepaid.App;
import com.example.sleepaid.Component.EditTextAnswerComponent;
import com.example.sleepaid.Component.RadioGroupAnswerComponent;
import com.example.sleepaid.Component.SleepDiaryQuestionComponent;
import com.example.sleepaid.Database.Answer.Answer;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Database.Option.Option;
import com.example.sleepaid.Database.Question.Question;
import com.example.sleepaid.Database.Questionnaire.Questionnaire;
import com.example.sleepaid.Database.SleepData.SleepData;
import com.example.sleepaid.Database.SleepDataField.SleepDataField;
import com.example.sleepaid.Handler.ComponentHandler;
import com.example.sleepaid.Handler.DataHandler;
import com.example.sleepaid.Model.SharedViewModel;
import com.example.sleepaid.R;
import com.example.sleepaid.Service.RemoteDatabaseTransferService;
import com.example.sleepaid.Service.ValidationService;
import com.google.gson.Gson;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SleepDiaryQuestionsFragment extends Fragment {
    private View view;

    protected int questionnaireId;

    private SharedViewModel model;
    private AppDatabase db;

    protected int[] questionComponentIds;
    protected int[][] answerComponentIds;

    protected List<Integer> questionIds;
    protected int[][] sections;
    protected List<Option> options;
    protected ArrayAdapter<String>[][] answerSuggestions;
    protected String[][] emptyErrors;

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        this.view = view;

        this.model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        this.db = AppDatabase.getDatabase(App.getContext());

        getParentFragment().getParentFragment().getView().findViewById(R.id.scrollView).scrollTo(0, 0);

        Button saveButton = view.findViewById(R.id.saveSleepDiaryAnswersButton);
        saveButton.setOnClickListener(saveAnswers);

        this.model.setSleepDiaryHasOptions(this.questionnaireId, this.questionnaireId == 4);
        this.loadQuestionnaire();
    }

    private void loadQuestionnaire() {
        if (this.model.getQuestionnaire(this.questionnaireId) == null) {
            this.db.questionnaireDao()
                    .loadAllByIds(new int[]{this.questionnaireId})
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            questionnaire -> {
                                this.model.setQuestionnaire(this.questionnaireId, questionnaire.get(0));
                                this.setupDiary(questionnaire.get(0));

                                this.loadQuestions();
                            },
                            Throwable::printStackTrace
                    );
        } else {
            this.setupDiary(this.model.getQuestionnaire(this.questionnaireId));

            this.loadQuestions();
        }
    }

    private void setupDiary(Questionnaire questionnaire) {
        TextView diaryTitle = this.view.findViewById(R.id.diaryTitle);
        diaryTitle.setText(questionnaire.getName());

        // Allow them to fill in the bedtime sleep diary until noon the next day
        String date = ZonedDateTime.now().getHour() <= 12 && this.questionnaireId == 5 ?
                DataHandler.getFullDate(ZonedDateTime.now().minusDays(1)) :
                DataHandler.getFullDate(ZonedDateTime.now());

        TextView diaryInformation = this.view.findViewById(R.id.diaryInformation);
        diaryInformation.setText("You're filling this diary in for " + date + ".\n\n" + questionnaire.getInformation());

        TextView diaryCopyright = this.view.findViewById(R.id.diaryCopyright);
        diaryCopyright.setText(questionnaire.getCopyright());
    }

    private void loadQuestions() {
        if (this.model.getSleepDiaryQuestions(this.questionnaireId) == null) {
            this.db.questionDao()
                    .loadAllByQuestionnaireIds(new int[]{this.questionnaireId})
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            questions -> {
                                this.model.setSleepDiaryQuestions(this.questionnaireId, questions);
                                this.setupQuestions(questions);

                                this.loadOptions();
                            },
                            Throwable::printStackTrace
                    );
        } else {
            this.setupQuestions(this.model.getSleepDiaryQuestions(this.questionnaireId));

            this.loadOptions();
        }
    }

    private void setupQuestions(List<Question> questions) {
        this.questionIds = questions
                .stream()
                .map(Question::getId)
                .collect(Collectors.toList());

        for (int i = 0; i < questions.size(); i++) {
            SleepDiaryQuestionComponent question = this.view.findViewById(this.questionComponentIds[i]);
            question.setQuestionText(questions.get(i).getQuestion());

            if (!questions.get(i).getInformation().isEmpty()) {
                question.setInformationText(questions.get(i).getInformation());
            } else {
                question.setInformationVisibility(View.GONE);
            }
        }
    }

    private void loadOptions() {
        if (model.hasOptions(this.questionnaireId) && model.getSleepDiaryOptions(this.questionnaireId) == null) {
            this.db.optionDao()
                    .loadAllByQuestionnaireIds(new int[]{this.questionnaireId})
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            options -> {
                                this.model.setSleepDiaryOptions(this.questionnaireId, options);
                                this.options = options;

                                this.loadSuggestions();
                            },
                            Throwable::printStackTrace
                    );
        } else {
            this.options = model.getSleepDiaryOptions(this.questionnaireId);

            this.loadSuggestions();
        }
    }

    private void loadSuggestions() {
        if (model.getSleepDiaryAnswers(this.questionnaireId) == null) {
            this.db.answerDao()
                    .loadAllByQuestionIds(this.questionIds.stream().mapToInt(Integer::intValue).toArray())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            answers -> {
                                this.model.setSleepDiaryAnswers(this.questionnaireId, answers);
                                this.setupSuggestions(answers);

                                this.setupComponents();
                            },
                            Throwable::printStackTrace
                    );
        } else {
            this.setupSuggestions(this.model.getSleepDiaryAnswers(this.questionnaireId));

            this.setupComponents();
        }
    }

    private void setupSuggestions(List<Answer> answers) {
        Context context = App.getContext();
        int layout = R.layout.auto_complete_text_view_dropdown;

        for (int i = 0; i < this.questionIds.size(); i++) {
            int questionId = this.questionIds.get(i);

            for (int j = 0; j < this.sections[i].length; j++) {
                int section = this.sections[i][j];

                List<String> suggestionsForQuestionAndSection = answers.stream()
                        .filter(s -> s.getQuestionId() == questionId &&
                                s.getSection() == section &&
                                !s.getValue().isEmpty())
                        .map(Answer::getValue)
                        .collect(Collectors.toList());

                if (!suggestionsForQuestionAndSection.isEmpty()) {
                    Collections.sort(suggestionsForQuestionAndSection);

                    this.answerSuggestions[i][j] = new ArrayAdapter(context, layout, suggestionsForQuestionAndSection);
                }
            }
        }
    }

    private void setupComponents() {
        boolean answerForTodayExists = this.getAnswerForToday() != null;

        if (answerForTodayExists) {
            this.view.findViewById(R.id.alreadySubmittedMessage).setVisibility(View.VISIBLE);
        }

        for (int i = 0; i < this.answerComponentIds.length; i++) {
            for (int j = 0; j < this.answerComponentIds[i].length; j++) {
                View answer = this.view.findViewById(answerComponentIds[i][j]);

                if (answer instanceof EditTextAnswerComponent) {
                    this.setupEditTextComponent((EditTextAnswerComponent) answer, answerForTodayExists, i, j);
                } else if (answer instanceof RadioGroupAnswerComponent) {
                    this.setupRadioGroupComponent((RadioGroupAnswerComponent) answer, answerForTodayExists, i);
                }

                setupNextFocus(answer, i, j);
            }
        }
    }

    private void setupEditTextComponent(EditTextAnswerComponent editTextAnswerComponent,
                                        boolean answerForTodayExists,
                                        int i,
                                        int j) {
        if (answerForTodayExists) {
            editTextAnswerComponent.setEnabled(false);
        } else {
            this.setupAutoCompleteSuggestions(editTextAnswerComponent, i, j);

            if (editTextAnswerComponent.getInputType() == (InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME)) {
                this.setupTimeInput(editTextAnswerComponent, i, j);
            } else {
                editTextAnswerComponent.addTextChangedListener(new TextWatcher() {
                    public void afterTextChanged(Editable s) {
                        editTextAnswerComponent.setError(null);
                    }

                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    public void onTextChanged(CharSequence s, int start, int before, int count) { }
                });
            }
        }
    }

    private void setupRadioGroupComponent(RadioGroupAnswerComponent radioGroupAnswerComponent,
                                          boolean answerForTodayExists,
                                          int i) {
        this.setupRadioGroup(radioGroupAnswerComponent.getRadioGroup(), this.questionIds.get(i));

        if (answerForTodayExists) {
            radioGroupAnswerComponent.setEnabled(false);
        }
    }

    private void setupAutoCompleteSuggestions(EditTextAnswerComponent sleepDiaryAnswer, int i, int j) {
        if (this.answerSuggestions[i][j] != null) {
            sleepDiaryAnswer.setAdapter(this.answerSuggestions[i][j]);
        }

        sleepDiaryAnswer.setOnTouchListener((v, event) -> {
            sleepDiaryAnswer.showDropDown();
            return false;
        });
    }

    private void setupNextFocus(View component, int i, int j) {
        if (component instanceof EditTextAnswerComponent) {
            EditTextAnswerComponent sleepDiaryAnswer = (EditTextAnswerComponent) component;

            sleepDiaryAnswer.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                    goToNextComponent(i, j);
                    return true;
                }

                return false;
            });
        } else if (component instanceof RadioGroupAnswerComponent) {
            RadioGroupAnswerComponent sleepDiaryRadioGroupAnswer = (RadioGroupAnswerComponent) component;

            sleepDiaryRadioGroupAnswer.setOnClickListener(view -> {
                sleepDiaryRadioGroupAnswer.setError(null);
                goToNextComponent(i, j);
            });
        }
    }

    private void goToNextComponent(int i, int j) {
        if (j < this.answerComponentIds[i].length - 1) {
            this.view.findViewById(this.answerComponentIds[i][j + 1]).requestFocus();
        } else if (i < this.answerComponentIds.length - 1) {
            this.view.findViewById(this.answerComponentIds[i + 1][0]).requestFocus();
        } else {
            this.view.findViewById(this.answerComponentIds[i][j]).clearFocus();
        }
    }

    private void setupTimeInput(EditTextAnswerComponent sleepDiaryAnswer, int i, int j) {
        sleepDiaryAnswer.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                sleepDiaryAnswer.setError(null);

                if (sleepDiaryAnswer.length() == 2 && !sleepDiaryAnswer.getText().toString().contains(":")) {
                    sleepDiaryAnswer.setText(sleepDiaryAnswer.getText() + ":");
                    sleepDiaryAnswer.setSelection(sleepDiaryAnswer.length());
                } else if (sleepDiaryAnswer.length() == 5) {
                    goToNextComponent(i, j);
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });
    }

    private void setupRadioGroup(RadioGroup radioGroup, int questionId) {
        List<Option> possibleOptions = this.options
                .stream()
                .filter(o -> o.getQuestionId() == questionId)
                .collect(Collectors.toList());

        List<Integer> possibleOptionsIds = possibleOptions
                .stream()
                .map(Option::getId)
                .collect(Collectors.toList());

        List<String> possibleOptionsTexts = possibleOptions
                .stream()
                .map(Option::getValue)
                .collect(Collectors.toList());

        ComponentHandler.setupRadioGroup(
                radioGroup,
                R.style.RadioButton_White,
                DataHandler.getSizeInDp(22, getResources().getDisplayMetrics()),
                possibleOptionsIds,
                possibleOptionsTexts,
                null,
                null
        );
    }

    private View.OnClickListener saveAnswers = new View.OnClickListener() {
        public void onClick(View view) {
            if (getAnswerForToday() != null) {
                Toast.makeText(getActivity(), R.string.already_submitted_diary_message, Toast.LENGTH_LONG).show();
            } else {
                if (validateAnswers()) {
                    List<Answer> answers = getAnswers();

                    db.answerDao()
                            .insert(answers)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    () -> {
                                        Toast.makeText(getActivity(), "Diary saved successfully!", Toast.LENGTH_SHORT).show();
                                        model.setSleepDiaryAnswers(questionnaireId, answers);

                                        clearAnswers();
                                        transferToRemoteDatabase(answers);
                                    },
                                    Throwable::printStackTrace
                            );
                }
            }
        }
    };

    private boolean validateAnswers() {
        boolean hasErrors = false;

        for (int i = 0; i < this.sections.length; i++) {
            for (int j = 0; j < this.sections[i].length; j++) {
                View answerComponent = this.view.findViewById(this.answerComponentIds[i][j]);

                if (answerComponent instanceof EditTextAnswerComponent) {
                    boolean componentHasErrors = !ValidationService.validateEditText(
                            (EditTextAnswerComponent) answerComponent,
                            j != 0,
                            this.view.findViewById(this.answerComponentIds[i][0]),
                            this.emptyErrors[i][j],
                            hasErrors
                    );

                    if (componentHasErrors) {
                        hasErrors = true;
                    }
                } else if (answerComponent instanceof RadioGroupAnswerComponent) {
                    boolean componentHasErrors = !ValidationService.validateRadioGroup((RadioGroupAnswerComponent) answerComponent, hasErrors);

                    if (componentHasErrors) {
                        hasErrors = true;
                    }
                }
            }
        }

        return !hasErrors;
    }

    private List<Answer> getAnswers() {
        List<Answer> answers = new ArrayList<>();

        for (int i = 0; i < this.sections.length; i++) {
            for (int j = 0; j < this.sections[i].length; j++) {
                View answerComponent = this.view.findViewById(this.answerComponentIds[i][j]);
                String answer = "";
                int checkedId = -1;

                if (answerComponent instanceof EditTextAnswerComponent) {
                    answer = ((EditTextAnswerComponent) answerComponent).getText().toString();
                } else if (answerComponent instanceof RadioGroupAnswerComponent) {
                    RadioGroupAnswerComponent radioGroupAnswerComponent = (RadioGroupAnswerComponent) answerComponent;
                    checkedId = radioGroupAnswerComponent.getCheckedRadioButtonId();
                    answer = radioGroupAnswerComponent.getCheckedRadioButtonText();
                }

                // Allow them to fill in the bedtime sleep diary until noon the next day
                String date = ZonedDateTime.now().getHour() <= 12 && this.questionnaireId == 5 ?
                        DataHandler.getSQLiteDate(ZonedDateTime.now().minusDays(1)) :
                        DataHandler.getSQLiteDate(ZonedDateTime.now());

                answers.add(new Answer(
                        answer,
                        this.questionIds.get(i),
                        checkedId == -1 ? null : checkedId,
                        this.sections[i][j],
                        date
                ));
            }
        }

        return answers;
    }

    private void clearAnswers() {
        for (int i = 0; i < this.answerComponentIds.length; i++) {
            for (int j = 0; j < this.answerComponentIds[i].length; j++) {
                View answerComponent = this.view.findViewById(this.answerComponentIds[i][j]);

                if (answerComponent instanceof EditTextAnswerComponent) {
                    ((EditTextAnswerComponent) answerComponent).clear();
                } else if (answerComponent instanceof RadioGroupAnswerComponent) {
                    ((RadioGroupAnswerComponent) answerComponent).clearCheck();
                }
            }
        }
    }

    private void transferToRemoteDatabase(List<Answer> answers) {
        if (this.model.getUserId() == null) {
            this.db.configurationDao()
                    .loadAllByNames(new String[]{"userId"})
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            userId -> {
                                new RemoteDatabaseTransferService().execute(
                                        userId.get(0).getValue(),
                                        Integer.toString(this.questionnaireId),
                                        new Gson().toJson(answers)
                                );

                                this.generateSleepData(answers);
                            },
                            Throwable::printStackTrace
                    );
        } else {
            new RemoteDatabaseTransferService().execute(
                    this.model.getUserId(),
                    Integer.toString(this.questionnaireId),
                    new Gson().toJson(answers)
            );

            if (this.questionnaireId == 4) {
                this.generateSleepData(answers);
            }
        }
    }

    private void generateSleepData(List<Answer> answers) {
        List<Answer> sleepDataAnswers = answers.stream()
                .filter(a -> a.getQuestionId() == 31 || a.getQuestionId() == 32 || a.getQuestionId() == 35)
                .collect(Collectors.toList());

        if (!sleepDataAnswers.isEmpty()) {
            List<SleepData> sleepData = new ArrayList<>();

            this.db.sleepDataFieldDao()
                    .getAll()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            sleepDataFields -> {
                                List<String> fieldNames = sleepDataFields.stream()
                                        .map(SleepDataField::getName)
                                        .collect(Collectors.toList());

                                if (this.questionnaireId == 4) {
                                    ZonedDateTime wakeupTime = null;
                                    ZonedDateTime bedtime = null;

                                    for (Answer a : sleepDataAnswers) {
                                        if (a.getQuestionId() == 32) {
                                            sleepData.add(new SleepData(
                                                    fieldNames.get(1),
                                                    a.getDate(),
                                                    a.getValue()
                                            ));

                                            List<Integer> times = DataHandler.getIntsFromString(a.getValue());
                                            bedtime = ZonedDateTime.now()
                                                    .withHour(times.get(0))
                                                    .withMinute(times.get(1))
                                                    .minusDays(1)
                                                    .truncatedTo(ChronoUnit.MINUTES);
                                        } else {
                                            sleepData.add(new SleepData(
                                                    fieldNames.get(0),
                                                    a.getDate(),
                                                    a.getValue()
                                            ));

                                            List<Integer> times = DataHandler.getIntsFromString(a.getValue());
                                            wakeupTime = ZonedDateTime.now()
                                                    .withHour(times.get(0))
                                                    .withMinute(times.get(1))
                                                    .truncatedTo(ChronoUnit.MINUTES);
                                        }
                                    }

                                    if (wakeupTime != null && bedtime != null) {
                                        String date = sleepDataAnswers.get(0).getDate();
                                        List<Integer> differenceTimes = DataHandler.getIntsFromString(Duration.between(wakeupTime, bedtime).toString());

                                        int hours = differenceTimes.size() >= 1 ?
                                                differenceTimes.get(0) :
                                                0;
                                        int minutes = differenceTimes.size() >= 2 ?
                                                differenceTimes.get(1) :
                                                0;

                                        sleepData.add(new SleepData(
                                                fieldNames.get(2),
                                                date,
                                                DataHandler.getFormattedDuration(hours, minutes)
                                        ));
                                    }
                                } else {
                                    if (sleepDataAnswers.get(0).getQuestionId() == 31) {
                                        List<Integer> times = DataHandler.getIntsFromString(sleepDataAnswers.get(0).getValue());

                                        int hours = times.size() >= 1 ?
                                                times.get(0) :
                                                0;
                                        int minutes = times.size() >= 2 ?
                                                times.get(1) :
                                                0;

                                        sleepData.add(new SleepData(
                                                fieldNames.get(3),
                                                sleepDataAnswers.get(0).getDate(),
                                                DataHandler.getFormattedDuration(hours, minutes)
                                        ));
                                    }
                                }

                                this.saveSleepData(sleepData, fieldNames);
                            },
                            Throwable::printStackTrace
                    );
        }
    }

    private void saveSleepData(List<SleepData> sleepData, List<String> fieldNames) {
        this.db.sleepDataDao()
                .insert(sleepData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            for (String n : fieldNames) {
                                model.setLineSeries(n, null);
                            }

                            this.cancelReminder();
                        },
                        Throwable::printStackTrace
                );
    }

    private void cancelReminder() {
        String notificationName = this.questionnaireId == 4 ?
                "You still haven't filled in your morning sleep diary. You can do it until 00:00." :
                "You still haven't filled in your bedtime sleep diary. You can do it until 12:00.";

        this.db.notificationDao()
                .loadAllByNames(new String[]{notificationName})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        notification -> {
                            if (!notification.isEmpty()) {
                                notification.get(0).cancel(requireActivity());
                            }
                        },
                        Throwable::printStackTrace
                );
    }

    private Answer getAnswerForToday() {
        List<Answer> previousAnswers = model.getSleepDiaryAnswers(questionnaireId);
        Optional<Answer> answerForToday = previousAnswers.stream()
                .filter(a -> a.getDate().equals(DataHandler.getSQLiteDate(ZonedDateTime.now())))
                .findAny();

        if (answerForToday.isPresent()) {
            return answerForToday.get();
        }

        return null;
    }
}