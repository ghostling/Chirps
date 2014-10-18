package cs121.jam.chirps;

import java.util.Calendar;

import android.app.DialogFragment;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.os.Bundle;

public class DatePickerDialogFragment extends DialogFragment {

    private OnDateSetListener mDateSetListener;

    public DatePickerDialogFragment() {
        // nothing to see here, move along
    }

    public DatePickerDialogFragment(OnDateSetListener callback) {
        mDateSetListener = (OnDateSetListener) callback;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 7);

        // Default date is 7 days from the current day.
        return new DatePickerDialog(getActivity(),
                mDateSetListener, cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
    }

}
