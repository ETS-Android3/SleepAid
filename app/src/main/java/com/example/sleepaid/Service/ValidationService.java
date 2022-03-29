package com.example.sleepaid.Service;

import android.text.InputType;

import com.example.sleepaid.App;
import com.example.sleepaid.Component.EditTextAnswerComponent;
import com.example.sleepaid.Component.RadioGroupAnswerComponent;
import com.example.sleepaid.Handler.DataHandler;
import com.example.sleepaid.R;

import java.util.List;

public class ValidationService {
    public static boolean validateEditText(EditTextAnswerComponent component,
                                    boolean hasParent,
                                    EditTextAnswerComponent parent,
                                    String emptyError,
                                    boolean hasErrors) {
        boolean isEmptyAndHasError = component.getText().toString().trim().isEmpty() && emptyError != null;

        boolean isEmptyAndHasErrorAndParentIsNotNoneOrEmpty = false;

        if (hasParent) {
             isEmptyAndHasErrorAndParentIsNotNoneOrEmpty = component.getText().toString().trim().isEmpty()
                && emptyError != null
                && !parent.getText().toString().trim().equalsIgnoreCase("none")
                && !parent.getText().toString().trim().isEmpty();
        }

        if((!hasParent && isEmptyAndHasError) || (hasParent && isEmptyAndHasErrorAndParentIsNotNoneOrEmpty)) {
            component.setError(emptyError);

            // If this is the first error we encounter redirect the user to it
            if (!hasErrors) {
                component.requestFocus();
            }

            return false;
        } else if (!isEmptyAndHasError && component.getInputType() == (InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_TIME)) {
            List<Integer> times = DataHandler.getIntsFromString(component.getText().toString());

            boolean isHourValid = times.size() == 2 && times.get(0) >= 0 && times.get(0) <= 23;
            boolean isMinuteValid = times.size() == 2 && times.get(1) >= 0 && times.get(1) <= 59;

            if (!isHourValid && isMinuteValid) {
                component.setError(App.getContext().getString(R.string.hour_validation));

                if (!hasErrors) {
                    component.requestFocus();
                }

                return false;
            }

            if (isHourValid && !isMinuteValid) {
                component.setError(App.getContext().getString(R.string.minute_validation));

                if (!hasErrors) {
                    component.requestFocus();
                }

                return false;
            }

            if (!isHourValid && !isMinuteValid) {
                component.setError(App.getContext().getString(R.string.time_validation));

                if (!hasErrors) {
                    component.requestFocus();
                }

                return false;
            }
        }

        return true;
    }

    public static boolean validateRadioGroup(RadioGroupAnswerComponent component, boolean hasErrors) {
        if (component.getCheckedRadioButtonId() == -1) {
            component.setError(App.getContext().getString(R.string.radio_group_validation));

            if (!hasErrors) {
                component.requestFocus();
            }

            return false;
        }

        return true;
    }
}
