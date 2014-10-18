package cs121.jam.chirps;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.ParseUser;

import java.sql.Time;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cs121.jam.model.Chirp;

import static android.app.DatePickerDialog.OnDateSetListener;


public class ChirpSubmissionActivity extends FragmentActivity implements OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {
    // All views from Chirp Submission Page
    EditText chirpTitleView;
    EditText chirpContactView;
    EditText chirpDescriptionView;
    Button submitChirpButtonView;
    TextView chirpExpirationDateView;
    TextView chirpExpirationTimeView;
    TextView chirpExpirationTextView; // This is so we can put the error message on a line that fits.
    Date currentDateAndTime;

    SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("MM-dd-yyyy");
    SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("hh:mm a");
    SimpleDateFormat DATE_AND_TIME_FORMATTER = new SimpleDateFormat("MM-dd-yyyy hh:mm a");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chirp_submission);

        // Update the current date and time.
        currentDateAndTime = new Date();

        // Connect views to values in xml file using their id's
        chirpTitleView = (EditText) findViewById(R.id.chirp_title);
        chirpContactView = (EditText) findViewById(R.id.chirp_contact);
        chirpExpirationDateView = (TextView) findViewById(R.id.submit_chirp_expiration_date);
        chirpExpirationTimeView = (TextView) findViewById(R.id.submit_chirp_expiration_time);
        chirpExpirationTextView = (TextView) findViewById(R.id.submit_chirp_expiration_text);
        chirpDescriptionView = (EditText) findViewById(R.id.chirp_description);
        submitChirpButtonView = (Button) findViewById(R.id.submit_chirp_button);

        // Setup default date and time.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 7);
        chirpExpirationDateView.setText(DATE_FORMATTER.format(cal.getTime()));
        chirpExpirationTimeView.setText(TIME_FORMATTER.format(cal.getTime()));

        // Data validation for chirp submission fields.
        addInlineChirpValidation();

        // Sign up Button Click Listener
        submitChirpButtonView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Retrieve the text entered from the EditText
                String chirpTitle = chirpTitleView.getText().toString();
                String chirpContact = chirpContactView.getText().toString();
                String chirpExpirationDate = chirpExpirationDateView.getText().toString();
                String chirpExpirationTime = chirpExpirationTimeView.getText().toString();
                String chirpDescription = chirpDescriptionView.getText().toString();

                // Create Date object for the expiration date and time.
                // The dateAndTime is of the format "MM-dd-yyyy hh:mm a"
                String dateAndTime = chirpExpirationDate + " " + chirpExpirationTime;
                Date expirationDate = parseDate(dateAndTime);

                if (titleValidation() && contactValidation() && expirationDateValidation()
                        && descriptionValidation()) {
                    ParseUser currentUser = ParseUser.getCurrentUser();

                    // Save new chirp into Parse.com Data Storage
                    Chirp chirp = new Chirp();
                    chirp.setTitle(chirpTitle);
                    chirp.setContactEmail(chirpContact);
                    chirp.setExpirationDate(expirationDate);
                    chirp.setDescription(chirpDescription);
                    chirp.setUser(currentUser);
                    chirp.approveChirp(); // TODO: this is auto-approve; remove later.
                    chirp.saveWithPermissions();

                    // Tell the user that the chirp is submitted and take them back to the main activity
                    Toast.makeText(getApplicationContext(),
                            "Successfully submitted chirp, " +
                                    "an admin will approve or reject it shortly.",
                            Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(
                            ChirpSubmissionActivity.this,
                            MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    public boolean titleValidation() {
        if (chirpTitleView.getText().length() == 0) {
            chirpTitleView.setError("Title is a required field.");
            return false;
        } else {
            chirpTitleView.setError(null);
        }
        return true;
    }

    public boolean contactValidation() {
        if (chirpContactView.getText().length() == 0) {
            chirpContactView.setError("Contact is a required field.");
            return false;
        } else if (!isValidEmail(chirpContactView.getText().toString())) {
            chirpContactView.setError("Please enter a valid email.");
            return false;
        } else {
            chirpContactView.setError(null);
        }

        return true;
    }

    public boolean expirationDateValidation() {
        String chirpExpirationDate = chirpExpirationDateView.getText().toString();
        String chirpExpirationTime = chirpExpirationTimeView.getText().toString();
        String dateAndTime = chirpExpirationDate + " " + chirpExpirationTime;
        Date expirationDate = parseDate(dateAndTime);

        if (expirationDate.before(currentDateAndTime)) {
            chirpExpirationTextView.requestFocus();
            chirpExpirationTextView.setError("Please set a expiration date in the future.");
            return false;
        } else {
            chirpExpirationTextView.setError(null);
        }

        return true;
    }

    public boolean descriptionValidation() {
        if (chirpDescriptionView.getText().length() == 0) {
            chirpDescriptionView.setError("Description is a required field.");
            return false;
        } else {
            chirpTitleView.setError(null);
        }

        return true;
    }

    public void addInlineChirpValidation() {
        chirpTitleView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                // Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                // Do nothing.
            }

            public void afterTextChanged(Editable edt) {
                titleValidation();
            }
        });

        chirpContactView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                // Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                // Do nothing.
            }

            public void afterTextChanged(Editable edt) {
                contactValidation();
            }
        });

        chirpExpirationDateView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                // Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                // Do nothing.
            }

            public void afterTextChanged(Editable edt) {
                expirationDateValidation();
            }
        });

        chirpDescriptionView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                // Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                // Do nothing.
            }

            public void afterTextChanged(Editable edt) {
                descriptionValidation();
            }
        });
    }

    // Parses the string according to the DATE_AND_TIME_FORMATTER.
    public Date parseDate(String dateStr) {
        Date date = null;
        try {
            date = DATE_AND_TIME_FORMATTER.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("Parse Expiration Date Error", e.getMessage());
        }

        return date;
    }

    // Checking if an email is valid or not.
    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    public void showDatePickerDialog(View v) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment newFragment = new DatePickerDialogFragment(ChirpSubmissionActivity.this);
        newFragment.show(ft, "date_dialog");
    }

    public void showTimePickerDialog(View v) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment newFragment = new TimePickerDialogFragment(ChirpSubmissionActivity.this);
        newFragment.show(ft, "time_dialog");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {
        Calendar cal = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        chirpExpirationDateView.setText(DATE_FORMATTER.format(cal.getTime()));
        Log.e("Expiration date is ", DATE_FORMATTER.format(cal.getTime()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR, hour);
        cal.set(Calendar.MINUTE, minute);
        chirpExpirationTimeView.setText(TIME_FORMATTER.format(cal.getTime()));
        Log.i("Expiration time is ", TIME_FORMATTER.format(cal.getTime()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chirp_submission, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
