package cs121.jam.chirps;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
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
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;


import cs121.jam.model.Chirp;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks, SwipeRefreshLayout.OnRefreshListener {
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

        swipeListLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_list);
        swipeListLayout.setOnRefreshListener(this);

        showChirpList();
    }

    public void onResume() {
        showChirpList();
        super.onResume();
    }

    public void showChirpList() {
        chirpListView = (ListView) findViewById(R.id.chirp_list_view);

        ParseUser currentUser = ParseUser.getCurrentUser();
        ArrayList<String> school = new ArrayList<String>();
        school.add(currentUser.getString("school"));
        // TODO: Maybe this goes somewhere else?
        ParseObject.registerSubclass(Chirp.class);

        ParseQuery<Chirp> chirpQuery = ParseQuery.getQuery("Chirp");
        chirpQuery.whereEqualTo(Chirp.CHIRP_APPROVAL, true);
        chirpQuery.whereContainsAll(Chirp.SCHOOLS, school);
        chirpQuery.whereGreaterThan(Chirp.EXPIRATION_DATE, new Date());
        chirpQuery.orderByAscending(Chirp.EXPIRATION_DATE);

        List<Chirp> chirps = null;
        try {
            chirps = chirpQuery.find();
        } catch (ParseException pe) {
            Log.e("Chirp Query", pe.getMessage());
        }

        // don't display list if there are no chirps to display
        if(chirps == null)
            return;

        final String[] titleArray = new String[chirps.size()];
        final Date[] expDateArray = new Date[chirps.size()];
        final String[] idArray = new String[chirps.size()];
        for (int i = 0; i < chirps.size(); i++) {
            titleArray[i] = chirps.get(i).getTitle();
            expDateArray[i] = chirps.get(i).getExpirationDate();
            idArray[i] = chirps.get(i).getObjectId();
        }

        ChirpList chirpListAdapter = new ChirpList(this, titleArray, expDateArray);

        final Activity thisActivity = this;
        chirpListView = (ListView) findViewById(R.id.chirp_list_view);
        chirpListView.setAdapter(chirpListAdapter);
        chirpListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(thisActivity, ChirpDetailsActivity.class);
                intent.putExtra(CHIRP_OBJECT_ID, idArray[+position]);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
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
            showChirpList();
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
    public void onRefresh() {
        showChirpList();
        swipeListLayout.setRefreshing(false);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
