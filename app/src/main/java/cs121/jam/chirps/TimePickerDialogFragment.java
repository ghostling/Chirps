package cs121.jam.chirps;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.format.DateFormat;

import java.util.Calendar;

/**
 * Created by maiho on 10/17/14.
 *
 * DialogFragment for a user to select a specific time.
 */
public class TimePickerDialogFragment extends DialogFragment {

    private TimePickerDialog.OnTimeSetListener mTimeSetListener;

    /**
     * Needed to extend the DialogFragment.
     */
    public TimePickerDialogFragment() {
        // nothing to see here, move along
    }

    /**
     * Setting up the DialogFragment to connect to the callback.
     * @param callback
     */
    public TimePickerDialogFragment(TimePickerDialog.OnTimeSetListener callback) {
        mTimeSetListener = (TimePickerDialog.OnTimeSetListener) callback;
    }

    /**
     * Creates the TimePickerDialog.
     * @param savedInstanceState
     * @return
     */
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar cal = Calendar.getInstance();

        return new TimePickerDialog(getActivity(),
                mTimeSetListener, cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                DateFormat.is24HourFormat(getActivity()));
    }
}
