package com.example.sleepaid.Fragment.Questionnaire;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sleepaid.Model.SharedViewModel;
import com.example.sleepaid.R;

public class GreetingFragment extends Fragment implements View.OnClickListener {
    private SharedViewModel model;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_greeting, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        this.model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        Button startButton = view.findViewById(R.id.startButton);
        startButton.setOnClickListener(this);
    }

    public void onClick(View view) {
        this.model.setQuestionnaireAnswers(6, null);
        this.model.setCurrentQuestionId(1);

        NavHostFragment.findNavController(this).navigate(R.id.startQuestionnaireAction);
    }
}