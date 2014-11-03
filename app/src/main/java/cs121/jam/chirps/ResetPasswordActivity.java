package cs121.jam.chirps;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseUser;


public class ResetPasswordActivity extends Activity {

    public Button setNewPasswordButton;
    public EditText newPasswordView;
    public EditText confirmNewPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        setNewPasswordButton = (Button) findViewById(R.id.set_new_password_button);
        newPasswordView = (EditText) findViewById(R.id.new_password);
        confirmNewPasswordView = (EditText) findViewById(R.id.confirm_new_password);

        setNewPasswordButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                String newPassword = newPasswordView.getText().toString();
                String confirmPassword = confirmNewPasswordView.getText().toString();

                if(newPassword.equals(confirmPassword)) {
                    ParseUser currentUser = ParseUser.getCurrentUser();
                    currentUser.setPassword(newPassword);
                    currentUser.saveInBackground();

                    Toast.makeText(
                            getApplicationContext(),
                            "New password set",
                            Toast.LENGTH_SHORT).show();

                    finish();
                }
                else {
                    Toast.makeText(
                            getApplicationContext(),
                            "New password and confirm new password values are not equal",
                            Toast.LENGTH_LONG).show();
                }

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.reset_password, menu);
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
