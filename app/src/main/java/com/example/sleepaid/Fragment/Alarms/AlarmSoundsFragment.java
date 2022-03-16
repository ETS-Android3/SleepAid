package com.example.sleepaid.Fragment.Alarms;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.sleepaid.Adapter.AlarmAdapter;
import com.example.sleepaid.App;
import com.example.sleepaid.Database.Alarm.Alarm;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Database.Option.Option;
import com.example.sleepaid.Listener.ListMultiChoiceModeListener;
import com.example.sleepaid.Model.SharedViewModel;
import com.example.sleepaid.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@SuppressLint("NewApi")
public class AlarmSoundsFragment extends Fragment implements View.OnClickListener {
    private SharedViewModel model;

    private RadioGroup radioGroup;

    private MediaPlayer mediaPlayer;

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

    private void loadSounds() {
        String currentAlarmSound = this.model.getSelectedSound();

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
        if (this.mediaPlayer != null) {
            this.mediaPlayer.stop();
        }

        if (view.getId() == R.id.saveAlarmSoundButton) {
            AppCompatRadioButton selectedSound = getView().findViewById(this.radioGroup.getCheckedRadioButtonId());
            this.model.setSelectedSound((String) selectedSound.getText());

            NavHostFragment.findNavController(this).navigate(R.id.exitAlarmSoundAction);
        } else if (view.getId() == R.id.cancelAlarmSoundButton) {
            NavHostFragment.findNavController(this).navigate(R.id.exitAlarmSoundAction);
        }
    }

    private View.OnClickListener playSound = new View.OnClickListener() {
        public void onClick(View view) {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
            }

            mediaPlayer = MediaPlayer.create(App.getContext(), view.getId());
            mediaPlayer.setLooping(false);
            mediaPlayer.start();
        }
    };
}
