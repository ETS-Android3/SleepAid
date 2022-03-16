package com.example.sleepaid.Fragment.Alarms;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.sleepaid.Adapter.AlarmAdapter;
import com.example.sleepaid.App;
import com.example.sleepaid.Database.Alarm.Alarm;
import com.example.sleepaid.Database.AppDatabase;
import com.example.sleepaid.Listener.ListMultiChoiceModeListener;
import com.example.sleepaid.Model.SharedViewModel;
import com.example.sleepaid.R;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@SuppressLint("NewApi")
public class AlarmSoundsFragment extends PreferenceFragmentCompat {
    private SharedViewModel model;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        this.model = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        setPreferencesFromResource(R.xml.preferences, rootKey);

        Preference selectSoundButton = getPreferenceManager().findPreference("alarmPref");

        if (selectSoundButton != null) {
            selectSoundButton.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    MediaPlayer mediaPlayer = MediaPlayer.create(App.getContext(), model.getSound(value.toString().toLowerCase()));
                    mediaPlayer.setLooping(false);
                    mediaPlayer.start();

                    return true;
                }
            });
        }
    }
}
