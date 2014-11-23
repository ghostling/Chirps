package cs121.jam.chirps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;
import java.util.Date;

import cs121.jam.model.Chirp;


public class UserProfileActivity extends FragmentActivity {
    ParseUser currentUser = ParseUser.getCurrentUser();
    public static String USER_OBJECT_ID = "userObjectId";
    public Button resetPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        ParseUser user = ParseUser.getCurrentUser();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.profile_container, UserProfileFragment.newInstance(user.getUsername()))
                .commit();

        fragmentManager.beginTransaction()
                .replace(R.id.chirps_container, ChirpFragment.newInstance(ChirpFragment.GENERAL_USER_CHIRP_QUERY, user.getObjectId()))
                .commit();
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
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            currentUser.logOut();
            Intent intent = new Intent(UserProfileActivity.this,
                    LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
