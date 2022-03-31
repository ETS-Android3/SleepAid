package com.example.sleepaid.Fragment.Questionnaire;

import android.content.Context;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sleepaid.App;
import com.example.sleepaid.Component.TextBox;
import com.example.sleepaid.Database.Answer.Answer;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Database.Configuration.Configuration;
import com.example.sleepaid.Database.Question.Question;
import com.example.sleepaid.Model.SharedViewModel;
import com.example.sleepaid.R;
import com.example.sleepaid.Service.InitialSettingsService;
import com.example.sleepaid.Service.RemoteDatabaseTransferService;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AnswersSummaryFragment extends Fragment {
    private View view;
    private Context context;
    private AppDatabase db;
    private SharedViewModel model;

    private int sizeInDp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Fragment fragment = this;

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                NavHostFragment.findNavController(fragment).navigate(R.id.exitSummaryAction);
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_answers_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        this.view = view;
        this.context = App.getContext();
        this.db = AppDatabase.getDatabase(this.context);
        this.model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        this.sizeInDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                25,
                getResources().getDisplayMetrics()
        );

        Button backButton = this.view.findViewById(R.id.backButton);
        backButton.setOnClickListener(this::loadPreviousScreen);

        Button finishButton = this.view.findViewById(R.id.finishButton);
        finishButton.setOnClickListener(this::storeAnswers);

        loadAllAnswers();
    }

    private void loadAllAnswers() {
        LinearLayout layout = this.view.findViewById(R.id.answers);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, sizeInDp / 2);

        this.addUserId(layout, layoutParams);

        for (Question q : this.model.getQuestionnaireQuestions()) {
            int questionId = q.getId();

            String questionText;
            List<String> sections = new ArrayList<>();

            if (q.getQuestion().contains("{")) {
                questionText = questionId + ". " + q.getQuestion().substring(0, q.getQuestion().indexOf("{") - 1);

                String sectionsText = q.getQuestion().substring(
                        q.getQuestion().indexOf("{") + 1,
                        q.getQuestion().length() - 1
                );

                sections = Arrays.asList(sectionsText.split("; "));
            } else {
                questionText = questionId + ". " + q.getQuestion();
                sections.add("");
            }

            String answerText;
            String sectionText;

            List<Answer> currentAnswers = this.model.getQuestionnaireAnswers()
                    .stream()
                    .filter(a -> a.getQuestionId() == questionId)
                    .collect(Collectors.toList());

            if (!currentAnswers.isEmpty()) {
                for (int i = 0; i < currentAnswers.size(); i++) {
                    answerText = "\nA: " + currentAnswers.get(i).getValue();
                    sectionText = sections.get(i).isEmpty() ?
                            "" :
                            "\n" + sections.get(i);

                    TextBox textBox = new TextBox(requireActivity());

                    textBox.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                    textBox.setTextSize((int) (this.sizeInDp / 3.5));
                    textBox.setLayoutParams(layoutParams);
                    textBox.setText(questionText + sectionText + answerText);

                    layout.addView(textBox);
                }
            }
        }
    }

    private void addUserId(LinearLayout layout, LinearLayout.LayoutParams layoutParams) {
        TextBox textBox = new TextBox(requireActivity());

        textBox.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        textBox.setTextSize((int) (this.sizeInDp / 3.5));
        textBox.setLayoutParams(layoutParams);
        textBox.setText(getResources().getString(R.string.user_id_question) + "\n" + "A: " + this.model.getUserId());

        layout.addView(textBox);
    }

    public void loadPreviousScreen(View view) {
        NavHostFragment.findNavController(this).navigate(R.id.exitSummaryAction);
    }

    public void storeAnswers(View view) {
        this.db.answerDao()
                .insert(this.model.getQuestionnaireAnswers())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            for (int i : this.model.getQuestionnaireIds()) {
                                if (i != 6) {
                                    new RemoteDatabaseTransferService().execute(
                                            this.model.getUserId(),
                                            Integer.toString(i),
                                            new Gson().toJson(this.model.getQuestionnaireAnswers(i))
                                    );
                                }
                            }

                            this.storeUserId();
                        },
                        Throwable::printStackTrace
                );
    }

    private void storeUserId() {
        Configuration userIdConfiguration = new Configuration("userId", this.model.getUserId());

        this.db.configurationDao()
                .insert(Collections.singletonList(userIdConfiguration))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> new InitialSettingsService(this, this.db).getSettings(),
                        Throwable::printStackTrace
                );
    }
}