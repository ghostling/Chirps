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
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

// Parse:


public class MainActivity extends FragmentActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, FilterDrawerFragment.FilterDrawerCallbacks, ChirpFragment.OnFragmentInteractionListener, MyChirpsFragment.OnFragmentInteractionListener, UserProfileFragment.OnFragmentInteractionListener {
    public static String CHIRP_OBJECT_ID = "chirpObjectId";

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Fragment[] navigationFragments;

    private FilterDrawerFragment mFilterDrawerFragment;
    private Fragment[] filterFragments;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    // For search
    public ChirpFragment frag;

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
    boolean hideAdd = false;
    boolean hideSearchAndFilter = false;

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
        filterFragments = new Fragment[10];

        setContentView(R.layout.activity_main);
        mTitle = getTitle();

        // Navigation Drawer
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        // Filter Drawer
        mFilterDrawerFragment = (FilterDrawerFragment)
                getFragmentManager().findFragmentById(R.id.filter_drawer);

        // Set up the drawer.
        mFilterDrawerFragment.setUp(
                R.id.filter_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        ParseInstallation.getCurrentInstallation().saveInBackground();
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e("com.parse.push", "Failed to subscribe for push notifications.", e);
                }
            }
        });

        if (!hideSearchAndFilter) {
            ViewStub stub = (ViewStub) findViewById(R.id.search_filter_bar_stub);
            stub.inflate();

            // Initially is a fragment switch.
            onFragmentSwitch();
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.search_progress);
            String query = intent.getStringExtra(SearchManager.QUERY);
            FragmentManager fragmentManager = getSupportFragmentManager();

            if(frag != null)
                fragmentManager.beginTransaction().remove(frag).commit();
            progressBar.setVisibility(View.VISIBLE);

            fragmentManager.beginTransaction()
                    .replace(R.id.container,
                            ChirpFragment.newInstance(ChirpFragment.SEARCH_CHIRP_QUERY, query))
                    .commit();
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    public void onFragmentSwitch() {
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) findViewById(R.id.action_search);
        if (getComponentName() != null && searchView != null) {
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));
        }
        if(hideSearchAndFilter) {
            (findViewById(R.id.search_filter_bar)).setVisibility(View.GONE);
        } else {
            ViewStub stub = (ViewStub) findViewById(R.id.search_filter_bar_stub);
            if (stub != null) {
                stub.setVisibility(View.VISIBLE);
                setFilterToggleListener();
            } else {
                try {
                    (findViewById(R.id.search_filter_bar)).setVisibility(View.VISIBLE);
                    setFilterToggleListener();
                } catch (NullPointerException e) {
                }
            }
        }
    }

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

    @Override
    public void onFilterDrawerItemSelected(int position) {
        String category = getResources().getStringArray(R.array.categories_array)[position];
        mTitle = category;

        if(filterFragments[position] == null)
            filterFragments[position] = ChirpFragment.newInstance(ChirpFragment.CATEGORY_CHIRP_QUERY, category);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, filterFragments[position])
                .commit();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        hideRefresh = false;
        hideAdd = false;
        hideSearchAndFilter = false;
        DrawerLayout mDrawerLayout = ((DrawerLayout) findViewById(R.id.drawer_layout));
        if(position == 3) {
            mTitle = "All Chirps";
            if(navigationFragments[position] == null)
                navigationFragments[position] = ChirpFragment.newInstance(ChirpFragment.ALL_CHIRP_QUERY, "");

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, navigationFragments[position])
                    .commit();
            if (mDrawerLayout != null)
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, Gravity.RIGHT);
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
            hideAdd = true;
            hideSearchAndFilter = true;
            if (mDrawerLayout != null)
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
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
            hideSearchAndFilter = true;
            if (mDrawerLayout != null)
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
        }
        else if(position == 2) {
            mTitle = "My Favorites";
            if(navigationFragments[position] == null)
                navigationFragments[position] = ChirpFragment.newInstance(ChirpFragment.FAVORITES_CHIRP_QUERY, "");

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, navigationFragments[position])
                    .commit();
            hideSearchAndFilter = true;
            if (mDrawerLayout != null)
                mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, Gravity.RIGHT);
        }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen if the left drawer is not
            // showing. Otherwise, let the drawer decide what to show in the action bar.

            getMenuInflater().inflate(R.menu.main, menu);

            menu.findItem(R.id.action_logout).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
            if (hideAdd) {
                menu.findItem(R.id.action_add_chirp).setVisible(false);
                menu.findItem(R.id.action_logout).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
            }
            if (hideRefresh) {
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
                }
            }
        });
    }
}
