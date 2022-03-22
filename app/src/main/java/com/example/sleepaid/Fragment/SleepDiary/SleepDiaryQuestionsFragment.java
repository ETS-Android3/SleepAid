package com.example.sleepaid.Fragment.SleepDiary;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private int[] questionIds = {
            R.id.question1,
            R.id.question2,
            R.id.question3,
            R.id.question4,
            R.id.question5,
            R.id.question6,
            R.id.question7,
            R.id.question8,
            R.id.question9
    };

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        getParentFragment().getParentFragment().getView().findViewById(R.id.scrollView).scrollTo(0, 0);

        this.db = AppDatabase.getDatabase(App.getContext());
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
                            }
                        },
                        Throwable::printStackTrace
                );
    }
}