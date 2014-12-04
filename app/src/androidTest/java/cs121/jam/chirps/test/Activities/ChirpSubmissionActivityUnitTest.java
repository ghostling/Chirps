package cs121.jam.chirps.test.Activities;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;

import cs121.jam.chirps.ChirpSubmissionActivity;
import cs121.jam.chirps.R;
import cs121.jam.model.Chirp;

import static org.mockito.Mockito.*;

/**
 * Created by maiho on 12/2/14.
 */
public class ChirpSubmissionActivityUnitTest extends ActivityUnitTestCase<ChirpSubmissionActivity> {
    EditText chirpTitleView;
    EditText chirpContactView;
    EditText chirpDescriptionView;
    Button submitChirpButtonView;
    TextView chirpCategoriesTextView;
    ArrayList<String> chirpCategoriesArray;

    TextView chirpExpirationDateView;
    TextView chirpExpirationTimeView;
    TextView chirpExpirationTextView;

    public ChirpSubmissionActivityUnitTest() {
        super(ChirpSubmissionActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());
        ParseObject.registerSubclass(Chirp.class);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }


    /* Tests that keywords are correctly generated from two given strings.
     */
    @SmallTest
    public void testGenerateKeywords() throws JSONException {
        Intent intent = new Intent(getInstrumentation()
                .getTargetContext(), ChirpSubmissionActivity.class);
        startActivity(intent, null, null);

        String title = "hi ....!";
        String description = "No description for you.";
        JSONArray keywords = getActivity().generateKeywords(title, description);

        for(int n = 0; n < keywords.length(); n++)
        {
            // Check that the keywords generated don't contain the undesired characters.
            String keyword = keywords.getString(n);
            Matcher m = (ChirpSubmissionActivity.UNDESIRABLES).matcher(keyword);
            assertFalse(m.find());
        }
    }

    /* Tests that the description field is correctly validated.
     */
    @SmallTest
    public void testDescriptionValidation() {
        Intent intent = new Intent(getInstrumentation()
                .getTargetContext(), ChirpSubmissionActivity.class);
        startActivity(intent, null, null);

        // Connect views to values in xml file using their id's
        chirpDescriptionView = (EditText) getActivity().findViewById(R.id.chirp_description);
        chirpDescriptionView.setText("Description is here!");
        assertTrue(getActivity().descriptionValidation());

        // Empty title field should not be valid!
        chirpDescriptionView.setText("");
        assertFalse(getActivity().descriptionValidation());

        getActivity().finish();
    }

    /* Tests that the title field is correctly validated.
     */
    @SmallTest
    public void testTitleValidation() {
        Intent intent = new Intent(getInstrumentation()
                .getTargetContext(), ChirpSubmissionActivity.class);
        startActivity(intent, null, null);

        // Connect views to values in xml file using their id's
        chirpTitleView = (EditText) getActivity().findViewById(R.id.chirp_title);
        chirpTitleView.setText("Title is here!");

        assertTrue(getActivity().titleValidation());

        // Empty title field should not be valid!
        chirpTitleView.setText("");
        assertFalse(getActivity().titleValidation());
    }

    /* Tests that the contact field is correctly validated.
     */
    @SmallTest
    public void testContactValidation() {
        Intent intent = new Intent(getInstrumentation()
                .getTargetContext(), ChirpSubmissionActivity.class);
        startActivity(intent, null, null);
        // Connect views to values in xml file using their id's
        chirpContactView = (EditText) getActivity().findViewById(R.id.chirp_contact);

        // Valid email
        chirpContactView.setText("test@domain.edu");
        assertTrue(getActivity().contactValidation());

        // Empty email field
        chirpContactView.setText("");
        assertFalse(getActivity().contactValidation());

        // Invalid email
        chirpContactView.setText("test@domain");
        assertFalse(getActivity().contactValidation());
    }

