package com.example.sleepaid.Fragment;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.sleepaid.Listener.OnSwipeTouchListener;
import com.example.sleepaid.R;

public class QuestionnaireFragment extends Fragment {
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        view.findViewById(R.id.scrollView).setOnTouchListener(new OnSwipeTouchListener(requireActivity()) {
            @Override
            public void onSwipeLeft() {
                super.onSwipeLeft();
                if (view.findViewById(R.id.nextButton) != null) {
                    view.findViewById(R.id.nextButton).performClick();
                } else if (view.findViewById(R.id.startButton) != null) {
                    view.findViewById(R.id.startButton).performClick();
                } else if (view.findViewById(R.id.finishButton) != null) {
                    view.findViewById(R.id.finishButton).performClick();
                }
            }

            @Override
            public void onSwipeRight() {
                super.onSwipeRight();
                if (view.findViewById(R.id.backButton) != null) {
                    view.findViewById(R.id.backButton).performClick();
                }
            }
        });
    }
}
