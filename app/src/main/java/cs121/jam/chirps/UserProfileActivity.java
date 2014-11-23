package cs121.jam.chirps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;
import java.util.Date;

import cs121.jam.model.Chirp;


public class UserProfileActivity extends Activity {
    ParseUser currentUser = ParseUser.getCurrentUser();
    public static String USER_OBJECT_ID = "userObjectId";
    public Button resetPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        displayUserProfile();
        displayChirpsList();

        resetPasswordButton = (Button) findViewById(R.id.reset_password_button);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                Intent intent = new Intent(
                        UserProfileActivity.this,
                        ResetPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    public void displayUserProfile() {
        String name = currentUser.getString("name");
        String email = currentUser.getEmail();
        String school = currentUser.getString("school");


        Log.e("User Profile", name);
        TextView nameView = (TextView) findViewById(R.id.user_profile_name);
        nameView.setText(name);

        TextView emailView = (TextView) findViewById(R.id.user_profile_email);
        emailView.setText(email);

        TextView schoolView = (TextView) findViewById(R.id.user_profile_school);
        schoolView.setText(school);

        displayChirpsList();
    }

    public void displayChirpsList() {
        // TODO: Maybe this goes somewhere else?
        ParseObject.registerSubclass(Chirp.class);

        ParseQuery<Chirp> chirpQuery = ParseQuery.getQuery("Chirp");
        chirpQuery.whereEqualTo(Chirp.CHIRP_APPROVAL, true);
        chirpQuery.whereEqualTo("user", currentUser);
        chirpQuery.orderByAscending(Chirp.EXPIRATION_DATE);

        List<Chirp> chirps = null;
        try {
            chirps = chirpQuery.find();
        } catch (ParseException pe) {
            Log.e("Chirp Query", pe.getMessage());
        }

        final String[] titleArray = new String[chirps.size()];
        final Date[] expDateArray = new Date[chirps.size()];
        final String[] idArray = new String[chirps.size()];
        for (int i = 0; i < chirps.size(); i++) {
            titleArray[i] = chirps.get(i).getTitle();
            expDateArray[i] = chirps.get(i).getExpirationDate();
            idArray[i] = chirps.get(i).getObjectId();
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_profile, menu);
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
        } else if (id == R.id.action_logout) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            currentUser.logOut();
            Intent intent = new Intent(UserProfileActivity.this,
                    LoginActivity.class);
            startActivity(intent);
            finish();
        } else if (id == R.id.user_profile) {
            Intent intent = new Intent(UserProfileActivity.this,
                    UserProfileActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
