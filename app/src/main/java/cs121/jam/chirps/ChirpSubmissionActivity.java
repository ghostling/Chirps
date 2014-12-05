package cs121.jam.chirps;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
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
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.parse.ParseUser;
import org.json.JSONArray;
import java.util.ArrayList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cs121.jam.model.Chirp;

/**
 * Created by maiho. Modified by alexputman.
 *
 * Activity for a user to submit a chirp to the application.
 */
public class ChirpSubmissionActivity extends FragmentActivity implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener, View.OnClickListener {
    // All views from Chirp Submission Page
    EditText chirpTitleView;
    EditText chirpContactView;
    EditText chirpDescriptionView;
    Button submitChirpButtonView;
    TextView chirpCategoriesTextView;
    ArrayList<String> chirpCategoriesArray;

    // Chirp to be submitted.
    Chirp chirp;

    // Checkboxes for school selection.
    CheckBox college_pmcCheckBox;
    CheckBox college_hmcCheckBox;
    CheckBox college_scCheckBox;
    CheckBox college_cmcCheckBox;
    CheckBox college_pzcCheckBox;
    ArrayList<CheckBox> collegesCheckBoxes;

    // Expiration date and time dialogs.
    TextView chirpExpirationDateView;
    TextView chirpExpirationTimeView;
    TextView chirpExpirationTextView; // This is so the error message is on a line that fits.
    Date currentDateAndTime;

    // Formatting for the expiration date and time.
    public static SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("MM-dd-yyyy");
    public static SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("hh:mm a");
    public static SimpleDateFormat DATE_AND_TIME_FORMATTER = new SimpleDateFormat("MM-dd-yyyy hh:mm a");

