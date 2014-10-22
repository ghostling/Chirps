package cs121.jam.chirps;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import java.util.Calendar;

/**
 * Created by maiho on 10/17/14.
 */
public class TimePickerDialogFragment extends DialogFragment {

    private TimePickerDialog.OnTimeSetListener mTimeSetListener;

    public TimePickerDialogFragment() {
        // nothing to see here, move along
    }

    public TimePickerDialogFragment(TimePickerDialog.OnTimeSetListener callback) {
        mTimeSetListener = (TimePickerDialog.OnTimeSetListener) callback;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar cal = Calendar.getInstance();

        return new TimePickerDialog(getActivity(),
                mTimeSetListener, cal.get(Calendar.HOUR),
                cal.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(getActivity()));
    }
}
