package com.example.sleepaid.Fragment.SleepDiary;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sleepaid.App;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Database.Option.Option;
import com.example.sleepaid.Database.Question.Question;
import com.example.sleepaid.Database.SleepDiaryAnswer.SleepDiaryAnswer;
import com.example.sleepaid.Handler.ComponentHandler;
import com.example.sleepaid.Handler.DataHandler;
import com.example.sleepaid.Model.SharedViewModel;
import com.example.sleepaid.R;
import com.example.sleepaid.Service.InitialSettingsService;

import java.time.ZonedDateTime;
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
    protected int[] informationComponentIds;
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

        this.model.setSleepDiaryHasOptions(this.questionnaireId, this.questionnaireId == 4 ? true : false);
        this.loadQuestions();
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

                                this.questionIds = questions
                                        .stream()
                                        .map(q -> q.getId())
                                        .collect(Collectors.toList());
                                Collections.sort(this.questionIds);

                                for (int i = 0; i < questions.size(); i++) {
                                    TextView question = this.view.findViewById(this.questionComponentIds[i]);
                                    question.setText(questions.get(i).getQuestion());

                                    TextView information = this.view.findViewById(this.informationComponentIds[i]);
                                    if (!questions.get(i).getInformation().isEmpty()) {
                                        information.setText(questions.get(i).getInformation());
                                    } else {
                                        information.setVisibility(View.GONE);
                                    }
                                }

                                this.loadOptions();
                            },
                            Throwable::printStackTrace
                    );
        } else {
            List<Question> questions = this.model.getSleepDiaryQuestions(this.questionnaireId);

            this.questionIds = questions
                    .stream()
                    .map(q -> q.getId())
                    .collect(Collectors.toList());

            for (int i = 0; i < questions.size(); i++) {
                TextView question = this.view.findViewById(this.questionComponentIds[i]);
                question.setText(questions.get(i).getQuestion());

                TextView information = this.view.findViewById(this.informationComponentIds[i]);
                if (!questions.get(i).getInformation().isEmpty()) {
                    information.setText(questions.get(i).getInformation());
                } else {
                    information.setVisibility(View.GONE);
                }
            }

            this.loadOptions();
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
            this.db.sleepDiaryAnswerDao()
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

    private void setupSuggestions(List<SleepDiaryAnswer> answers) {
        Context context = App.getContext();
        int layout = R.layout.auto_complete_text_view_dropdown;

        for (int i = 0; i < this.questionIds.size(); i++) {
            int questionId = this.questionIds.get(i);

            for (int j = 0; j < this.sections[i].length; j++) {
                int section = this.sections[i][j];

                List<String> suggestionsForQuestionAndSection = answers.stream()
                        .filter(s -> s.getQuestionId() == questionId &&
                                s.getSection() == section)
                        .map(a -> a.getValue())
                        .collect(Collectors.toList());

                if (suggestionsForQuestionAndSection != null) {
                    Collections.sort(suggestionsForQuestionAndSection);

                    this.answerSuggestions[i][j] = new ArrayAdapter(context, layout, suggestionsForQuestionAndSection);
                }
            }
        }
    }

    private void setupComponents() {
        //TODO make it more obvious edit texts have been clicked
        for (int i = 0; i < this.answerComponentIds.length; i++) {
            for (int j = 0; j < this.answerComponentIds[i].length; j++) {
                View answer = this.view.findViewById(answerComponentIds[i][j]);

                if (answer instanceof AutoCompleteTextView) {
                    AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) answer;

                    this.setupAutoCompleteSuggestions(autoCompleteTextView, i, j);

                    if(autoCompleteTextView.getInputType() == (InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME)) {
                        this.setupTimeInput(autoCompleteTextView);
                    }
                } else if (answer instanceof RadioGroup) {
                    this.setupRadioGroup((RadioGroup) answer, this.questionIds.get(i));
                }
            }
        }
    }

    private void setupAutoCompleteSuggestions(AutoCompleteTextView autoCompleteTextView, int i, int j) {
        if (this.answerSuggestions[i][j] != null) {
            autoCompleteTextView.setAdapter(this.answerSuggestions[i][j]);

            autoCompleteTextView.setOnTouchListener(new View.OnTouchListener(){
                @Override
                public boolean onTouch(View v, MotionEvent event){
                    autoCompleteTextView.showDropDown();
                    return false;
                }
            });
        }
    }

    private void setupTimeInput(AutoCompleteTextView autoCompleteTextView) {
        autoCompleteTextView.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (autoCompleteTextView.length() == 2 && !autoCompleteTextView.getText().toString().contains(":")) {
                    autoCompleteTextView.setText(autoCompleteTextView.getText() + ":");
                    autoCompleteTextView.setSelection(autoCompleteTextView.length());
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
                .map(o -> o.getId())
                .collect(Collectors.toList());

        List<String> possibleOptionsTexts = possibleOptions
                .stream()
                .map(o -> o.getValue())
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
            //TODO check if it's already been submitted
            List<SleepDiaryAnswer> previousAnswers = model.getSleepDiaryAnswers(questionnaireId);
            Optional<SleepDiaryAnswer> answerForToday = previousAnswers.stream()
                    .filter(a -> a.getDate().equals(DataHandler.getSQLiteDate(ZonedDateTime.now())))
                    .findAny();

            if (answerForToday.isPresent()) {
                //TODO add error saying answer's already been submitted
                Toast.makeText(getActivity(), "You've already submitted your diary today.", Toast.LENGTH_LONG).show();
            } else {
                //TODO validation for each answer
                if (validateAnswers()) {
                    List<SleepDiaryAnswer> answers = getAnswers();

                    db.sleepDiaryAnswerDao()
                            .insert(answers)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    () -> {
                                        Toast.makeText(getActivity(), "Diary saved successfully!", Toast.LENGTH_LONG).show();
                                        model.setSleepDiaryAnswers(questionnaireId, answers);
                                        clearAnswers();
                                    },
                                    Throwable::printStackTrace
                            );
                }
            }
        }
    };

    private boolean validateAnswers() {
        boolean hasErrors = false;
        //TODO style the errors better

        for (int i = 0; i < this.sections.length; i++) {
            for (int j = 0; j < this.sections[i].length; j++) {
                View answerComponent = this.view.findViewById(this.answerComponentIds[i][j]);

                if (answerComponent instanceof AutoCompleteTextView) {
                    AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) answerComponent;
                    AutoCompleteTextView autoCompleteTextViewParent = (AutoCompleteTextView) this.view.findViewById(this.answerComponentIds[i][0]);

                    boolean isEmptyAndHasError = autoCompleteTextView.getText().toString().trim().isEmpty()
                            && this.emptyErrors[i][j] != null;

                    boolean isEmptyAndHasErrorAndParentIsNotNoneOrEmpty = autoCompleteTextView.getText().toString().trim().isEmpty()
                            && this.emptyErrors[i][j] != null
                            && !autoCompleteTextViewParent.getText().toString().trim().toLowerCase().equals("none")
                            && !autoCompleteTextViewParent.getText().toString().trim().isEmpty();

                    if((j == 0 && isEmptyAndHasError)
                            || (this.sections[i].length > 1 && j != 0 && isEmptyAndHasErrorAndParentIsNotNoneOrEmpty)) {
                        autoCompleteTextView.setError(this.emptyErrors[i][j]);

                        hasErrors = true;
                    } else {
                        //TODO check time inputs are correct, numbers are not ridiculous
                    }
                } else if (answerComponent instanceof RadioGroup) {
                    RadioGroup radioGroup = (RadioGroup) answerComponent;

                    int lastOptionId = radioGroup.getChildCount() - 1;
                    ((RadioButton) radioGroup.getChildAt(lastOptionId)).setError("Please select an option.");

                    hasErrors = true;
                }
            }
        }

        return !hasErrors;
    }

    private List<SleepDiaryAnswer> getAnswers() {
        List<SleepDiaryAnswer> answers = new ArrayList<>();

        for (int i = 0; i < this.sections.length; i++) {
            for (int j = 0; j < this.sections[i].length; j++) {
                View answerComponent = this.view.findViewById(this.answerComponentIds[i][j]);
                String answer = "";

                if (answerComponent instanceof AutoCompleteTextView) {
                    answer = ((AutoCompleteTextView) answerComponent).getText().toString();
                } else if (answerComponent instanceof RadioGroup) {
                    int answerId = ((RadioGroup) answerComponent).getCheckedRadioButtonId();
                    answer = ((RadioButton) this.view.findViewById(answerId)).getText().toString();
                }

                answers.add(new SleepDiaryAnswer(
                        answer,
                        this.questionIds.get(i),
                        this.sections[i][j],
                        DataHandler.getSQLiteDate(ZonedDateTime.now())
                ));
            }
        }

        return answers;
    }

    private void clearAnswers() {
        for (int i = 0; i < this.answerComponentIds.length; i++) {
            for (int j = 0; j < this.answerComponentIds[i].length; j++) {
                View answerComponent = this.view.findViewById(this.answerComponentIds[i][j]);

                if (answerComponent instanceof AutoCompleteTextView) {
                    ((AutoCompleteTextView) answerComponent).getText().clear();
                } else if (answerComponent instanceof RadioGroup) {
                    ((RadioGroup) answerComponent).clearCheck();
                }
            }
        }
    }
}