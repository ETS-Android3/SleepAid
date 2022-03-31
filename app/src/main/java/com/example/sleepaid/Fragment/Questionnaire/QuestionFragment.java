package com.example.sleepaid.Fragment.Questionnaire;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sleepaid.App;
import com.example.sleepaid.Component.EditTextAnswerComponent;
import com.example.sleepaid.Component.Modal;
import com.example.sleepaid.Component.RadioGroupAnswerComponent;
import com.example.sleepaid.Component.TextBox;
import com.example.sleepaid.Database.Answer.Answer;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Database.Option.Option;
import com.example.sleepaid.Database.Question.Question;
import com.example.sleepaid.Database.Questionnaire.Questionnaire;
import com.example.sleepaid.Fragment.QuestionnaireFragment;
import com.example.sleepaid.Handler.ComponentHandler;
import com.example.sleepaid.Handler.DataHandler;
import com.example.sleepaid.Model.SharedViewModel;
import com.example.sleepaid.R;
import com.example.sleepaid.Service.ValidationService;
import com.google.common.collect.ImmutableMap;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class QuestionFragment extends QuestionnaireFragment {
    private View view;
    private Context context;
    private AppDatabase db;
    private SharedViewModel model;

    private List<Answer> currentAnswers;

    private int sizeInDp;

    private LinearLayout answerContainer;

    private HashMap<Integer, String> answerInputTypes;
    private HashMap<Integer, String> answerHints;
    private HashMap<Integer, Integer> answerMaxLengths;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (model.getCurrentQuestionId() == 1) {
                    exitQuestionnaire();
                }
                else {
                    loadScreen(model.getCurrentQuestionId() - 1);
                }
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_question, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.view = view;
        this.context = App.getContext();
        this.db = AppDatabase.getDatabase(context);
        this.model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        this.sizeInDp = DataHandler.getSizeInDp(25, getResources().getDisplayMetrics());

        this.answerContainer = this.view.findViewById(R.id.questionnaireAnswerContainer);

        this.currentAnswers = this.model.getQuestionnaireAnswers() == null ? new ArrayList<>() : this.model.getQuestionnaireAnswers();

        this.answerInputTypes = new HashMap<>(ImmutableMap.of(
                1, "time",
                2, "float",
                3, "time",
                4, "float"
        ));

        this.answerHints = new HashMap<>(ImmutableMap.of(
                1, "e.g. 22:30",
                2, "e.g. 30",
                3, "e.g. 06:30",
                4, "e.g. 8"
        ));

        this.answerMaxLengths = new HashMap<>(ImmutableMap.of(
                1, 5,
                2, 5,
                3, 5,
                4, 5
        ));

        Button backButton = this.view.findViewById(R.id.backButton);
        backButton.setOnClickListener(this::loadPreviousScreen);

        Button nextButton = this.view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this::loadNextScreen);

        if(this.model.getQuestionnaires() == null) {
            loadAllQuestionnaires();
        } else {
            loadScreen(this.model.getCurrentQuestionId());
        }
    }

    private void loadAllQuestionnaires() {
        this.db.questionnaireDao()
                .loadAllByIds(this.model.getQuestionnaireIds())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        questionnaireData -> {
                            for (int i = 0; i < questionnaireData.size(); i++) {
                                this.model.setQuestionnaire(questionnaireData.get(i).getId(), questionnaireData.get(i));
                            }

                            loadAllQuestions();
                        },
                        Throwable::printStackTrace
                );
    }

    private void loadAllQuestions() {
        this.db.questionDao()
                .loadAllByQuestionnaireIds(this.model.getQuestionnaireIds())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        questionData -> {
                            for (int i : this.model.getQuestionnaireIds()) {
                                List<Question> questionsForQuestionnaire = questionData.stream()
                                        .filter(q -> q.getQuestionnaireId() == i)
                                        .collect(Collectors.toList());

                                this.model.setQuestionnaireQuestions(i, questionsForQuestionnaire);
                            }

                            loadAllOptions();
                        },
                        Throwable::printStackTrace
                );
    }

    private void loadAllOptions() {
        this.db.optionDao()
                .loadAllByQuestionnaireIds(this.model.getQuestionnaireIds())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        optionData -> {
                            for (int i : this.model.getQuestionnaireIds()) {
                                List<Integer> questionIds = this.model.getQuestionnaireQuestions(i).stream()
                                        .map(q -> q.getId())
                                        .collect(Collectors.toList());

                                List<Option> optionsForQuestionnaire = optionData.stream()
                                        .filter(o -> questionIds.contains(o.getQuestionId()))
                                        .collect(Collectors.toList());

                                this.model.setQuestionnaireOptions(i, optionsForQuestionnaire);
                            }

                            loadScreen(this.model.getCurrentQuestionId());
                        },
                        Throwable::printStackTrace
                );
    }

    public void loadPreviousScreen(View view) {
        loadScreen(this.model.getCurrentQuestionId() - 1);
    }

    public void loadNextScreen(View view) {
        if (this.validateAnswers()) {
            loadScreen(this.model.getCurrentQuestionId() + 1);
        }
    }

    private void loadScreen(int questionId) {
        this.saveAnswers();

        List<Question> firstQuestionnaireQuestions = this.model.getQuestionnaireQuestions(this.model.getQuestionnaireIds()[0]);
        int firstQuestionId = firstQuestionnaireQuestions.get(0).getId();

        int lastQuestionnaireId = this.model.getQuestionnaireIds()[this.model.getQuestionnaireIds().length - 1];
        List<Question> lastQuestionnaireQuestions = this.model.getQuestionnaireQuestions(lastQuestionnaireId);
        int lastQuestionId = lastQuestionnaireQuestions.get(lastQuestionnaireQuestions.size() - 1).getId();

        if (questionId == firstQuestionId - 1) {
            exitQuestionnaire();
        } else if (questionId == lastQuestionId + 1) {
            this.model.setQuestionnaireAnswers(this.currentAnswers);

            NavHostFragment.findNavController(this).navigate(R.id.showSummaryAction);
        } else {
            this.view.findViewById(R.id.scrollView).scrollTo(0, 0);

            this.model.setCurrentQuestionId(questionId);

            setupQuestion(questionId);
            setupOptions(questionId);
            setupPreviousAnswer(questionId);

            if (questionId == 23) {
                presetWakeUpTime(questionId);
            }
        }
    }

    private void saveAnswers() {
        for (int i = 0; i < this.answerContainer.getChildCount(); i++) {
            int answerId = -1;
            String answerText = null;
            final int section = i + 1;

            if (this.answerContainer.getChildAt(i) instanceof EditTextAnswerComponent) {
                EditTextAnswerComponent answerComponent = (EditTextAnswerComponent) this.answerContainer.getChildAt(i);

                answerText = answerComponent.getText().toString();
            } else if (this.answerContainer.getChildAt(i) instanceof RadioGroupAnswerComponent) {
                RadioGroupAnswerComponent answerComponent = (RadioGroupAnswerComponent) this.answerContainer.getChildAt(i);

                answerId = answerComponent.getCheckedRadioButtonId();
                answerText = answerComponent.getCheckedRadioButtonText();
            }

            if (answerText != null && !answerText.isEmpty()) {
                int existingAnswerId = -1;

                if (!this.currentAnswers.isEmpty()) {
                    Optional<Answer> answer = this.currentAnswers
                            .stream()
                            .filter(a -> a.getQuestionId() == this.model.getCurrentQuestionId() &&
                                    a.getSection() == section)
                            .findAny();

                    if (answer.isPresent()) {
                        existingAnswerId = this.currentAnswers.indexOf(answer.get());
                    }
                }

                if (existingAnswerId != -1) {
                    this.currentAnswers.set(existingAnswerId, new Answer(
                            answerText,
                            this.model.getCurrentQuestionId(),
                            answerId == -1 ? null : answerId,
                            section,
                            DataHandler.getSQLiteDate(ZonedDateTime.now())
                    ));
                }
                else {
                    this.currentAnswers.add(new Answer(
                            answerText,
                            this.model.getCurrentQuestionId(),
                            answerId == -1 ? null : answerId,
                            section,
                            DataHandler.getSQLiteDate(ZonedDateTime.now())
                    ));
                }
            }
        }
    }

    private boolean validateAnswers() {
        boolean hasErrors = false;

        for (int i = 0; i < this.answerContainer.getChildCount(); i++) {
            if (this.answerContainer.getChildAt(i) instanceof EditTextAnswerComponent) {
                hasErrors = !ValidationService.validateEditText(
                        (EditTextAnswerComponent) this.answerContainer.getChildAt(i),
                        false,
                        null,
                        "Please enter a value.",
                        hasErrors
                );
            } else if (this.answerContainer.getChildAt(i) instanceof RadioGroupAnswerComponent) {
                hasErrors = !ValidationService.validateRadioGroup(
                        (RadioGroupAnswerComponent) this.answerContainer.getChildAt(i),
                        hasErrors
                );
            }
        }

        return !hasErrors;
    }

    private void exitQuestionnaire() {
        Fragment fragment = this;

        DialogInterface.OnClickListener exitAction = (dialog, whichButton) -> NavHostFragment.findNavController(fragment).navigate(R.id.exitQuestionnaireAction);

        DialogInterface.OnClickListener cancelAction = (dialog, whichButton) -> {};

        Modal.show(
                requireActivity(),
                getString(R.string.exit_questionnaire),
                getString(R.string.yes_modal),
                exitAction,
                getString(R.string.cancel_modal),
                cancelAction
        );
    }

    private void setupQuestion(int questionId) {
        TextView questionnaireTitle = this.view.findViewById(R.id.questionnaireTitle);
        TextBox questionnaireInformationBox = this.view.findViewById(R.id.questionnaireInformation);
        TextBox questionBox = this.view.findViewById(R.id.question);
        TextBox questionInformationBox = this.view.findViewById(R.id.questionInformation);
        TextView questionnaireCopyright = this.view.findViewById(R.id.questionnaireCopyright);

        Optional<Question> question = this.model.getQuestionnaireQuestions()
                .stream()
                .filter(q -> q.getId() == questionId)
                .findAny();

        if (question.isPresent()) {
            Questionnaire questionnaire = this.model.getQuestionnaire(question.get().getQuestionnaireId());
            questionnaireTitle.setText(questionnaire.getName());

            if (this.model.getFirstQuestionsIds().containsValue(questionId)) {
                questionnaireInformationBox.setVisibility(View.VISIBLE);
                questionnaireInformationBox.setText(questionnaire.getInformation());
            } else {
                questionnaireInformationBox.setVisibility(View.GONE);
            }

            String questionText = question.get().getQuestion().contains("{") ?
                    question.get().getQuestion().substring(0, question.get().getQuestion().indexOf("{") - 1) :
                    question.get().getQuestion();

            questionBox.setText(questionText);

            if (!question.get().getInformation().isEmpty()) {
                questionInformationBox.setVisibility(View.VISIBLE);
                questionInformationBox.setText(question.get().getInformation());
            } else {
                questionInformationBox.setVisibility(View.GONE);
            }

            if (questionnaire.getCopyright() != null) {
                questionnaireCopyright.setVisibility(View.VISIBLE);
                questionnaireCopyright.setText(questionnaire.getCopyright());
            } else {
                questionnaireCopyright.setVisibility(View.GONE);
            }
        }
    }

    private void setupOptions(int questionId) {
        this.answerContainer.removeAllViews();

        List<Option> possibleOptions = this.model.getQuestionnaireOptions()
                .stream()
                .filter(o -> o.getQuestionId() == questionId)
                .collect(Collectors.toList());

        if (!possibleOptions.isEmpty()) {
            this.setupRadioGroupComponents(questionId, possibleOptions);
        } else {
            this.setupEditTextComponent(questionId);
        }
    }

    private void setupRadioGroupComponents(int questionId,
                                           List<Option> possibleOptions) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, sizeInDp);

        List<String> sections = new ArrayList<>();

        Optional<Question> question = this.model.getQuestionnaireQuestions()
                .stream()
                .filter(q -> q.getId() == questionId)
                .findAny();

        if (question.isPresent() && question.get().getQuestion().contains("{")) {
            String sectionsText = question.get().getQuestion().substring(
                    question.get().getQuestion().indexOf("{") + 1,
                    question.get().getQuestion().length() - 1
            );

            sections = Arrays.asList(sectionsText.split("; "));
        } else {
            sections.add(null);
        }

        for (String s : sections) {
            this.setupRadioGroupSection(s, sections.get(sections.size() - 1), possibleOptions, layoutParams);
        }
    }

    private void setupRadioGroupSection(String s,
                                        String lastSection,
                                        List<Option> possibleOptions,
                                        LinearLayout.LayoutParams layoutParams) {
        RadioGroupAnswerComponent radioGroupAnswerComponent = new RadioGroupAnswerComponent(requireActivity());

        if (s != null) {
            radioGroupAnswerComponent.setSubQuestion(s);
        }

        RadioGroup radioGroup = radioGroupAnswerComponent.getRadioGroup();

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
                this.sizeInDp,
                possibleOptionsIds,
                possibleOptionsTexts,
                null,
                view -> radioGroupAnswerComponent.setError(null)
        );

        if (s != null && !s.equals(lastSection)) {
            radioGroupAnswerComponent.setLayoutParams(layoutParams);
        }

        this.answerContainer.addView(radioGroupAnswerComponent);
    }

    private void setupEditTextComponent(int questionId) {
        EditTextAnswerComponent editTextAnswerComponent = new EditTextAnswerComponent(requireActivity());

        editTextAnswerComponent.setInputType(this.answerInputTypes.get(questionId));
        editTextAnswerComponent.setHint(this.answerHints.get(questionId));
        editTextAnswerComponent.setMaxLength(this.answerMaxLengths.get(questionId));
        editTextAnswerComponent.setTextSize(20);

        if (this.answerInputTypes.get(questionId).equals("time")) {
            editTextAnswerComponent.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    editTextAnswerComponent.setError(null);

                    if (editTextAnswerComponent.length() == 2 && !editTextAnswerComponent.getText().toString().contains(":")) {
                        editTextAnswerComponent.setText(editTextAnswerComponent.getText() + ":");
                        editTextAnswerComponent.setSelection(editTextAnswerComponent.length());
                    }
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                public void onTextChanged(CharSequence s, int start, int before, int count) { }
            });
        } else {
            editTextAnswerComponent.addTextChangedListener(new TextWatcher() {
                public void afterTextChanged(Editable s) {
                    editTextAnswerComponent.setError(null);
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                public void onTextChanged(CharSequence s, int start, int before, int count) { }
            });
        }

        this.answerContainer.addView(editTextAnswerComponent);
    }

    private void setupPreviousAnswer(int questionId) {
        if (!this.currentAnswers.isEmpty()) {
            List<Answer> answers = this.currentAnswers
                    .stream()
                    .filter(a -> a.getQuestionId() == questionId)
                    .collect(Collectors.toList());

            if (!answers.isEmpty()) {
                for (int i = 0; i < this.answerContainer.getChildCount(); i++) {
                    final int section = i + 1;
                    Optional<Answer> answerForSection = answers.stream()
                            .filter(a -> a.getSection() == section)
                            .findFirst();

                    if (this.answerContainer.getChildAt(i) instanceof EditTextAnswerComponent) {
                        if (answerForSection.isPresent()) {
                            ((EditTextAnswerComponent) this.answerContainer.getChildAt(i)).setText(answerForSection.get().getValue());
                        }
                    } else if (this.answerContainer.getChildAt(i) instanceof RadioGroupAnswerComponent) {
                        if (answerForSection.isPresent()) {
                            ((RadioGroupAnswerComponent) this.answerContainer.getChildAt(i)).setChecked(answerForSection.get().getOptionId(), true);
                        }
                    }
                }
            }
        }
    }

    private void presetWakeUpTime(int questionId) {
        Optional<Answer> previousAnswer = this.currentAnswers
                .stream()
                .filter(a -> a.getQuestionId() == (questionId - 1))
                .findFirst();

        int numberOfOptions = this.model.getQuestionnaireOptions()
                .stream()
                .filter(o -> o.getQuestionId() == questionId)
                .collect(Collectors.toList())
                .size();

        if (previousAnswer.isPresent()) {
            RadioGroupAnswerComponent radioGroupAnswerComponent = (RadioGroupAnswerComponent) this.answerContainer.getChildAt(0);
            int optionId = previousAnswer.get().getOptionId() + numberOfOptions;
            System.out.println(optionId);

            TextBox information = this.view.findViewById(R.id.questionInformation);
            String currentText = information.getText().toString();
            String newText = getString(R.string.wakeup_time) + radioGroupAnswerComponent.getRadioButtonText(optionId).toLowerCase();

            information.setText(currentText + "\n " + newText);

            Optional<Answer> currentAnswer = this.currentAnswers
                    .stream()
                    .filter(a -> a.getQuestionId() == questionId)
                    .findFirst();

            if(!currentAnswer.isPresent()) {
                radioGroupAnswerComponent.setChecked(optionId,true);
            }
        }
    }
}