    /* Tests that the given expiration date is correctly validated.
     */
    @SmallTest
    public void testExpirationDateValidation() {
        Intent intent = new Intent(getInstrumentation()
                .getTargetContext(), ChirpSubmissionActivity.class);
        startActivity(intent, null, null);

        // Connect views to values in xml file using their id's
        chirpExpirationDateView = (TextView) getActivity().findViewById(R.id.submit_chirp_expiration_date);
        chirpExpirationTimeView = (TextView) getActivity().findViewById(R.id.submit_chirp_expiration_time);

        // Give a valid date.
        Calendar cal = new GregorianCalendar(2050, 1, 1);
        chirpExpirationDateView.setText(getActivity().DATE_FORMATTER.format(cal.getTime()));
        assertTrue(getActivity().expirationDateValidation());

        // Give an invalid date.
        cal = new GregorianCalendar(1990, 1, 1);
        chirpExpirationDateView.setText(getActivity().DATE_FORMATTER.format(cal.getTime()));
        assertFalse(getActivity().expirationDateValidation());
    }

    /* Tests that the isValidEmail() method is correct.
     */
    @SmallTest
    public void testIsValidEmail() {
        Intent intent = new Intent(getInstrumentation()
                .getTargetContext(), ChirpSubmissionActivity.class);
        startActivity(intent, null, null);

        // Empty email should not be valid.
        String email = "";
        assertFalse(getActivity().isValidEmail(email));

        // Valid email
        email = "test@test.edu";
        assertTrue(getActivity().isValidEmail(email));

        // Invalid email
        email = "test";
        assertFalse(getActivity().isValidEmail(email));

        // Invalid email
        email = "test@domain";
        assertFalse(getActivity().isValidEmail(email));
    }

    /* You should only be able to get to the ChirpSubmissionActivity if you are logged in.
     */
    @SmallTest
    public void testIsLoggedIn() {
        Intent intent = new Intent(getInstrumentation()
                .getTargetContext(), ChirpSubmissionActivity.class);
        startActivity(intent, null, null);

        // Login fields are empty at this point.
        final Intent launchIntent = getStartedActivityIntent();
        if (launchIntent != null) {
            assertNotNull(ParseUser.getCurrentUser());
        }
    }

    /* You should only be able to get to the ChirpSubmissionActivity if you are logged in.
     */
    @MediumTest
    public void testSubmitVerification() {
        Intent intent = new Intent(getInstrumentation()
                .getTargetContext(), ChirpSubmissionActivity.class);
        startActivity(intent, null, null);


        ParseUser currentUser = ParseUser.getCurrentUser();
        Boolean emailVerification = (Boolean) currentUser.get("emailVerified");

        if (emailVerification) {
            // Connect views to values in xml file using their id's
            chirpTitleView = (EditText) getActivity().findViewById(R.id.chirp_title);
            chirpContactView = (EditText) getActivity().findViewById(R.id.chirp_contact);
            chirpExpirationDateView = (TextView) getActivity().findViewById(R.id.submit_chirp_expiration_date);
            chirpExpirationTimeView = (TextView) getActivity().findViewById(R.id.submit_chirp_expiration_time);
            chirpDescriptionView = (EditText) getActivity().findViewById(R.id.chirp_description);
            submitChirpButtonView = (Button) getActivity().findViewById(R.id.submit_chirp_button);

            chirpTitleView.setText("Title!"); // set up title
            chirpContactView.setText("test@email.edu"); // set up contact field
            assertTrue(getActivity().titleValidation());

            // Set up default date and time.
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, 7);
            chirpExpirationDateView.setText(getActivity().DATE_FORMATTER.format(cal.getTime()));
            chirpExpirationTimeView.setText(getActivity().TIME_FORMATTER.format(cal.getTime()));
            assertTrue(getActivity().expirationDateValidation());

            // Set up description
            chirpDescriptionView.setText("Description!");
            assertTrue(getActivity().descriptionValidation());

            // Mock the chirp calls. Now all the methods do nothing.
            Chirp chirpMock = Mockito.mock(Chirp.class);
            getActivity().setChirp(chirpMock);
            submitChirpButtonView.performClick();
            assertTrue(this.isFinishCalled());

            // Verify that we tried to save the chirp!
            Mockito.verify(chirpMock).saveWithPermissions();

        } else {
            assertFalse(this.isFinishCalled());
        }
    }

}

