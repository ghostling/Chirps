package cs121.jam.chirps;

import android.app.Activity;
import android.app.Fragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cs121.jam.model.Chirp;


public class SearchActivity extends FragmentActivity implements ChirpFragment.OnFragmentInteractionListener {
    public ChirpList searchResultsAdapter;
    ProgressBar barView;
    public ArrayList<String> idArray = new ArrayList<String>();
    private AbsListView mListView;
    public ChirpFragment frag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);


        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        handleIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        barView = (ProgressBar) findViewById(R.id.search_progress);
        barView.setVisibility(View.VISIBLE);

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);

            FragmentManager fragmentManager = getSupportFragmentManager();

            if(frag != null)
                fragmentManager.beginTransaction().remove(frag).commit();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, ChirpFragment.newInstance(ChirpFragment.SEARCH_CHIRP_QUERY, query))
                    .commit();

            barView = (ProgressBar) findViewById(R.id.search_progress);
            barView.setVisibility(View.GONE);
        }
    }


    @Override
    public void onFragmentChirpClick(String chirpId) {
        Intent intent = new Intent(SearchActivity.this, ChirpDetailsActivity.class);
        intent.putExtra(MainActivity.CHIRP_OBJECT_ID, chirpId);
        startActivity(intent);
    }
}
