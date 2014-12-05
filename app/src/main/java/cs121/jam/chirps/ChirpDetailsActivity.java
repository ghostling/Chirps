package cs121.jam.chirps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.MenuItem;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Date;

import cs121.jam.model.Chirp;
import cs121.jam.model.User;

/**
 * Created by jiexicao. Modified by alexputman.
 *
 * Activity for viewing the details of a specific chirp. Navigation to this activity should
 * include selecting a chirp.
 */
public class ChirpDetailsActivity extends Activity {
    // Data members
    TextView relevantSchoolsTextView;
    public static String USER_CLICKABLE = "userClickable?";
    public Chirp chirp;
    boolean userClickable;
    public ParseQuery<Chirp> chirpQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // If there is navigation from a previous activity, set up the up button to go back to
        // that activity.
        setContentView(R.layout.activity_chirp_details);
        try {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        // Handle the intent and extract the object ID of the chirp.
        Intent intent = getIntent();
        String chirpObjectId = intent.getStringExtra(MainActivity.CHIRP_OBJECT_ID);
        userClickable = intent.getBooleanExtra(USER_CLICKABLE, true);

        // Look up in parse
        chirpQuery = ParseQuery.getQuery(Chirp.class);
        try {
            chirp = chirpQuery.get(chirpObjectId);
        } catch (ParseException pe) {
            Log.e("Chirp Details", pe.getMessage());
        }

        getAndDisplayChirpDetails(chirpObjectId);
    }

    /**
     * Extracts relevant values from the chirp and modifies the XML to display those values.
     * @param chirpObjectId Parse objectID of the specific chirp.
     */
    public void getAndDisplayChirpDetails(String chirpObjectId) {
        // Don't display the chirp if it doesn't exist!
        if (chirp == null) {
            if (chirpObjectId != null) {
                Log.e("Chirp does not exist.", chirpObjectId);
            }
            return;
        }

        // Initialize values
        String title = "";
        String description = "";
        Date expirationDate = null;
        String contact = "";
        JSONArray schoolsArray = chirp.getSchools();
        JSONArray categoriesArray = chirp.getCategories();
        StringBuilder schools = new StringBuilder("");
        StringBuilder categories = new StringBuilder("");

        // Extract values
        if (chirp != null) {
            title = chirp.getTitle();
            description = chirp.getDescription();
            expirationDate = chirp.getExpirationDate();
            contact = chirp.getContactEmail();
        }

        // Fill in the user information.
        TextView userField = (TextView) findViewById(R.id.chirp_details_user);
        try {
            userField.setText(chirp.getUser().fetchIfNeeded().getString(User.NAME));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // If they have a public profile, set up the button handler to bring the user to the
        // appropriate profile page.
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

        // Fill in the rest of the fields in the chirp details.
        Log.e("Chirp Details", title);
        TextView chirpTitleView = (TextView) findViewById(R.id.chirp_details_title);
        chirpTitleView.setText(title);

        TextView chirpDescriptionView = (TextView) findViewById(R.id.chirp_details_description);
        chirpDescriptionView.setText(description);

        TextView chirpExpirationDate = (TextView) findViewById(R.id.chirp_details_expiration_date);
        chirpExpirationDate.setText(ChirpList.PRETTY_DATE_TIME.format(expirationDate));

        TextView chirpContact = (TextView) findViewById(R.id.chirp_details_contact);
        chirpContact.setText(contact);

        // When a user clicks on the contact email, they should be able to choose which
        // application to use to email the chirp poster.
        final String finalContact = contact;
        chirpContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] {finalContact});
                startActivity(Intent.createChooser(intent, ""));
            }
        });

        // If schools were selected, generate a pretty way to display the selected schools on
        // the view.
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

        // If categories were selected, generate a pretty way to display the selected categories
        // on the view.
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
        TextView relevantCategoriesTextView = (TextView) findViewById(R.id.chirp_details_categories);
        relevantCategoriesTextView.setText(categories);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chirp_details, menu);

        // If viewing the details of your own chirp, then don't show a button to be able to
        // favorite the chirp. All of a user's chirps are automatically followed by the user
        // who submitted them.
        ParseUser currentUser = ParseUser.getCurrentUser();
        if(chirp.getUser().getObjectId().equals(currentUser.getObjectId())) {
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
        if (id == android.R.id.home) {
            finish();
        } else if (id == R.id.action_logout) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            currentUser.logOut();
            Intent intent = new Intent(ChirpDetailsActivity.this,
                    LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.delete) {
            // Functionality to delete chirps
            DeleteChirpDialog deleteChirpDialog = new DeleteChirpDialog(this);
        } else if (id == R.id.favorite_toggle) {
            // Functionality to favorite chirps
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

    /**
     * Method to change the chirp shown in the chirp details.
     *
     * The main reason why I had this method is so I can test the Activity by inserting mock
     * objects.
     *
     * @param chirpObj A chirp object to replace the current one with.
     */
    public void setChirp(Chirp chirpObj) {
        chirp = chirpObj;
    }

    /**
     * A dialog that pops up when a user tries to delete a chirp. It asks the user to confirm the
     * the deletion before it occurs.
     */
    public class DeleteChirpDialog extends AlertDialog implements  DialogInterface.OnClickListener {
        protected DeleteChirpDialog(Context context) {
            super(context);

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Are you sure you want to delete this chirp?")
                    .setPositiveButton("Yes", this)
                    .setNegativeButton("No", this).show();
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            switch (i) {
                case DialogInterface.BUTTON_POSITIVE: // Yes button clicked
                    chirp.deleteInBackground();

                    Toast.makeText(getContext(),
                            "Deleting Chirp",
                            Toast.LENGTH_LONG)
                            .show();

                    finish();
                    break;
                case DialogInterface.BUTTON_NEGATIVE: // No button clicked
                    // do nothing
                    Toast.makeText(getContext(), "Chirp not deleted", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
