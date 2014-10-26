package cs121.jam.chirps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.util.Date;

import cs121.jam.model.Chirp;


public class ChirpDetailsActivity extends Activity {

    TextView relevantSchoolsTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chirp_details);

        Intent intent = getIntent();
        String chirpObjectId = intent.getStringExtra(MainActivity.CHIRP_OBJECT_ID);
        getAndDisplayChirpDetails(chirpObjectId);


    }

    public void getAndDisplayChirpDetails(String chirpObjectId) {
        ParseQuery<Chirp> chirpQuery = ParseQuery.getQuery(Chirp.class);
        Chirp chirp = null;
        try {
            chirp = chirpQuery.get(chirpObjectId);
        } catch (ParseException pe) {
            Log.e("Chirp Details", pe.getMessage());
        }

        String title = "";
        String description = "";
        Date expirationDate = null;
        String contact = "";
        JSONArray schoolsArray = chirp.getSchools();
        StringBuilder schools = new StringBuilder("");

        if (chirp != null) {
            title = chirp.getTitle();
            description = chirp.getDescription();
            expirationDate = chirp.getExpirationDate();
            contact = chirp.getContactEmail();
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chirp_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
