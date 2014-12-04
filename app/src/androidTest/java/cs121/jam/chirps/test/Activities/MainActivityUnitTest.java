package cs121.jam.chirps.test.Activities;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.parse.ParseUser;

import cs121.jam.chirps.MainActivity;

/**
 * Created by maiho on 12/3/14.
 */
public class MainActivityUnitTest extends ActivityUnitTestCase<MainActivity> {

    public MainActivityUnitTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        Intent intent = new Intent(getInstrumentation()
                .getTargetContext(), MainActivity.class);
        startActivity(intent, null, null);
    }

    @Override
    protected void tearDown() throws Exception {
        // Does nothing.
    }
}
