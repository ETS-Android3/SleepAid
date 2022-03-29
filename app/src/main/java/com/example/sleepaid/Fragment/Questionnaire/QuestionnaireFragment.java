package com.example.sleepaid.Fragment.Questionnaire;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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
import com.example.sleepaid.Handler.ComponentHandler;
import com.example.sleepaid.Handler.DataHandler;
import com.example.sleepaid.Model.SharedViewModel;
import com.example.sleepaid.R;
import com.example.sleepaid.Service.ValidationService;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class QuestionnaireFragment extends Fragment {
    private View view;
    private Context context;
    private AppDatabase db;
    private SharedViewModel model;

    private List<Answer> currentAnswers;

    private int sizeInDp;

    private LinearLayout answerContainer;

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
        return inflater.inflate(R.layout.fragment_questionnaire, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        this.view = view;
        this.context = App.getContext();
        this.db = AppDatabase.getDatabase(context);
        this.model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        this.sizeInDp = DataHandler.getSizeInDp(25, getResources().getDisplayMetrics());

        this.answerContainer = this.view.findViewById(R.id.questionnaireAnswerContainer);

        this.currentAnswers = this.model.getQuestionnaireAnswers() == null ? new ArrayList<>() : this.model.getQuestionnaireAnswers();

        Button backButton = this.view.findViewById(R.id.backButton);
        backButton.setOnClickListener(this::loadPreviousScreen);

        Button nextButton = this.view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this::loadNextScreen);

        if(this.model.getQuestionnaireQuestions() == null) {
            loadAllQuestions();
        }
        else {
            loadScreen(this.model.getCurrentQuestionId());
        }
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
        for (int i = 0; i < this.answerContainer.getChildCount(); i++) {
            int answerId = -1;
            String answerText = null;

            if (this.answerContainer.getChildAt(i) instanceof EditTextAnswerComponent) {
                EditTextAnswerComponent answerComponent = (EditTextAnswerComponent) this.answerContainer.getChildAt(i);

                answerText = answerComponent.getText().toString();
            } else if (this.answerContainer.getChildAt(i) instanceof RadioGroupAnswerComponent) {
                RadioGroupAnswerComponent answerComponent = (RadioGroupAnswerComponent) this.answerContainer.getChildAt(i);

                answerId = answerComponent.getCheckedRadioButtonId();
                if (answerId != -1) {
                    answerText = ((RadioButton) this.view.findViewById(answerId)).getText().toString();
                }
            }

            if (!answerText.isEmpty() && answerText != null) {
                int existingAnswerId = -1;

                if (!this.currentAnswers.isEmpty()) {
                    Optional<Answer> answer = this.currentAnswers
                            .stream()
                            .filter(a -> a.getQuestionId() == this.model.getCurrentQuestionId())
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
                            1,
                            DataHandler.getSQLiteDate(ZonedDateTime.now())
                    ));
                }
                else {
                    this.currentAnswers.add(new Answer(
                            answerText,
                            this.model.getCurrentQuestionId(),
                            answerId == -1 ? null : answerId,
                            1,
                            DataHandler.getSQLiteDate(ZonedDateTime.now())
                    ));
                }
            }
        }

        if (questionId == 0) {
            exitQuestionnaire();
        }
        else if (questionId == this.model.getQuestionnaireQuestions(6).get(this.model.getQuestionnaireQuestions(6).size() - 1).getId() + 1) {
            //TODO fix this
            this.model.setQuestionnaireAnswers(6, this.currentAnswers);

            NavHostFragment.findNavController(this).navigate(R.id.showSummaryAction);
        }
        else {
            this.view.findViewById(R.id.scrollView).scrollTo(0, 0);

            this.model.setCurrentQuestionId(questionId);

            loadQuestion(questionId);
            loadOptionsForQuestion(questionId);
            loadPreviousAnswerForQuestion(questionId);

            if (questionId == 24) {
                presetWakeUpTime(questionId);
            }
        }
    }

    private boolean validateAnswers() {
        boolean hasErrors = false;

        for (int i = 0; i < this.answerContainer.getChildCount(); i++) {
            if (this.answerContainer.getChildAt(i) instanceof EditTextAnswerComponent) {
                hasErrors = ValidationService.validateEditText(
                        (EditTextAnswerComponent) this.answerContainer.getChildAt(i),
                        false,
                        null,
                        "Please enter a value.",
                        hasErrors
                );
            } else if (this.answerContainer.getChildAt(i) instanceof RadioGroupAnswerComponent) {
                hasErrors = ValidationService.validateRadioGroup(
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

    private void loadQuestion(int questionId) {
        TextBox questionBox = this.view.findViewById(R.id.question);
        TextBox informationBox = this.view.findViewById(R.id.information);

        Optional<Question> question = this.model.getQuestionnaireQuestions()
                .stream()
                .filter(q -> q.getId() == questionId)
                .findAny();

        if (question.isPresent()) {
            questionBox.setText(question.get().getQuestion());

            if (!question.get().getInformation().isEmpty()) {
                informationBox.setVisibility(View.VISIBLE);
                informationBox.setText(question.get().getInformation());
                informationBox.setTextSize((int) (this.sizeInDp / 3.5));
            } else {
                informationBox.setVisibility(View.GONE);
            }
        }
    }

    private void loadOptionsForQuestion(int questionId) {
        this.answerContainer.removeAllViews();

        List<Option> possibleOptions = this.model.getQuestionnaireOptions()
                .stream()
                .filter(o -> o.getQuestionId() == questionId)
                .collect(Collectors.toList());

        if (!possibleOptions.isEmpty()) {
            RadioGroupAnswerComponent radioGroupAnswerComponent = new RadioGroupAnswerComponent(App.getContext());
            this.answerContainer.addView(radioGroupAnswerComponent);

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
                    null
            );
        } else {
            EditTextAnswerComponent editTextAnswerComponent = new EditTextAnswerComponent(App.getContext());
            //TODO do this based on question
            editTextAnswerComponent.setInputType("number");

            this.answerContainer.addView(editTextAnswerComponent);
        }
    }

    private void loadPreviousAnswerForQuestion(int questionId) {
        if (!this.currentAnswers.isEmpty()) {
            Optional<Answer> answer = this.currentAnswers
                    .stream()
                    .filter(a -> a.getQuestionId() == questionId)
                    .findAny();

            if (answer.isPresent()) {
                for (int i = 0; i < this.answerContainer.getChildCount(); i++) {
                    if (this.answerContainer.getChildAt(i) instanceof EditTextAnswerComponent) {
                        ((EditTextAnswerComponent) this.answerContainer.getChildAt(i)).setText(answer.get().getValue());
                    } else if (this.answerContainer.getChildAt(i) instanceof RadioGroupAnswerComponent) {
                        RadioButton option = this.view.findViewById(answer.get().getOptionId());
                        option.setChecked(true);
                    }
                }
            }
        }
    }

    private void presetWakeUpTime(int questionId) {
        Optional<Answer> previousAnswer = this.currentAnswers
                .stream()
                .filter(a -> a.getQuestionId() == (questionId - 1))
                .findAny();

        Optional<Option> firstOption = this.model.getQuestionnaireOptions()
                .stream()
                .filter(o -> o.getQuestionId() == questionId)
                .findFirst();

        if (previousAnswer.isPresent() && firstOption.isPresent()) {
            RadioButton option = this.view.findViewById(previousAnswer.get().getOptionId() + firstOption.get().getId() - 1);

            TextBox information = this.view.findViewById(R.id.information);
            String currentText = information.getText().toString();
            String newText = getString(R.string.wakeup_time) + option.getText().toString().toLowerCase();

            information.setText(currentText + "\n " + newText);

            Optional<Answer> currentAnswer = this.currentAnswers
                    .stream()
                    .filter(a -> a.getQuestionId() == questionId)
                    .findAny();

            if(!currentAnswer.isPresent()) {
                option.setChecked(true);
            }
        }
    }
}
