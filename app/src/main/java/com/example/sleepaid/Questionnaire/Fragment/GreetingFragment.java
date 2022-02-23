package com.example.sleepaid.Questionnaire.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sleepaid.R;
import com.example.sleepaid.SharedViewModel;

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
        model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        Button startButton = view.findViewById(R.id.startButton);
        startButton.setOnClickListener(this);
    }

    public void onClick(View view) {
        model.setAnswers(null);
        model.setCurrentQuestionId(1);

        NavHostFragment.findNavController(this).navigate(R.id.startQuestionnaireAction);
    }
}