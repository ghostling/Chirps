package cs121.jam.chirps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

//import cs121.jam.model.User;

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
                                    // If user exists and is authenticated,
                                    // send user to MainActivity.
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
