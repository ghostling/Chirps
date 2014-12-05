package cs121.jam.chirps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.ParseUser;

/**
 * Created by aputman.
 *
 * Activity to show a user's profile.
 */
public class UserProfileActivity extends FragmentActivity implements
        ChirpFragment.OnFragmentInteractionListener,
        UserProfileFragment.OnFragmentInteractionListener {
    public static String USER_OBJECT_ID = "userObjectId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_main);

        // Handle intent
        Intent intent = getIntent();
        String userId = intent.getStringExtra(USER_OBJECT_ID);

        Log.e("User Profile", "User id: " + userId);

        ViewPager mViewPager = (ViewPager) findViewById(R.id.viewPager);

        // Set the ViewPagerAdapter into ViewPager
        mViewPager.setAdapter(new UserProfileViewPagerAdapter(getSupportFragmentManager(), userId));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_profile, menu);
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
            Intent intent = new Intent(UserProfileActivity.this,
                    LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Handling navigation to a user's chirp from the user profile.
     * @param chirpId the ID of the chirp selected.
     */
    @Override
    public void onFragmentChirpClick(String chirpId) {
        Intent intent = new Intent(UserProfileActivity.this, ChirpDetailsActivity.class);
        intent.putExtra(MainActivity.CHIRP_OBJECT_ID, chirpId);
        startActivity(intent);
    }


    @Override
    public void onFragmentResetPassword() {
        // Can't Reset password from this view
    }

    @Override
    public void onFragmentResendVerification() {
        // Can't resend verification from this activity
    }
}
