package com.example.sleepaid.Fragment.Questionnaire;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sleepaid.App;
import com.example.sleepaid.Component.Modal;
import com.example.sleepaid.Component.TextBox;
import com.example.sleepaid.Database.Answer.Answer;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Database.Option.Option;
import com.example.sleepaid.Database.Question.Question;
import com.example.sleepaid.Handler.ComponentHandler;
import com.example.sleepaid.Handler.DataHandler;
import com.example.sleepaid.Model.SharedViewModel;
import com.example.sleepaid.R;

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

    private int[] questionnaireIds = new int[]{6};

    private int sizeInDp;

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

        this.currentAnswers = this.model.getQuestionnaireAnswers(6) == null ? new ArrayList<>() : this.model.getQuestionnaireAnswers(6);

        Button backButton = this.view.findViewById(R.id.backButton);
        backButton.setOnClickListener(this::loadPreviousScreen);

        Button nextButton = this.view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this::loadNextScreen);

        if(this.model.getQuestionnaireQuestions(6) == null) {
            loadAllQuestions();
        }
        else {
            loadScreen(this.model.getCurrentQuestionId());
        }
    }

    private void loadAllQuestions() {
        this.db.questionDao()
                .loadAllByQuestionnaireIds(this.questionnaireIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        questionData -> {
                            for (int i : this.questionnaireIds) {
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
                .loadAllByQuestionnaireIds(this.questionnaireIds)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        optionData -> {
                            for (int i : this.questionnaireIds) {
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
        RadioGroup radioGroup = this.view.findViewById(R.id.radioGroup);
        int checkedId = radioGroup.getCheckedRadioButtonId();

        if (checkedId == -1) {
            DialogInterface.OnClickListener cancelAction = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {}
            };

            Modal.show(
                    requireActivity(),
                    getString(R.string.radio_group_validation),
                    getString(R.string.ok_modal),
                    cancelAction,
                    null,
                    null
            );
        }
        else {
            loadScreen(this.model.getCurrentQuestionId() + 1);
        }
    }

    private void loadScreen(int questionId) {
        RadioGroup radioGroup = this.view.findViewById(R.id.radioGroup);
        int checkedId = radioGroup.getCheckedRadioButtonId();

        if (checkedId != -1) {
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

            if (existingAnswerId > -1) {
                this.currentAnswers.set(existingAnswerId, new Answer(checkedId, this.model.getCurrentQuestionId()));
            }
            else {
                this.currentAnswers.add(new Answer(checkedId, this.model.getCurrentQuestionId()));
            }
        }

        if (questionId == 0) {
            exitQuestionnaire();
        }
        else if (questionId == this.model.getQuestionnaireQuestions(6).size() + 1) {
            this.model.setQuestionnaireAnswers(6, this.currentAnswers);

            NavHostFragment.findNavController(this).navigate(R.id.showSummaryAction);
        }
        else {
            this.view.findViewById(R.id.scrollView).scrollTo(0, 0);

            this.model.setCurrentQuestionId(questionId);

            loadQuestion(questionId);
            loadOptionsForQuestion(questionId);
            loadPreviousAnswerForQuestion(questionId);

            if (questionId == 2) {
                presetWakeUpTime(questionId);
            }
        }
    }

    private void exitQuestionnaire() {
        Fragment fragment = this;

        DialogInterface.OnClickListener exitAction = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                NavHostFragment.findNavController(fragment).navigate(R.id.exitQuestionnaireAction);
            }
        };

        DialogInterface.OnClickListener cancelAction = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {}
        };

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

        Optional<Question> question = this.model.getQuestionnaireQuestions(6)
                .stream()
                .filter(q -> q.getId() == questionId)
                .findAny();

        if (question.isPresent()) {
            questionBox.setText(question.get().getQuestion());
            informationBox.setText(question.get().getInformation());
            informationBox.setTextSize((int) (this.sizeInDp / 3.5));
        }
    }

    private void loadOptionsForQuestion(int questionId) {
        RadioGroup radioGroup = this.view.findViewById(R.id.radioGroup);

        List<Option> possibleOptions = this.model.getQuestionnaireOptions(6)
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
                this.sizeInDp,
                possibleOptionsIds,
                possibleOptionsTexts,
                null,
                null
        );
    }

    private void loadPreviousAnswerForQuestion(int questionId) {
        if (!this.currentAnswers.isEmpty()) {
            Optional<Answer> answer = this.currentAnswers
                    .stream()
                    .filter(a -> a.getQuestionId() == questionId)
                    .findAny();

            if (answer.isPresent()) {
                AppCompatRadioButton option = this.view.findViewById(answer.get().getOptionId());
                option.setChecked(true);
            }
        }
    }

    private void presetWakeUpTime(int questionId) {
        Optional<Answer> previousAnswer = this.currentAnswers
                .stream()
                .filter(a -> a.getQuestionId() == (questionId - 1))
                .findAny();

        Optional<Option> firstOption = this.model.getQuestionnaireOptions(6)
                .stream()
                .filter(o -> o.getQuestionId() == questionId)
                .findFirst();

        if (previousAnswer.isPresent() && firstOption.isPresent()) {
            AppCompatRadioButton option = this.view.findViewById(previousAnswer.get().getOptionId() + firstOption.get().getId() - 1);

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