    // For generating keywords - pre-compile the regex for optimization.
    public static final Pattern UNDESIRABLES = Pattern.compile("[\\Q][(){},.;!?<>%\\E]");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chirp_submission);

        // Connect views to values in xml file using their id's
        initializeValues();

        // Add inline data validation for chirp submission fields.
        addInlineChirpValidation();


        // Set up listener for the categories selection dialog to pop up.
        chirpCategoriesTextView.setOnClickListener(this);

        // Sign up Button Click Listener
        submitChirpButtonView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Retrieve the text entered from the EditText
                String chirpTitle = chirpTitleView.getText().toString();
                String chirpContact = chirpContactView.getText().toString();
                String chirpExpirationDate = chirpExpirationDateView.getText().toString();
                String chirpExpirationTime = chirpExpirationTimeView.getText().toString();
                String chirpDescription = chirpDescriptionView.getText().toString();
                JSONArray chirpSchools = new JSONArray();
                JSONArray chirpCategories = new JSONArray();

                // Collect all the colleges submitted
                for(CheckBox collegeCheckBox: collegesCheckBoxes) {
                    if (collegeCheckBox.isChecked())
                        chirpSchools.put(collegeCheckBox.getText().toString());
                }

                // Collect all the categories
                for(String cat : chirpCategoriesArray)
                        chirpCategories.put(cat);

                // Parse the date and time from the date and time pickers.
                String dateAndTime = chirpExpirationDate + " " + chirpExpirationTime;
                Date expirationDate = parseDate(dateAndTime);

                // If all the fields have valid values, go ahead and save to Parse.
                if (titleValidation() && contactValidation() && expirationDateValidation()
                        && descriptionValidation()) {
                    ParseUser currentUser = ParseUser.getCurrentUser();

                    // Check if the user is email verified. If not, reject the chirp.
                    Boolean emailVerification = (Boolean) currentUser.get("emailVerified");
                    if(emailVerification == null || !emailVerification)
                    {
                        Toast.makeText(getApplicationContext(),
                                "Email verification needed before submitting Chirps.",
                                Toast.LENGTH_LONG).show();
                    }
                    else {
                        saveChirp(chirpTitle, chirpContact, expirationDate, chirpDescription,
                                chirpSchools, chirpCategories, currentUser);

                        Toast.makeText(getApplicationContext(),
                                "Successfully submitted chirp, " +
                                        "an admin will approve or reject it shortly.",
                                Toast.LENGTH_LONG).show();
                        finish();
                    }
                }
            }
        });
    }

    /**
     * Sets up the default date and time and also the contact email.
     *
     * The default date is exactly a week from the date of submission.
     * The contact email is default the user's email.
     */
    public void setUpDefaultValues() {
        // Setup default date and time.
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, 7);
        chirpExpirationDateView.setText(DATE_FORMATTER.format(cal.getTime()));
        chirpExpirationTimeView.setText(TIME_FORMATTER.format(cal.getTime()));

        // Setup default contact email to be the chirp poster's email.
        String posterEmail = (ParseUser.getCurrentUser()).getEmail();
        chirpContactView.setText(posterEmail);
    }

    /**
     * Adds the checkboxes to our array of checkboxes.
     */
    public void initializeCollegeCheckboxes() {
        // Add them to our array of checkboxes.
        collegesCheckBoxes = new ArrayList<CheckBox>();
        collegesCheckBoxes.add(college_pmcCheckBox);
        collegesCheckBoxes.add(college_hmcCheckBox);
        collegesCheckBoxes.add(college_scCheckBox);
        collegesCheckBoxes.add(college_cmcCheckBox);
        collegesCheckBoxes.add(college_pzcCheckBox);
    }

    /**
     * Initializes needed values on creation.
     */
    public void initializeValues() {
        // Instantiate the chirp to be submitted.
        chirp = new Chirp();

        // Update the current date and time.
        currentDateAndTime = new Date();

        // Connect xml elements.
        chirpTitleView = (EditText) findViewById(R.id.chirp_title);
        chirpContactView = (EditText) findViewById(R.id.chirp_contact);
        chirpExpirationDateView = (TextView) findViewById(R.id.submit_chirp_expiration_date);
        chirpExpirationTimeView = (TextView) findViewById(R.id.submit_chirp_expiration_time);
        chirpExpirationTextView = (TextView) findViewById(R.id.submit_chirp_expiration_text);
        chirpDescriptionView = (EditText) findViewById(R.id.chirp_description);
        submitChirpButtonView = (Button) findViewById(R.id.submit_chirp_button);
        chirpCategoriesTextView = (TextView) findViewById(R.id.string_of_chirp_categories);
        college_pmcCheckBox = (CheckBox) findViewById(R.id.school_checkbox_pmc);
        college_hmcCheckBox = (CheckBox) findViewById(R.id.school_checkbox_hmc);
        college_scCheckBox = (CheckBox) findViewById(R.id.school_checkbox_sc);
        college_cmcCheckBox = (CheckBox) findViewById(R.id.school_checkbox_cmc);
        college_pzcCheckBox = (CheckBox) findViewById(R.id.school_checkbox_pzc);

        initializeCollegeCheckboxes();

        // For our categories.
        chirpCategoriesArray = new ArrayList<String>();

        setUpDefaultValues();
    }

    /**
     * Changes the chirp to be submitted.
     *
     * The main purpose of this function is for testing. By having this method, I can inject
     * mock objects for chirps.
     *
     * @param newChirp Chirp to replace the old chirp.
     */
    public void setChirp(Chirp newChirp) {
        chirp = newChirp;
    }

    /**
     * Saves the chirp with the given information to Parse.
     *
     * @param title Title of the chirp.
     * @param contact Contact email for the chirp.
     * @param expirationDate Expiration date for the chirp.
     * @param description A description of the purpose of the chirp.
     * @param schools The relevant schools to show the chirp to.
     * @param categories The relevant categories for the chirp.
     * @param user The user that posted the chirp.
     */
    public void saveChirp(String title, String contact, Date expirationDate,
                          String description, JSONArray schools, JSONArray categories,
                          ParseUser user) {
        chirp.setTitle(title);
        chirp.setContactEmail(contact);
        chirp.setExpirationDate(expirationDate);
        chirp.setDescription(description);
        chirp.setSchools(schools);
        chirp.setCategories(categories);
        chirp.setKeywords(generateKeywords(title, description));
        chirp.setUser(user);
        chirp.rejectChirp(); // All chirps are default not approved.
        chirp.saveWithPermissions();
    }

    /**
     * Generates the relevant keywords for searching.
     *
     * @param title The title of the chirp.
     * @param description The description of the chirp.
     * @return A JSONArray of the generated keywords.
     */
    public JSONArray generateKeywords(String title, String description) {
        // Gets rid of all unwanted characters like punctuation.
        String titleAndDescription = title + " " + description;
        titleAndDescription = UNDESIRABLES.matcher(titleAndDescription).replaceAll("");

        String[] allWords = (titleAndDescription.toLowerCase()).trim().split("\\s+");
        JSONArray keywords = new JSONArray();
        String[] stopWords = {"the", "a", "in", "and"};

        // Filter out the stop words.
        for (String word : allWords) {
            if (!Arrays.asList(stopWords).contains(word)) {
                keywords.put(word);
            }
        }

        return keywords;
    }

    /**
     * Checks if the title field has been filled.
     */
    public boolean titleValidation() {
        if (chirpTitleView.getText().length() == 0) {
            chirpTitleView.setError("Title is a required field.");
            return false;
        } else {
            chirpTitleView.setError(null);
        }
        return true;
    }

    /**
     * Checks if the contact field is filled and also contains a valid email.
     */
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

    /**
     * Checks that the expiration date entered is in the future.
     */
    public boolean expirationDateValidation() {
        String chirpExpirationDate = chirpExpirationDateView.getText().toString();
        String chirpExpirationTime = chirpExpirationTimeView.getText().toString();
        String dateAndTime = chirpExpirationDate + " " + chirpExpirationTime;
        Date expirationDate = parseDate(dateAndTime);

        // If the expiration date is not in the future, then it's not valid!
        if (expirationDate.before(currentDateAndTime)) {
            chirpExpirationTextView.requestFocus();
            chirpExpirationTextView.setError("Please set a expiration date in the future.");
            return false;
        } else {
            chirpExpirationTextView.setError(null);
        }

        return true;
    }

    /**
     * Checks that there is a description.
     */
    public boolean descriptionValidation() {
        if (chirpDescriptionView.getText().length() == 0) {
            chirpDescriptionView.setError("Description is a required field.");
            return false;
        } else {
            chirpTitleView.setError(null);
        }

        return true;
    }

    /**
     * Sets up inline data validation using text watchers.
     */
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

    /**
     * Parses the string according to the DATE_AND_TIME_FORMATTER.
     * @param dateStr String representing a date.
     * @return The equivalent Date object.
     */
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

    /**
     * Checks if the email is valid.
     * @param email Email to be verified.
     * @return A boolean indicating if it's valid.
     */
    public boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * Creates the date picker dialog.
     * @param v
     */
    public void showDatePickerDialog(View v) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment newFragment = new DatePickerDialogFragment(ChirpSubmissionActivity.this);
        newFragment.show(ft, "date_dialog");
    }

    /**
     * Creates the time picker dialog.
     * @param v
     */
    public void showTimePickerDialog(View v) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        DialogFragment newFragment = new TimePickerDialogFragment(ChirpSubmissionActivity.this);
        newFragment.show(ft, "time_dialog");
    }

    /**
     * Sets the date when the user chooses a date in the date picker dialog.
     * @param view
     * @param year
     * @param monthOfYear
     * @param dayOfMonth
     */
    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear,
                          int dayOfMonth) {
        Calendar cal = new GregorianCalendar(year, monthOfYear, dayOfMonth);
        chirpExpirationDateView.setText(DATE_FORMATTER.format(cal.getTime()));
        Log.e("Expiration date is ", DATE_FORMATTER.format(cal.getTime()));
    }

    /**
     * Sets the time when the user chooses a time in the time picker dialog.
     * @param view
     * @param hour
     * @param minute
     */
    @Override
    public void onTimeSet(TimePicker view, int hour, int minute) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
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
        if (id == R.id.action_logout) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            currentUser.logOut();
            Intent intent = new Intent(ChirpSubmissionActivity.this,
                    LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.user_profile) {
            Intent intent = new Intent(ChirpSubmissionActivity.this,
                    UserProfileActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    ArrayList mSelectedCategories;

    @Override
    public void onClick(View view) {
        ChooseCategoriesFragment catFrag = new ChooseCategoriesFragment();
        catFrag.show(getFragmentManager(), "Category");
    }

    /**
     * DialogFragment for category selection, which is a multiselection dialog.
     */
    @SuppressLint("ValidFragment")
    public class ChooseCategoriesFragment extends DialogFragment {
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            if(mSelectedCategories == null)
                mSelectedCategories = new ArrayList<String>();  // Where we track the selected items

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            // Extract the selected categories from the view.
            String[] categories = getResources().getStringArray(R.array.categories_array);
            final boolean[] catList = new boolean[categories.length];
            for(int i = 0; i < categories.length; i++) {
                if(mSelectedCategories.contains(categories[i])) {
                    catList[i] = true;
                }
            }

            // Set the dialog title
            builder.setTitle(R.string.set_chirp_categories_title)
                    // Specify the list array, the items to be selected by default (null for none),
                    // and the listener through which to receive callbacks when items are selected
                    .setMultiChoiceItems(R.array.categories_array, catList,
                            new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which,
                                                    boolean isChecked) {
                                    if (isChecked) {
                                        // Limit the category selection to at most 3.
                                        if(mSelectedCategories.size() >= 3) {
                                            // Don't let them select any more
                                            ((AlertDialog) dialog).getListView().
                                                    setItemChecked(which, false);
                                            Toast.makeText(getApplicationContext(),
                                                    "No more than 3 categories allowed for a " +
                                                            "Chirp.",
                                                    Toast.LENGTH_LONG).show();
                                            catList[which] = false;
                                        }
                                        else {
                                            // If the user checked the item, add it to the selected
                                            // items
                                            mSelectedCategories.add(getResources().
                                                    getStringArray(R.array.categories_array)[which]);
                                            catList[which] = true;
                                        }
                                    } else{
                                        // Else, if the item is already in the array, remove it
                                        mSelectedCategories.remove(mSelectedCategories.
                                                indexOf(getResources().
                                                        getStringArray(R.array.categories_array)
                                                        [which]));
                                        catList[which] = false;
                                    }
                                }
                            })
                            // Set the action buttons
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK, so save the mSelectedCategories results somewhere
                            // or return them to the component that opened the dialog
                            chirpCategoriesArray = mSelectedCategories;
                            StringBuilder categoriesText = new StringBuilder("");

                            boolean first = true;
                            for (String cat : chirpCategoriesArray) {
                                if (!first) {
                                    categoriesText = categoriesText.append(", ");
                                } else {
                                    first = false;
                                }
                                categoriesText = categoriesText.append(cat);
                            }

                            if (categoriesText.toString().equals(""))
                                chirpCategoriesTextView.setText(R.string.select_categories_text);
                            else
                                chirpCategoriesTextView.setText(categoriesText.toString());
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

            return builder.create();
        }
    }
}
