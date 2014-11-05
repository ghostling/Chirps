package cs121.jam.chirps;

import android.app.Activity;

import android.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

// Parse:
import com.parse.FindCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;


import cs121.jam.model.Chirp;


public class MainActivity extends FragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, ChirpFragment.OnFragmentInteractionListener, MyChirpsFragment.OnFragmentInteractionListener {
    public static String CHIRP_OBJECT_ID = "chirpObjectId";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;


    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    /**
     * Used to display the list of Chirps.
     */
    private ListView chirpListView;
    private ArrayAdapter<String> chirpListAdapter;

    /**
     * Used for swipeRefresh
     */
    private SwipeRefreshLayout swipeListLayout;

    boolean hideRefresh = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Determine whether the current user is an anonymous user
        if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            // If user is anonymous, send the user to LoginActivity.class
            Intent intent = new Intent(MainActivity.this,
                    LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Get current user data from Parse.com
            ParseUser currentUser = ParseUser.getCurrentUser();

            // If the current user is anonymous, send them to the LoginActivity to either login or
            // sign up.
            if (currentUser == null) {
                // Send user to LoginActivity.class
                Intent intent = new Intent(MainActivity.this,
                        LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }

        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        hideRefresh = false;
        if(position == 0) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.chirp_list_fragment, ChirpFragment.newInstance(ChirpFragment.ALL_CHIRP_QUERY, ""))
                    .commit();
        }
        else if(position == 1) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.chirp_list_fragment, MyChirpsFragment.newInstance())
                    .commit();
            hideRefresh = true;
        }
        else if(position == 2) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.chirp_list_fragment, ChirpFragment.newInstance(ChirpFragment.CATEGORY_CHIRP_QUERY, ""))
                    .commit();
        }
        invalidateOptionsMenu();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.

            getMenuInflater().inflate(R.menu.main, menu);

            if(hideRefresh)
            {
                menu.findItem(R.id.action_refresh_chirps).setVisible(false);
            }
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_refresh_chirps) {
            ChirpFragment frag = (ChirpFragment) getSupportFragmentManager().findFragmentById(R.id.chirp_list_fragment);
            if(frag.isVisible())
            {
                frag.refreshList();
            }
        } else if (id == R.id.action_logout) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            currentUser.logOut();
            Intent intent = new Intent(MainActivity.this,
                    LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.user_profile) {
            Intent intent = new Intent(MainActivity.this,
                    UserProfileActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentChirpClick(String chirpId) {
        Intent intent = new Intent(MainActivity.this, ChirpDetailsActivity.class);
        intent.putExtra(CHIRP_OBJECT_ID, chirpId);
        startActivity(intent);
    }

    @Override
    public void onFragmentInteraction(String chirpId) {
        onFragmentChirpClick(chirpId);
    }


}
