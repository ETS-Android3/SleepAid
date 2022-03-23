package com.example.sleepaid.Fragment.SleepDiary;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sleepaid.App;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.R;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SleepDiaryQuestionsFragment extends Fragment {
    private AppDatabase db;

    protected int[] questionIds;
    protected int[][] optionIds;

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        getParentFragment().getParentFragment().getView().findViewById(R.id.scrollView).scrollTo(0, 0);

        this.db = AppDatabase.getDatabase(App.getContext());
    }

    protected void loadQuestionnaire(int questionnaireId) {
        for (int i = 0; i < this.optionIds.length; i++) {
            for (int j : this.optionIds[i]) {
                View answer = getView().findViewById(j);

                if (answer instanceof EditText) {
                    if(((EditText) answer).getInputType() != InputType.TYPE_CLASS_NUMBER &&
                            ((EditText) answer).getInputType() != InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE) {
                        ((EditText) answer).addTextChangedListener(new TextWatcher() {
                            public void afterTextChanged(Editable s) {}

                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            public void onTextChanged(CharSequence s, int start,
                                                      int before, int count) {
                                if (s.length() == 2) {
                                    ((EditText) answer).setText(((EditText) answer).getText() + ":");
                                    ((EditText) answer).setSelection(((EditText) answer).length());
                                }
                            }
                        });
                    }
                }
            }
        }

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