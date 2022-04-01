package com.example.sleepaid.Fragment.Questionnaire;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sleepaid.Component.EditTextAnswerComponent;
import com.example.sleepaid.Component.Modal;
import com.example.sleepaid.Fragment.QuestionnaireFragment;
import com.example.sleepaid.Model.SharedViewModel;
import com.example.sleepaid.R;
import com.example.sleepaid.Service.ValidationService;

public class UserIdFragment extends QuestionnaireFragment implements View.OnClickListener {
    private SharedViewModel model;
    private View view;

    private EditTextAnswerComponent answerComponent;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                exitUserId();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_id, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        this.view = view;

        this.answerComponent = this.view.findViewById(R.id.userIdAnswer);
        this.answerComponent.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                answerComponent.setError(null);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) { }
        });

        Button backButton = view.findViewById(R.id.backButton);
        backButton.setOnClickListener(this);

        Button nextButton = view.findViewById(R.id.nextButton);
        nextButton.setOnClickListener(this);
    }

    public void onClick(View view) {
        if (view.getId() == R.id.backButton) {
            this.exitUserId();
        } else if (view.getId() == R.id.nextButton) {
            this.startQuestionnaire();
        }
    }

    private void exitUserId() {
        Fragment fragment = this;

        DialogInterface.OnClickListener exitAction = (dialog, whichButton) -> NavHostFragment.findNavController(fragment).navigate(R.id.exitUserIdAction);

        DialogInterface.OnClickListener cancelAction = (dialog, whichButton) -> {};

        Modal.show(
                requireActivity(),
                getString(R.string.exit_user_id),
                getString(R.string.yes_modal),
                exitAction,
                getString(R.string.cancel_modal),
                cancelAction
        );
    }

    private void startQuestionnaire() {
        boolean isValid = ValidationService.validateEditText(this.answerComponent, false, null, "Please enter a value.", false);

        if (isValid) {
            String userId = this.answerComponent.getText().toString();
            this.model.setUserId(userId);

            for (int i : this.model.getQuestionnaireIds()) {
                this.model.setQuestionnaireAnswers(i, null);
            }

            this.model.setCurrentQuestionId(1);

            NavHostFragment.findNavController(this).navigate(R.id.startQuestionnairesAction);
        }
    }
}