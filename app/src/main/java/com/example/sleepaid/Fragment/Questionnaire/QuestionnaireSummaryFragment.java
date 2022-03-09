package com.example.sleepaid.Fragment.Questionnaire;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.sleepaid.Database.Answer.Answer;
import com.example.sleepaid.App;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Service.InitialSettingsService;
import com.example.sleepaid.Database.Option.Option;
import com.example.sleepaid.Database.Question.Question;
import com.example.sleepaid.R;
import com.example.sleepaid.Model.SharedViewModel;
import com.example.sleepaid.Component.TextBox;

import java.util.Optional;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@SuppressLint("NewApi")
public class QuestionnaireSummaryFragment extends Fragment {
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
        return inflater.inflate(R.layout.fragment_questionnaire_summary, container, false);
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

        Button backButton = getView().findViewById(R.id.backButton);
        backButton.setOnClickListener(this::loadPreviousScreen);

        Button finishButton = getView().findViewById(R.id.finishButton);
        finishButton.setOnClickListener(this::storeAnswers);

        loadAllAnswers();
    }

    private void loadAllAnswers() {
        LinearLayout layout = getView().findViewById(R.id.answers);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, sizeInDp / 2);

        for (Question q : model.getQuestions()) {
            TextBox textBox = new TextBox(context);

            textBox.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
            textBox.setTextSize((int) (sizeInDp / 3.5));
            textBox.setLayoutParams(layoutParams);

            int questionId = q.getId();

            String questionText = questionId + ". " + q.getQuestion();
            String answerText;

            Optional<Answer> currentAnswer = model.getAnswers()
                    .stream()
                    .filter(a -> a.getQuestionId() == questionId)
                    .findAny();

            if (currentAnswer.isPresent()) {
                Optional<Option> option = model.getOptions()
                        .stream()
                        .filter(o -> o.getId() == currentAnswer.get().getOptionId())
                        .findAny();

                answerText = "A: " + option.get().getValue();
            }
            else {
                answerText = "No answer";
            }

            textBox.setText(questionText + "\n" + answerText);

            layout.addView(textBox);
        }
    }

    public void loadPreviousScreen(View view) {
        NavHostFragment.findNavController(this).navigate(R.id.exitSummaryAction);
    }

    public void storeAnswers(View view) {
        db.answerDao()
                .insert(model.getAnswers())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> new InitialSettingsService(this, db).getSettings(),
                        Throwable::printStackTrace
                );
    }
}