package cs121.jam.chirps.test.Activities;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.Button;

import cs121.jam.chirps.LoginActivity;
import cs121.jam.chirps.R;


/**
 * Created by maiho on 11/30/14.
 */
public class LoginActivityUnitTest extends ActivityUnitTestCase<LoginActivity> {

    public LoginActivityUnitTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Intent mLoginIntent = new Intent(getInstrumentation()
                .getTargetContext(), LoginActivity.class);
        startActivity(mLoginIntent, null, null);
    }

    @Override
    protected void tearDown() throws Exception {
        // Does nothing.
    }

    /* Tests to make sure an intent to the SignUpActivity is launched when the button is clicked.
     */
    @SmallTest
    public void testSignUpActivityWasLaunchedWithIntent() {
        final Button signUpButton =
                (Button) getActivity().findViewById(R.id.sign_up_button);
        signUpButton.performClick();

        final Intent launchIntent = getStartedActivityIntent();
        assertNotNull("Intent was null", launchIntent);
    }

    /* Tests to make sure an intent is not sent to the MainActivity if the login fields are empty.
     */
    @SmallTest
    public void testMainActivityWasLaunchedWithIntent() {
        final Button loginButton =
                (Button) getActivity().findViewById(R.id.login_button);
        loginButton.performClick();

        // Login fields are empty at this point.
        final Intent launchIntent = getStartedActivityIntent();
        assertNull("Intent was not null", launchIntent);
    }
}
