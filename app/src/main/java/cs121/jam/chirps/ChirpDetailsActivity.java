package cs121.jam.chirps;

import android.app.Activity;
import android.view.MenuItem;
import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.Date;

import cs121.jam.model.Chirp;
import cs121.jam.model.User;

import static cs121.jam.model.User.*;


public class ChirpDetailsActivity extends Activity {

    TextView relevantSchoolsTextView;

    public static String USER_CLICKABLE = "userClickable?";

    public Chirp chirp;

    boolean userClickable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chirp_details);

        Intent intent = getIntent();
        String chirpObjectId = intent.getStringExtra(MainActivity.CHIRP_OBJECT_ID);
        userClickable = intent.getBooleanExtra(USER_CLICKABLE, true);

        ParseQuery<Chirp> chirpQuery = ParseQuery.getQuery(Chirp.class);
        try {
            chirp = chirpQuery.get(chirpObjectId);
        } catch (ParseException pe) {
            Log.e("Chirp Details", pe.getMessage());
        }

        getAndDisplayChirpDetails(chirpObjectId);
    }

    public void getAndDisplayChirpDetails(String chirpObjectId) {

        String title = "";
        String description = "";
        Date expirationDate = null;
        String contact = "";
        JSONArray schoolsArray = chirp.getSchools();
        JSONArray categoriesArray = chirp.getCategories();
        StringBuilder schools = new StringBuilder("");
        StringBuilder categories = new StringBuilder("");

        if (chirp != null) {
            title = chirp.getTitle();
            description = chirp.getDescription();
            expirationDate = chirp.getExpirationDate();
            contact = chirp.getContactEmail();
        }

        TextView userField = (TextView) findViewById(R.id.chirp_details_user);

        try {
            userField.setText(chirp.getUser().fetchIfNeeded().getString(User.NAME));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(userClickable) {


            userField.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ChirpDetailsActivity.this, UserProfileActivity.class);
                    intent.putExtra(UserProfileActivity.USER_OBJECT_ID, chirp.getUser().getObjectId());
                    startActivity(intent);
                }
            });
        }

        Log.e("Chirp Details", title);
        TextView tv = (TextView) findViewById(R.id.chirp_details_title);
        tv.setText(title);
        tv = (TextView) findViewById(R.id.chirp_details_description);
        tv.setText(description);
        tv = (TextView) findViewById(R.id.chirp_details_expiration_date);
        tv.setText(ChirpList.PRETTY_DATE_TIME.format(expirationDate));
        tv = (TextView) findViewById(R.id.chirp_details_contact);
        tv.setText(contact);
        final String finalContact = contact;
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] {finalContact});
                startActivity(Intent.createChooser(intent, ""));
            }
        });
        if(schoolsArray != null) {
            for (int i = 0; i < schoolsArray.length(); i++) {
                if (i != 0)
                    schools = schools.append(", ");

                try {
                    schools = schools.append(schoolsArray.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        relevantSchoolsTextView = (TextView) findViewById(R.id.chirp_relevant_schools);
        relevantSchoolsTextView.setText(schools.toString());

        if(categoriesArray != null) {
            for (int i = 0; i < categoriesArray.length(); i++) {
                if (i != 0)
                    categories = categories.append(", ");

                try {
                    categories = categories.append(categoriesArray.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        tv = (TextView) findViewById(R.id.chirp_details_categories);
        tv.setText(categories);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chirp_details, menu);

        ParseUser currentUser = ParseUser.getCurrentUser();
        if(chirp.getUser().equals(currentUser)) {
            MenuItem favToggle = menu.findItem(R.id.favorite_toggle);
            favToggle.setVisible(false);
        }
        else {
            MenuItem item = menu.findItem(R.id.favorite_toggle);
            menu.findItem(R.id.delete).setVisible(false);
            try {
                boolean fav = chirp.isFavoriting(currentUser);
                if(!fav) {
                    item.setIcon(R.drawable.btn_star_big_off);
                    item.setTitle("Favorite");
                }
                else {
                    item.setIcon(R.drawable.btn_star_big_on);
                    item.setTitle("Un-Favorite");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
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
            Intent intent = new Intent(ChirpDetailsActivity.this,
                    LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.favorite_toggle) {
            try {
                ParseUser currentUser = ParseUser.getCurrentUser();
                boolean fav = chirp.isFavoriting(currentUser);
                if(fav) {
                    item.setIcon(R.drawable.btn_star_big_off);
                    item.setTitle("Favorite");
                    chirp.removeFromFavorites(currentUser);
                }
                else {
                    item.setIcon(R.drawable.btn_star_big_on);
                    item.setTitle("Un-Favorite");
                    chirp.addToFavorites(currentUser);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
