package cs121.jam.chirps;


import android.app.ActionBar;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * MainActivity holds the fragments accessible by the left navigation drawer. These include
 * "All Chirps" (homepage), "My Profile," "My Chirps," and "My Favorites."
 */
public class MainActivity extends FragmentActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        FilterDrawerFragment.FilterDrawerCallbacks,
        ChirpFragment.OnFragmentInteractionListener,
        MyChirpsFragment.OnFragmentInteractionListener,
        UserProfileFragment.OnFragmentInteractionListener {

    // Constants
    public static String CHIRP_OBJECT_ID = "chirpObjectId";

    // Fragments managing the behaviors of this activity.
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Fragment[] navigationFragments;
    private FilterDrawerFragment mFilterDrawerFragment;
    private Fragment[] filterFragments;

    // Used to store the last screen title. For use in {@link #restoreActionBar()}.
    private CharSequence mTitle;

    // Maintain characteristics across the different fragments of this activity.
    private boolean hideRefreshMenuButton = false;
    private boolean hideAddChirpMenuButton = false;
    private boolean hideSearchAndFilterBar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            // If user is anonymous, send them to LoginActivity.
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            ParseUser currentUser = ParseUser.getCurrentUser();
            if (currentUser == null) {
                // The current user is anonymous, send them to the LoginActivity.
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }

        // Ties the current user to the installation on this phone and sets up push notifications.
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("user", ParseUser.getCurrentUser());
        installation.saveInBackground();
        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("com.parse.push", "Failed to subscribe for push notifications.", e);
                }
            }
        });

        navigationFragments = new Fragment[10];
        filterFragments = new Fragment[10];

        setContentView(R.layout.activity_main);
        mTitle = getTitle();

        // Set up Navigation Drawer.
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // Set up Filter Drawer.
        mFilterDrawerFragment = (FilterDrawerFragment)
                getFragmentManager().findFragmentById(R.id.filter_drawer);
        mFilterDrawerFragment.setUp(R.id.filter_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // Initial setup for search and filter bar.
        ViewStub stub = (ViewStub) findViewById(R.id.search_filter_bar_stub);
        stub.inflate();

        // Initially switch to first fragment ("All Chirps").
        onFragmentSwitch();

    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    /**
     * Currently only handles search intent inside of MainActivity.
     * @param intent Intent of search action.
     */
    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.search_progress);
            String query = intent.getStringExtra(SearchManager.QUERY);
            FragmentManager fragmentManager = getSupportFragmentManager();

            progressBar.setVisibility(View.VISIBLE);
            fragmentManager.beginTransaction()
                    .replace(R.id.container,
                            ChirpFragment.newInstance(ChirpFragment.SEARCH_CHIRP_QUERY, query))
                    .commit();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Attributes of the activity change when we switch from fragment to fragment. The key here is
     * that only the homepage ("All Chirps") contains the search and filter bar and is allowed to
     * search and filter the list of chirps.
     */
    public void onFragmentSwitch() {
        // Associate searchable configuration with the SearchView.
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) findViewById(R.id.action_search);
        if (getComponentName() != null && searchView != null)
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        if(hideSearchAndFilterBar) {
            RelativeLayout searchFilterBar = (RelativeLayout) findViewById(R.id.search_filter_bar);
            if(searchFilterBar != null) {
                searchFilterBar.setVisibility(View.GONE);
            }
        } else {
            try {
                (findViewById(R.id.search_filter_bar)).setVisibility(View.VISIBLE);
                setFilterToggleListener();
            } catch (NullPointerException e) {}
        }
    }

    /**
     * Allows the filter button to toggle open/close the right filter drawer.
     */
    public void setFilterToggleListener() {
        ImageButton filterBtn = (ImageButton) findViewById(R.id.filter_button);
        filterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                mDrawerLayout.openDrawer(Gravity.RIGHT);
            }
        });
    }

    /**
     * Given the position of the element selected on the filter drawer, performs a query using the
     * filter and replaces the main content area with matching chirps.
     * @param position
     */
    @Override
    public void onFilterDrawerItemSelected(int position) {
        String category = getResources().getStringArray(R.array.categories_array)[position];
        mTitle = category;

        if(filterFragments[position] == null) {
            filterFragments[position] = ChirpFragment.newInstance(
                    ChirpFragment.CATEGORY_CHIRP_QUERY, category);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, filterFragments[position])
                .commit();
    }

    /**
     * Given the position of the element selected on the navigation drawer, performs a request for
     * the appropriate fragment and replaces the current one.
     * @param position
     */
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        hideRefreshMenuButton = false;
        hideAddChirpMenuButton = false;

        // Most fragments do not use the serach and filter bar.
        hideSearchAndFilterBar = true;
        DrawerLayout mDrawerLayout = ((DrawerLayout) findViewById(R.id.drawer_layout));
        if (mDrawerLayout != null)
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);

        // TODO(jiexi): Standardize position numbers into constant variables.
        // Depending on which section is selected, get the correct fragment.

        // Undo all selected filters
        if(mFilterDrawerFragment != null)
            mFilterDrawerFragment.unsetFilterChoice();

        if (position == 3) {
            mTitle = getString(R.string.all_chirps_section);
            if (navigationFragments[position] == null) {
                navigationFragments[position] = ChirpFragment.newInstance(
                        ChirpFragment.ALL_CHIRP_QUERY, "");
            }
            hideSearchAndFilterBar = false;
            if (mDrawerLayout != null)
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.RIGHT);
        } else if (position == 0) {
            mTitle = getString(R.string.my_profile_section);
            if (navigationFragments[position] == null) {
                navigationFragments[position] = UserProfileFragment.newInstance(
                        ParseUser.getCurrentUser().getObjectId(), "TRUE");
            }
            hideRefreshMenuButton = true;
            hideAddChirpMenuButton = true;
        } else if (position == 1) {
            mTitle = getString(R.string.my_chirps_section);
            if (navigationFragments[position] == null)
                navigationFragments[position] = MyChirpsFragment.newInstance();
            hideRefreshMenuButton = true;
        } else if (position == 2) {
            mTitle = getString(R.string.my_favorites_section);
            if (navigationFragments[position] == null) {
                navigationFragments[position] = ChirpFragment.newInstance(
                        ChirpFragment.FAVORITES_CHIRP_QUERY, "");
            }
            hideAddChirpMenuButton = true;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, navigationFragments[position])
                .commit();

        invalidateOptionsMenu();
        restoreActionBar();
        onFragmentSwitch();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    /**
     * Only show items in the action bar relevant to this screen if the left drawer is not
     * showing. Otherwise, let the drawer decide what to show in the action bar.
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);

            menu.findItem(R.id.action_logout).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            if (hideAddChirpMenuButton) {
                menu.findItem(R.id.action_add_chirp).setVisible(false);
                menu.findItem(R.id.action_logout).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            }
            if (hideRefreshMenuButton) {
                menu.findItem(R.id.action_refresh_chirps).setVisible(false);
            }

            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Handle action bar item clicks here. The action bar will automatically handle clicks on the
     * Home/Up button, so long as you specify a parent activity in AndroidManifest.xml.
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh_chirps) {
            ChirpFragment frag = (ChirpFragment) getSupportFragmentManager().findFragmentById(R.id.container);
            if(frag.isVisible())
                frag.refreshList();
        } else if (id == R.id.action_logout) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            currentUser.logOut();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(String chirpId) {
        onFragmentChirpClick(chirpId);
    }

    @Override
    public void onFragmentChirpClick(String chirpId) {
        Intent intent = new Intent(MainActivity.this, ChirpDetailsActivity.class);
        intent.putExtra(CHIRP_OBJECT_ID, chirpId);
        startActivity(intent);
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
        currentUser.setEmail(email + "fake");
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
                }
            }
        });
    }
}
