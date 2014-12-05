package cs121.jam.chirps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.parse.ParseUser;


public class UserProfileActivity extends FragmentActivity implements ChirpFragment.OnFragmentInteractionListener, UserProfileFragment.OnFragmentInteractionListener {
    ParseUser currentUser = ParseUser.getCurrentUser();
    public static String USER_OBJECT_ID = "userObjectId";
    public Button resetPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_main);

        Intent intent = getIntent();

        String userId = intent.getStringExtra(USER_OBJECT_ID);

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

    @Override
    public void onFragmentChirpClick(String chirpId) {
        Intent intent = new Intent(UserProfileActivity.this, ChirpDetailsActivity.class);
        intent.putExtra(MainActivity.CHIRP_OBJECT_ID, chirpId);
        startActivity(intent);
    }

    @Override
    public void onFragmentResetPassword() {
        // Cant Reset password from this view
    }

    @Override
    public void onFragmentResendVerification() {
        // Can't resend verification from this activity
    }
}
