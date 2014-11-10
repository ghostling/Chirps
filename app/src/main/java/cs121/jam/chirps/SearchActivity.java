package cs121.jam.chirps;

import android.app.Activity;
import android.app.ListActivity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

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
                String[] titleArray = new String[chirps.size()];
                Date[] expDateArray = new Date[chirps.size()];
                String[] idArray = new String[chirps.size()];

                // Extract the title and date for each chirp in the results.
                for (int i = 0; i < chirps.size(); i++) {
                    Log.i("Search Results", chirps.get(i).getTitle());
                    titleArray[i] = chirps.get(i).getTitle();
                    expDateArray[i] = chirps.get(i).getExpirationDate();
                    idArray[i] = chirps.get(i).getObjectId();
                }

                searchResultsAdapter = new ChirpList(SearchActivity.this, titleArray, expDateArray);
                searchResultsView = (ListView) findViewById(R.id.chirp_search_list);
                searchResultsView.setAdapter(searchResultsAdapter);
            }
        });
    }
}
