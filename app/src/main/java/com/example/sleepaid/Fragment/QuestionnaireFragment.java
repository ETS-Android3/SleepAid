package com.example.sleepaid.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import com.example.sleepaid.Database.Answer;
import com.example.sleepaid.App;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Modal;
import com.example.sleepaid.Database.Option;
import com.example.sleepaid.Database.Question;
import com.example.sleepaid.R;
import com.example.sleepaid.SharedViewModel;
import com.example.sleepaid.TextBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@SuppressLint("NewApi")
public class QuestionnaireFragment extends Fragment {
    private Context context;
    private AppDatabase db;
    private SharedViewModel model;

    private List<Answer> currentAnswers;

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

        context = App.getContext();
        db = AppDatabase.getDatabase(context);
        model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        sizeInDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                25,
                getResources().getDisplayMetrics()
        );

        currentAnswers = model.getAnswers() == null ? new ArrayList<>() : model.getAnswers();

        Button backButton = getView().findViewById(R.id.backButton);
        backButton.setOnClickListener(this::loadPreviousScreen);

        Button nextButton = getView().findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this::loadNextScreen);

        if(model.getQuestions() == null) {
            loadAllQuestions();
        }
        else {
            loadScreen(model.getCurrentQuestionId());
        }
    }

    private void loadAllQuestions() {
        db.questionDao()
                .getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        questionData -> {
                            model.setQuestions(questionData);
                            loadAllOptions();
                        },
                        Throwable::printStackTrace
                );
    }

    private void loadAllOptions() {
        db.optionDao()
                .getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        optionData -> {
                            model.setOptions(optionData);
                            loadScreen(model.getCurrentQuestionId());
                        },
                        Throwable::printStackTrace
                );
    }

    public void loadPreviousScreen(View view) {
        loadScreen(model.getCurrentQuestionId() - 1);
    }

    public void loadNextScreen(View view) {
        RadioGroup radioGroup = getView().findViewById(R.id.radioGroup);
        int checkedId = radioGroup.getCheckedRadioButtonId();

        if (checkedId == -1) {
            DialogInterface.OnClickListener cancelAction = new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {}
            };

            Modal.show(
                    requireActivity(),
                    getString(R.string.question_validation),
                    getString(R.string.ok_modal),
                    cancelAction,
                    null,
                    null
            );
        }
        else {
            loadScreen(model.getCurrentQuestionId() + 1);
        }
    }

    private void loadScreen(int questionId) {
        RadioGroup radioGroup = getView().findViewById(R.id.radioGroup);
        int checkedId = radioGroup.getCheckedRadioButtonId();

        if (checkedId != -1) {
            int existingAnswerId = -1;

            if (!currentAnswers.isEmpty()) {
                Optional<Answer> answer = currentAnswers
                        .stream()
                        .filter(a -> a.getQuestionId() == model.getCurrentQuestionId())
                        .findAny();

                if (answer.isPresent()) {
                    existingAnswerId = currentAnswers.indexOf(answer.get());
                }
            }

            if (existingAnswerId > -1) {
                currentAnswers.set(existingAnswerId, new Answer(checkedId, model.getCurrentQuestionId()));
            }
            else {
                currentAnswers.add(new Answer(checkedId, model.getCurrentQuestionId()));
            }
        }

        if (questionId == 0) {
            exitQuestionnaire();
        }
        else if (questionId == model.getQuestions().size() + 1) {
            model.setAnswers(currentAnswers);

            NavHostFragment.findNavController(this).navigate(R.id.showSummaryAction);
        }
        else {
            getView().findViewById(R.id.scrollView).scrollTo(0, 0);

            model.setCurrentQuestionId(questionId);

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
        TextBox questionBox = getView().findViewById(R.id.question);
        TextBox informationBox = getView().findViewById(R.id.information);

        Optional<Question> question = model.getQuestions()
                .stream()
                .filter(q -> q.getId() == questionId)
                .findAny();

        if (question.isPresent()) {
            questionBox.setText(question.get().getQuestion());
            informationBox.setText(question.get().getInformation());
        }
    }

    private void loadOptionsForQuestion(int questionId) {
        Context contextThemeWrapper = new ContextThemeWrapper(context, R.style.RadioButton_White);

        RadioGroup radioGroup = getView().findViewById(R.id.radioGroup);
        radioGroup.clearCheck();
        radioGroup.removeAllViews();

        List<Option> possibleOptions = model.getOptions()
                .stream()
                .filter(o -> o.getQuestionId() == questionId)
                .collect(Collectors.toList());

        for (Option o : possibleOptions) {
            AppCompatRadioButton optionBox = new AppCompatRadioButton(contextThemeWrapper, null, R.style.RadioButton_White);

            optionBox.setId(o.getId());
            optionBox.setText(o.getValue());
            optionBox.setTextSize((int) (sizeInDp / 3.5));
            optionBox.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.MATCH_PARENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, sizeInDp);
            optionBox.setLayoutParams(layoutParams);

            optionBox.setPadding(
                    sizeInDp / 2,
                    sizeInDp / 2,
                    sizeInDp / 2,
                    sizeInDp / 2
            );

            radioGroup.addView(optionBox);
        }
    }

    private void loadPreviousAnswerForQuestion(int questionId) {
        if (!currentAnswers.isEmpty()) {
            Optional<Answer> answer = currentAnswers
                    .stream()
                    .filter(a -> a.getQuestionId() == questionId)
                    .findAny();

            if (answer.isPresent()) {
                AppCompatRadioButton option = getView().findViewById(answer.get().getOptionId());
                option.setChecked(true);
            }
        }
    }

    private void presetWakeUpTime(int questionId) {
        Optional<Answer> previousAnswer = currentAnswers
                .stream()
                .filter(a -> a.getQuestionId() == (questionId - 1))
                .findAny();

        Optional<Option> firstOption = model.getOptions()
                .stream()
                .filter(o -> o.getQuestionId() == questionId)
                .findFirst();

        if (previousAnswer.isPresent() && firstOption.isPresent()) {
            AppCompatRadioButton option = getView().findViewById(previousAnswer.get().getOptionId() + firstOption.get().getId() - 1);

            TextBox information = getView().findViewById(R.id.information);
            String currentText = information.getText().toString();
            String newText = getString(R.string.wakeup_time) + option.getText().toString().toLowerCase();

            information.setText(currentText + "\n " + newText);

            Optional<Answer> currentAnswer = currentAnswers
                    .stream()
                    .filter(a -> a.getQuestionId() == questionId)
                    .findAny();

            if(!currentAnswer.isPresent()) {
                option.setChecked(true);
            }
        }
    }
}