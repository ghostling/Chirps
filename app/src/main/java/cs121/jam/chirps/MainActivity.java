package cs121.jam.chirps;

import android.app.Activity;

import android.app.ActionBar;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
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
import android.widget.SearchView;
import android.widget.Toast;

// Parse:
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;


import cs121.jam.model.Chirp;


public class MainActivity extends FragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, ChirpFragment.OnFragmentInteractionListener, MyChirpsFragment.OnFragmentInteractionListener, UserProfileFragment.OnFragmentInteractionListener {
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

    private Fragment[] navigationFragments;

    boolean hideRefresh = false;
    boolean hideAddAndSearch = false;

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

        ParseInstallation inst = ParseInstallation.getCurrentInstallation();
        inst.put("user", ParseUser.getCurrentUser());
        inst.saveInBackground();

        navigationFragments = new Fragment[10];
        setContentView(R.layout.activity_main);
        mTitle = getTitle();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });

    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        hideRefresh = false;
        hideAddAndSearch = false;
        if(position == 3) {
            mTitle = "All Chirps";
            if(navigationFragments[position] == null)
                navigationFragments[position] = ChirpFragment.newInstance(ChirpFragment.ALL_CHIRP_QUERY, "");

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, navigationFragments[position])
                    .commit();
        }
        else if(position == 0) {
            mTitle = "My Profile";
            if(navigationFragments[position] == null)
                navigationFragments[position] = UserProfileFragment.newInstance(ParseUser.getCurrentUser().getObjectId(), "TRUE");
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, navigationFragments[position])
                    .commit();
            hideRefresh = true;
            hideAddAndSearch = true;
        }
        else if(position == 1) {
            mTitle = "My Chirps";
            if(navigationFragments[position] == null)
                navigationFragments[position] = MyChirpsFragment.newInstance();
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, navigationFragments[position])
                    .commit();
            hideRefresh = true;
        }
        else if(position == 2) {
            mTitle = "My Favorites";
            if(navigationFragments[position] == null)
                navigationFragments[position] = ChirpFragment.newInstance(ChirpFragment.FAVORITES_CHIRP_QUERY, "");

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, navigationFragments[position])
                    .commit();
        }
        else {
            String category = getResources().getStringArray(R.array.categories_array)[position-4];
            mTitle = category;
            if(navigationFragments[position] == null)
                navigationFragments[position] = ChirpFragment.newInstance(ChirpFragment.CATEGORY_CHIRP_QUERY, category);

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, navigationFragments[position])
                    .commit();
        }
        invalidateOptionsMenu();
        restoreActionBar();
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

            // Associate searchable configuration with the SearchView
            SearchManager searchManager =
                    (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            SearchView searchView =
                    (SearchView) menu.findItem(R.id.action_search).getActionView();
            ComponentName name = new ComponentName(this, SearchActivity.class);
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(name));
            menu.findItem(R.id.action_logout).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            if(hideAddAndSearch) {
                menu.findItem(R.id.action_search).setVisible(false);
                menu.findItem(R.id.action_add_chirp).setVisible(false);
                menu.findItem(R.id.action_logout).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            }
            if(hideRefresh) {
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
        if (id == R.id.action_refresh_chirps) {
            ChirpFragment frag = (ChirpFragment) getSupportFragmentManager().findFragmentById(R.id.container);
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


    @Override
    public void onFragmentResetPassword() {
        Intent intent = new Intent(
                MainActivity.this,
                ResetPasswordActivity.class);
        startActivity(intent);
    }

    @Override
    public void onFragmentResendVerification() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        String email = currentUser.getEmail();
        currentUser.setEmail(email+"fake");
        try {
            currentUser.save();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        currentUser.setEmail(email);
        currentUser.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getApplicationContext(),
                            "Email Verification Resent",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Object not saved
                    Toast.makeText(getApplicationContext(),
                            "Problem Resending Email Verification",
                            Toast.LENGTH_SHORT).show();
                    Log.e("Saving chirp: ", e.getMessage());
                }
            }
        });
    }
}
