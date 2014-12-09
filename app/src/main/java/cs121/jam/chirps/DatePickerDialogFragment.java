package cs121.jam.chirps;

import java.util.Calendar;

import android.app.DialogFragment;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;

/**
 * Created by maiho.
 *
 * Dialog for selecting a date.
 */
public class DatePickerDialogFragment extends DialogFragment {
    private OnDateSetListener mDateSetListener;

    /**
     * Needed to extend the DialogFragment.
     */
    public DatePickerDialogFragment() {
        // nothing to see here, move along
    }

    /**
     * Setting up the DialogFragment to connect to the callback.
     * @param callback
     */
    public DatePickerDialogFragment(OnDateSetListener callback) {
        mDateSetListener = (OnDateSetListener) callback;
    }

    public void setDateSetListener(OnDateSetListener callback) {
        mDateSetListener = (OnDateSetListener) callback;
    }

    /**
     * Creates the actual dialog displayed.
     * @param savedInstanceState
     * @return
     */
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Default date is 7 days from the current day.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 7);

        return new DatePickerDialog(getActivity(),
                mDateSetListener, cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

}
