package com.example.sleepaid.Fragment.SleepDiary;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sleepaid.App;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Model.SharedViewModel;
import com.example.sleepaid.R;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SleepDiaryQuestionsFragment extends Fragment {
    protected int questionnaireId;

    protected SharedViewModel model;
    private AppDatabase db;

    protected int[] questionIds;
    protected int[][] optionIds;
    protected ArrayAdapter<String>[][] optionSuggestions;

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        getParentFragment().getParentFragment().getView().findViewById(R.id.scrollView).scrollTo(0, 0);

        this.db = AppDatabase.getDatabase(App.getContext());

        this.setupComponents();
        this.loadQuestionnaire(this.questionnaireId);
    }

    private void setupComponents() {
        for (int i = 0; i < this.optionIds.length; i++) {
            for (int j = 0; j < this.optionIds[i].length; j++) {
                View answer = getView().findViewById(optionIds[i][j]);

                if (answer instanceof AutoCompleteTextView) {
                    AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) answer;

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

                    if(autoCompleteTextView.getInputType() != InputType.TYPE_CLASS_NUMBER && autoCompleteTextView.getInputType() != InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE) {
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
                }
            }
        }
    }

    protected void loadQuestionnaire(int questionnaireId) {
        db.questionDao()
                .loadAllByQuestionnaireIds(new int[]{questionnaireId})
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        questions -> {
                            for (int i = 0; i < questions.size(); i++) {
                                TextView question = getView().findViewById(this.questionIds[i]);
                                question.setText(questions.get(i).getQuestion());

                                this.loadInformation(questions.get(i).getInformation());
                                this.loadOptions(questions.get(i).getId());
                            }
                        },
                        Throwable::printStackTrace
                );
    }

    private void loadInformation(String information) {
    }

    private void loadOptions(int questionId) {
    }
}