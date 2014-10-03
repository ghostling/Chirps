package cs121.jam.chirps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class LoginActivity extends Activity {
    // All views from Login
    EditText emailView;
    EditText passwordView;
    Button loginButtonView;
    Button signUpButtonView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Connect views to values in xml file using their id's
        emailView = (EditText) findViewById(R.id.login_username_input);
        passwordView = (EditText) findViewById(R.id.login_password_input);
        loginButtonView = (Button) findViewById(R.id.login_button);
        signUpButtonView = (Button) findViewById(R.id.sign_up_button);

        // On login button click
        loginButtonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get the values of all the form fields

                String email = emailView.getText().toString().trim();
                String password = passwordView.getText().toString().trim();

                // Send data to Parse.com for verification
                ParseUser.logInInBackground(email, password,
                        new LogInCallback() {
                            public void done(ParseUser user, ParseException e) {
                                if (user != null) {
                                    // If user exist and authenticated, send user to Welcome.class
                                    Intent intent = new Intent(
                                            LoginActivity.this,
                                            MainActivity.class);
                                    startActivity(intent);
                                    Toast.makeText(getApplicationContext(),
                                            "Successfully Logged in",
                                            Toast.LENGTH_LONG).show();
                                    finish();
                                } else {
                                    Toast.makeText(
                                            getApplicationContext(),
                                            "No such user exists, please try again or sign up.",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                // Sign up Button Click Listener
                signUpButtonView.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View arg0) {
                        Intent intent = new Intent(
                                LoginActivity.this,
                                SignUpActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
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
