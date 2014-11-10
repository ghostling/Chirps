package cs121.jam.chirps;

import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cs121.jam.model.Chirp;


public class SearchActivity extends Activity {
    public ListView searchResultsView;
    public ChirpList searchResultsAdapter;
    public TextView messageView;
    ProgressBar barView;
    public ArrayList<String> idArray = new ArrayList<String>();
    private AbsListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchResultsView = (ListView) findViewById(R.id.chirp_search_list);
        searchResultsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(SearchActivity.this, ChirpDetailsActivity.class);
                intent.putExtra(MainActivity.CHIRP_OBJECT_ID, idArray.get(position));
                startActivity(intent);
            }
        });

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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        messageView = (TextView) findViewById(R.id.search_message);
        searchResultsView = (ListView) findViewById(R.id.chirp_search_list);
        barView = (ProgressBar) findViewById(R.id.search_progress);
        messageView.setVisibility(View.GONE);
        searchResultsView.setVisibility(View.GONE);
        barView.setVisibility(View.VISIBLE);

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }
    }

    public void doMySearch(String query) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ArrayList<String> school = new ArrayList<String>();
        school.add(currentUser.getString("school"));
        List<String> searchWords = Arrays.asList((query.toLowerCase()).trim().split("\\s+"));

        ParseQuery<Chirp> chirpQuery = ParseQuery.getQuery("Chirp");
        chirpQuery.whereEqualTo(Chirp.CHIRP_APPROVAL, true);
        chirpQuery.whereContainsAll(Chirp.SCHOOLS, school);
        chirpQuery.whereGreaterThan(Chirp.EXPIRATION_DATE, new Date());
        chirpQuery.whereContainsAll(Chirp.KEYWORDS, searchWords);
        Log.i("Keywords searched.", query);

        chirpQuery.findInBackground(new FindCallback<Chirp>() {
            public void done(List<Chirp> chirps, ParseException e) {
                barView = (ProgressBar) findViewById(R.id.search_progress);
                barView.setVisibility(View.GONE);

                if (chirps.size() == 0) {
                    messageView = (TextView) findViewById(R.id.search_message);
                    messageView.setText("No chirps found.");
                    messageView.setVisibility(View.VISIBLE);
                } else {
                    String[] titleArray = new String[chirps.size()];
                    Date[] expDateArray = new Date[chirps.size()];

                    // Extract the title and date for each chirp in the results.
                    for (int i = 0; i < chirps.size(); i++) {
                        Log.i("Search Results", chirps.get(i).getTitle());
                        titleArray[i] = chirps.get(i).getTitle();
                        expDateArray[i] = chirps.get(i).getExpirationDate();
                        idArray.add(chirps.get(i).getObjectId());
                    }

                    searchResultsAdapter = new ChirpList(SearchActivity.this, titleArray, expDateArray);
                    searchResultsView = (ListView) findViewById(R.id.chirp_search_list);
                    searchResultsView.setVisibility(View.VISIBLE);
                    searchResultsView.setAdapter(searchResultsAdapter);
                }
            }
        });
    }

}
