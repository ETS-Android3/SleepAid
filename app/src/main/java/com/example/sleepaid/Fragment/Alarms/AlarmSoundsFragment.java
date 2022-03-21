package com.example.sleepaid.Fragment.Alarms;

import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.sleepaid.App;
import com.example.sleepaid.Component.Modal;
import com.example.sleepaid.Handler.DataHandler;
import com.example.sleepaid.Model.SharedViewModel;
import com.example.sleepaid.R;

import java.util.HashMap;


public class AlarmSoundsFragment extends Fragment implements View.OnClickListener {
    private SharedViewModel model;

    private RadioGroup radioGroup;

    private MediaPlayer mediaPlayer;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                AppCompatRadioButton selectedSoundOption = getView().findViewById(radioGroup.getCheckedRadioButtonId());
                String selectedSound = (String) selectedSoundOption.getText();

                if (!model.getSelectedConfiguration().getSound().equals(selectedSound)) {
                    exitAlarmSound();
                } else {
                    cancelAlarmSound();
                }
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_alarm_sounds, container, false);
    }

    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        this.model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        Button cancelAlarmSoundButton = view.findViewById(R.id.cancelAlarmSoundButton);
        cancelAlarmSoundButton.setOnClickListener(this);

        Button saveAlarmSoundButton = view.findViewById(R.id.saveAlarmSoundButton);
        saveAlarmSoundButton.setOnClickListener(this);

        this.loadSounds();
    }

    private void exitAlarmSound() {
        if (this.mediaPlayer != null && this.mediaPlayer.isPlaying()) {
            this.mediaPlayer.stop();
        }

        DialogInterface.OnClickListener discardAction = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                cancelAlarmSound();
            }
        };

        DialogInterface.OnClickListener saveAction = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                saveAlarmSound();
            }
        };

        Modal.show(
                requireActivity(),
                getString(R.string.exit_alarm_sound),
                getString(R.string.save_modal),
                saveAction,
                getString(R.string.discard_modal),
                discardAction
        );
    }

    private void cancelAlarmSound() {
        NavHostFragment.findNavController(this).navigate(R.id.exitAlarmSoundAction);
    }

    private void saveAlarmSound() {
        AppCompatRadioButton selectedSound = getView().findViewById(this.radioGroup.getCheckedRadioButtonId());
        this.model.getSelectedConfiguration().setSound((String) selectedSound.getText());

        NavHostFragment.findNavController(this).navigate(R.id.exitAlarmSoundAction);
    }

    private void loadSounds() {
        String currentAlarmSound = this.model.getSelectedConfiguration().getSound();

        int sizeInDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                25,
                getResources().getDisplayMetrics()
        );

        Context contextThemeWrapper = new ContextThemeWrapper(App.getContext(), R.style.RadioButton_Transparent);

        this.radioGroup = getView().findViewById(R.id.alarmSoundsRadioGroup);
        this.radioGroup.clearCheck();
        this.radioGroup.removeAllViews();

        HashMap<String, Integer> alarmSounds = App.getSounds();

        for (String s : alarmSounds.keySet()) {
            AppCompatRadioButton optionBox = new AppCompatRadioButton(contextThemeWrapper, null, R.style.RadioButton_White);

            optionBox.setId(alarmSounds.get(s));
            optionBox.setText(s);
            optionBox.setTextSize((int) (sizeInDp / 3.5));
            optionBox.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

            RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.MATCH_PARENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, sizeInDp);
            optionBox.setLayoutParams(layoutParams);

            optionBox.setPadding(
                    sizeInDp / 2,
                    sizeInDp / 2,
                    sizeInDp / 2,
                    sizeInDp / 2
            );

            optionBox.setOnClickListener(playSound);

            if (s.equals(currentAlarmSound)) {
                optionBox.setChecked(true);
            }

            this.radioGroup.addView(optionBox);
        }
    }

    public void onClick(View view) {
        if (this.mediaPlayer != null && mediaPlayer.isPlaying()) {
            this.mediaPlayer.stop();
        }

        if (view.getId() == R.id.saveAlarmSoundButton) {
            this.saveAlarmSound();
        } else if (view.getId() == R.id.cancelAlarmSoundButton) {
            this.cancelAlarmSound();
        }
    }

    private View.OnClickListener playSound = new View.OnClickListener() {
        public void onClick(View view) {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }

            mediaPlayer = new MediaPlayer();
            DataHandler.playAlarmSound(mediaPlayer, App.getContext(), view.getId(), false);
        }
    };
}
