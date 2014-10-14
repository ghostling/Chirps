package cs121.jam.chirps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

//import cs121.jam.model.User;


public class SignUpActivity extends Activity {
    // All views from Login
    EditText firstNameView;
    EditText lastNameView;
    EditText emailView;
    EditText passwordView;
    EditText reenterPasswordView;
    Button signUpButtonView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // Connect views to values in xml file using their id's
        firstNameView = (EditText) findViewById(R.id.sign_up_first_name_input);
        lastNameView = (EditText) findViewById(R.id.sign_up_last_name_input);
        emailView = (EditText) findViewById(R.id.sign_up_email_input);
        passwordView = (EditText) findViewById(R.id.sign_up_password_input);
        reenterPasswordView = (EditText) findViewById(R.id.sign_up_reenter_password_input);
        signUpButtonView = (Button) findViewById(R.id.sign_up_button);

        // Sign up Button Click Listener
        signUpButtonView.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Retrieve the text entered from the EditText
                String name = firstNameView.getText().toString() + ' '
                        + lastNameView.getText().toString();
                String email = emailView.getText().toString();
                String password = passwordView.getText().toString();
                String reenterPassword = reenterPasswordView.getText().toString();

                String school = "";

                if(email.matches(getString(R.string.email_regex_harveymudd)))
                    school = getString(R.string.school_harveymudd);
                else if(email.matches(getString(R.string.email_regex_pomona)))
                    school = getString(R.string.school_pomona);
                else if(email.matches(getString(R.string.email_regex_scripps)))
                    school = getString(R.string.school_scripps);
                else if(email.matches(getString(R.string.email_regex_claremontmckenna)))
                    school = getString(R.string.school_claremontmckenna);
                else if(email.matches(getString(R.string.email_regex_pitzer)))
                    school = getString(R.string.school_pitzer);
                else {
                    // TODO(Alex): Don't create User since they are not using a school email
                }

                // Force user to fill up the form
                if (!password.equals(reenterPassword)) {
                    Toast.makeText(getApplicationContext(),
                            "Passwords did not match. Please try again.",
                            Toast.LENGTH_LONG).show();
                    reenterPasswordView.selectAll();
                    reenterPasswordView.requestFocus();
                } else {
                    // Save new user data into Parse.com Data Storage
                    ParseUser user = new ParseUser();
                    user.setUsername(email);
                    user.setPassword(password);
                    user.setEmail(email);
                    user.put("name", name);
                    user.put("school", school);

                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                // Show a simple Toast message upon successful registration
                                Toast.makeText(getApplicationContext(),
                                        "Successfully Signed up, " +
                                                "please log in to continue.",
                                        Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(
                                        SignUpActivity.this,
                                        LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Sign up Error", Toast.LENGTH_LONG)
                                        .show();

                                switch (e.getCode()) {
                                    case ParseException.INVALID_EMAIL_ADDRESS:
                                        Toast.makeText(getApplicationContext(),
                                                "Invalid email address.", Toast.LENGTH_LONG)
                                                .show();
                                        break;
                                    case ParseException.USERNAME_TAKEN:
                                        Toast.makeText(getApplicationContext(),
                                                "Username is already taken. Please enter a different one.",
                                                Toast.LENGTH_LONG)
                                                .show();
                                        break;
                                    case ParseException.EMAIL_TAKEN:
                                        Toast.makeText(getApplicationContext(),
                                                "Username is already taken. Please enter a different one.",
                                                Toast.LENGTH_LONG)
                                                .show();
                                        break;
                                    default:
                                        Toast.makeText(getApplicationContext(),
                                                "Could not sign up, please try again later.",
                                                Toast.LENGTH_LONG)
                                                .show();

                                }
                            }
                        }
                    });
                }

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

