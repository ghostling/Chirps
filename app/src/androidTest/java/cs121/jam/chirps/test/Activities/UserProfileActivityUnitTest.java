package cs121.jam.chirps.test.Activities;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.SmallTest;
import android.view.View;
import android.widget.TextView;

import com.parse.ParseUser;

import cs121.jam.chirps.ParseApplication;
import cs121.jam.chirps.R;
import cs121.jam.chirps.UserProfileActivity;

/**
 * Created by maiho on 12/3/14.
 */
public class UserProfileActivityUnitTest extends ActivityUnitTestCase<UserProfileActivity> {

    public UserProfileActivityUnitTest() {
        super(UserProfileActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        // Does nothing.
    }

    /* Tests to make sure that a user profile is displayed.
    Need to figure out how to workaround the fact that we are testing from outside the application.
    Can't access users otherwise.
     */
    @SmallTest
    public void testUserProfile() {
        Intent intent = new Intent(getInstrumentation()
                .getTargetContext(), UserProfileActivity.class);
        startActivity(intent, null, null);
    }

}