package com.example.dionicio.appcotizacion.Clases;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment {

    private TimePickerDialog.OnTimeSetListener listener;

    public static TimePickerFragment newInstance(TimePickerDialog.OnTimeSetListener listener) {
        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setListener(listener);

        return fragment;
    }

    public void setListener(TimePickerDialog.OnTimeSetListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();

        int hour = c.get(Calendar.HOUR);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of DatePickerDialog and return it
        return new TimePickerDialog(getActivity(), listener, hour, minute, false);
    }

}