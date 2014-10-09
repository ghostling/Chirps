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

import org.w3c.dom.Text;

import cs121.jam.model.Chirp;


public class ChirpDetailsActivity extends Activity {

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
        String expirationDate = "";
        String contact = "";
        if (chirp != null) {
            title = chirp.getTitle();
            description = chirp.getDescription();
            expirationDate = chirp.getExpirationDate().toString();
            contact = chirp.getContactEmail();
        }
        Log.e("Chirp Details", title);
        TextView tv = (TextView) findViewById(R.id.chirp_details_title);
        tv.setText(title);
        tv = (TextView) findViewById(R.id.chirp_details_description);
        tv.setText(description);
        tv = (TextView) findViewById(R.id.chirp_details_expiration_date);
        tv.setText(expirationDate);
        tv = (TextView) findViewById(R.id.chirp_details_contact);
        tv.setText(contact);
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
