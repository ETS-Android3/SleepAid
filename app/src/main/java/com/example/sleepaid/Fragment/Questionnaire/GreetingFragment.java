package com.example.sleepaid.Fragment.Questionnaire;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sleepaid.Fragment.QuestionnaireFragment;
import com.example.sleepaid.Model.SharedViewModel;
import com.example.sleepaid.R;

public class GreetingFragment extends QuestionnaireFragment implements View.OnClickListener {
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
        super.onViewCreated(view, savedInstanceState);

        this.model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        Button startButton = view.findViewById(R.id.startButton);
        startButton.setOnClickListener(this);
    }

    public void onClick(View view) {
        this.model.setUserId(null);

        NavHostFragment.findNavController(this).navigate(R.id.startUserIdAction);
    }
}