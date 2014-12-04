package cs121.jam.chirps.test.Activities;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.Button;
import android.widget.EditText;

import com.parse.ParseUser;

import cs121.jam.chirps.R;
import cs121.jam.chirps.SignUpActivity;

/**
 * Created by maiho on 11/30/14.
 * TODO(maiho): Find out how to mock the Parse API calls. Eventually want to test if the login
 * activity gets called if all the fields are valid. But we don't want to actually make a user.
 */
public class SignUpActivityUnitTest extends ActivityUnitTestCase<SignUpActivity> {
    EditText firstNameView;
    EditText lastNameView;
    EditText emailView;
    EditText passwordView;
    EditText reenterPasswordView;
    Button signUpButtonView;

    public SignUpActivityUnitTest() {
        super(SignUpActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        // Does nothing.
    }

    /*
    Tests that we do not start the LoginActivity if we didn't register an account.
     */
    @SmallTest
    public void testLoginActivityWasNotLaunchedWithIntent() {
        Intent mLoginIntent = new Intent(getInstrumentation()
                .getTargetContext(), SignUpActivity.class);
        startActivity(mLoginIntent, null, null);

        signUpButtonView =
                (Button) getActivity().findViewById(R.id.sign_up_button);
        signUpButtonView.performClick();

        // Sign up fields are empty.
        final Intent launchIntent = getStartedActivityIntent();
        assertNull("Intent was not null", launchIntent);

        getActivity().finish();
    }
}
