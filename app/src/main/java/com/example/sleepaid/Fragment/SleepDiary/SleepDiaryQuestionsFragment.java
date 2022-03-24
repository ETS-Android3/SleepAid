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
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sleepaid.App;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Database.Option.Option;
import com.example.sleepaid.Database.Question.Question;
import com.example.sleepaid.Handler.ComponentHandler;
import com.example.sleepaid.Handler.DataHandler;
import com.example.sleepaid.Model.SharedViewModel;
import com.example.sleepaid.R;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SleepDiaryQuestionsFragment extends Fragment {
    private View view;

    protected int questionnaireId;

    private SharedViewModel model;
    private AppDatabase db;

    protected int[] questionComponentIds;
    protected int[][] optionComponentIds;
    protected ArrayAdapter<String>[][] optionSuggestions;
    protected List<Integer> questionIds;
    protected List<Option> options;

    //TODO figure out some storage system here
    //TODO use Answer table? Or a new table for sleep diary?
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        this.view = view;

        this.model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        this.db = AppDatabase.getDatabase(App.getContext());

        getParentFragment().getParentFragment().getView().findViewById(R.id.scrollView).scrollTo(0, 0);

        this.loadQuestions();
    }

    private void loadQuestions() {
        if (this.model.getQuestions(this.questionnaireId) == null) {
            this.db.questionDao()
                    .loadAllByQuestionnaireIds(new int[]{this.questionnaireId})
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            questions -> {
                                this.model.setQuestions(this.questionnaireId, questions);
                                this.questionIds = questions
                                        .stream()
                                        .map(q -> q.getId())
                                        .collect(Collectors.toList());

                                for (int i = 0; i < questions.size(); i++) {
                                    TextView question = this.view.findViewById(this.questionComponentIds[i]);
                                    question.setText(questions.get(i).getQuestion());

//                                    TextView information = this.view.findViewById(this.informationComponentIds[i]);
//                                    information.setText(questions.get(i).getInformation());
                                }

                                this.loadOptions();
                            },
                            Throwable::printStackTrace
                    );
        } else {
            List<Question> questions = this.model.getQuestions(this.questionnaireId);

            this.questionIds = questions
                    .stream()
                    .map(q -> q.getId())
                    .collect(Collectors.toList());

            for (int i = 0; i < questions.size(); i++) {
                TextView question = this.view.findViewById(this.questionComponentIds[i]);
                question.setText(questions.get(i).getQuestion());

//                TextView information = this.view.findViewById(this.informationComponentIds[i]);
//                information.setText(questions.get(i).getInformation());
            }

            this.loadOptions();
        }
    }

    private void loadOptions() {
        if (model.getOptions(this.questionnaireId) == null) {
            this.db.optionDao()
                    .loadAllByQuestionnaireIds(new int[]{this.questionnaireId})
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            options -> {
                                this.model.setOptions(this.questionnaireId, options);
                                this.options = options;

                                this.setupComponents();
                            },
                            Throwable::printStackTrace
                    );
        } else {
            this.options = model.getOptions(this.questionnaireId);

            this.setupComponents();

            Context context = App.getContext();
            int layout = android.R.layout.simple_dropdown_item_1line;

            List<Option> suggestions = this.options
                    .stream()
                    .collect(Collectors.toList());

            List<Integer> questionIds = this.options
                    .stream()
                    .map(o -> o .getQuestionId())
                    .collect(Collectors.toList());

            Collections.sort(suggestions);
            Collections.sort(questionIds);

//            this.optionSuggestions = new ArrayAdapter[6][];
//
//            for (int i = 0; i < questionIds.size(); i++) {
//                int finalI = i;
//
//                List<String> suggestionsForQuestion = suggestions.stream()
//                        .filter(s -> s.getQuestionId() == finalI)
//                        .map(s -> s.getValue())
//                        .collect(Collectors.toList());
//
//                Collections.sort(suggestionsForQuestion);
//
//                this.optionSuggestions[i] = new ArrayAdapter[] {
//                        new ArrayAdapter(context, layout, suggestionsForQuestion)
//                };
//            }
        }
    }

    private void setupComponents() {
        for (int i = 0; i < this.optionComponentIds.length; i++) {
            for (int j = 0; j < this.optionComponentIds[i].length; j++) {
                View answer = this.view.findViewById(optionComponentIds[i][j]);

                if (answer instanceof AutoCompleteTextView) {
                    AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) answer;

                    this.setupAutoCompleteSuggestions(autoCompleteTextView, i, j);

                    if(autoCompleteTextView.getInputType() != InputType.TYPE_CLASS_NUMBER && autoCompleteTextView.getInputType() != InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE) {
                        this.setupTimeInput(autoCompleteTextView);
                    }
                } else if (answer instanceof RadioGroup) {
                    this.setupRadioGroup((RadioGroup) answer, this.questionIds.get(i));
                }
            }
        }
    }

    private void setupAutoCompleteSuggestions(AutoCompleteTextView autoCompleteTextView, int i, int j) {
        if (this.optionSuggestions != null) {
            autoCompleteTextView.setAdapter(this.optionSuggestions[i][j]);
        }

        autoCompleteTextView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                autoCompleteTextView.showDropDown();
                return false;
            }
        });
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
                DataHandler.getSizeInDp(25, getResources().getDisplayMetrics()),
                possibleOptionsIds,
                possibleOptionsTexts,
                null,
                null
        );
    }
}