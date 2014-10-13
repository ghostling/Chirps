package cs121.jam.chirps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseUser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Date;

import cs121.jam.model.Chirp;


public class ChirpSubmissionActivity extends Activity {
    // All views from Chirp Submission Page
    EditText chirpTitleView;
    EditText chirpContactView;
    EditText chirpExpirationDateView;
    EditText chirpExpirationTimeView;
    EditText chirpDescriptionView;
    Button submitChirpButtonView;
    CheckBox college_pmcCheckBox;
    CheckBox college_hmcCheckBox;
    CheckBox college_scCheckBox;
    CheckBox college_cmcCheckBox;
    CheckBox college_pzcCheckBox;
    ArrayList<CheckBox> collegesCheckBoxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chirp_submission);

        // Connect views to values in xml file using their id's
        chirpTitleView = (EditText) findViewById(R.id.chirp_title);
        chirpContactView = (EditText) findViewById(R.id.chirp_contact);
        chirpExpirationDateView = (EditText) findViewById(R.id.chirp_expiration_date);
        chirpExpirationTimeView = (EditText) findViewById(R.id.chirp_expiration_time);
        chirpDescriptionView = (EditText) findViewById(R.id.chirp_description);
        submitChirpButtonView = (Button) findViewById(R.id.submit_chirp_button);

        college_pmcCheckBox = (CheckBox) findViewById(R.id.school_checkbox_pmc);
        college_hmcCheckBox = (CheckBox) findViewById(R.id.school_checkbox_hmc);
        college_scCheckBox = (CheckBox) findViewById(R.id.school_checkbox_sc);
        college_cmcCheckBox = (CheckBox) findViewById(R.id.school_checkbox_cmc);
        college_pzcCheckBox = (CheckBox) findViewById(R.id.school_checkbox_pzc);

        collegesCheckBoxes = new ArrayList<CheckBox>();
        collegesCheckBoxes.add(college_pmcCheckBox);
        collegesCheckBoxes.add(college_hmcCheckBox);
        collegesCheckBoxes.add(college_scCheckBox);
        collegesCheckBoxes.add(college_cmcCheckBox);
        collegesCheckBoxes.add(college_pzcCheckBox);

        // Sign up Button Click Listener
        submitChirpButtonView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Retrieve the text entered from the EditText
                String chirpTitle = chirpTitleView.getText().toString();
                String chirpContact = chirpContactView.getText().toString();
                String chirpExpirationDate = chirpExpirationDateView.getText().toString();
                String chirpExpirationTime = chirpExpirationTimeView.getText().toString();
                String chirpDescription = chirpDescriptionView.getText().toString();
                JSONArray chirpSchools = new JSONArray();

                // Collect all the colleges submitted
                for(CheckBox collegeCheckBox: collegesCheckBoxes) {
                    if (collegeCheckBox.isChecked())
                        chirpSchools.put(collegeCheckBox.getText().toString());
                }
                // Create Date object
                // TODO(Mai): Extract the date and time from the given strings and use it in the
                // constructor for Date.
                Date expirationDate = new Date();

                ParseUser currentUser = ParseUser.getCurrentUser();

                // Save new user data into Parse.com Data Storage
                Chirp chirp = new Chirp();
                chirp.setTitle(chirpTitle);
                chirp.setSchools(chirpSchools);
                chirp.setContactEmail(chirpContact);
                chirp.setExpirationDate(expirationDate);
                chirp.setDescription(chirpDescription);
                chirp.setUser(currentUser);
                chirp.approveChirp(); // TODO: this is auto-approve; remove later.
                chirp.saveWithPermissions();

                // Tell the user that the chirp is submitted and take them back to the main activity
                Toast.makeText(getApplicationContext(),
                        "Successfully submitted chirp, " +
                                "an admin will approve or reject it shortly.",
                        Toast.LENGTH_LONG).show();
                Intent intent = new Intent(
                        ChirpSubmissionActivity.this,
                        MainActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chirp_submission, menu);
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
