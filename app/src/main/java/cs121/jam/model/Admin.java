package cs121.jam.model;

import android.util.Log;

import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRole;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.util.List;

/**
 * Created by jiexicao on 10/5/14.
 *
 * This class can only be access through the implementation side as of now. Once we have an admin
 * homepage, we can allow for a different interface. To create an admin, simply create an "Admin"
 * object following the constructor and it will create the user account as well as add it to the
 * ParseRole.
 */
public class Admin {
    public static String ADMIN_ROLE = "admin";

    public Admin(String username, String password, String email) {
        // Create a basic user.
        ParseUser adminUser = new ParseUser();
        adminUser.setUsername(username);
        adminUser.setPassword(password);
        adminUser.setEmail(email);

        adminUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i("Admin User Creation", "Account creation for username  success!");
                    // Associate with role only when account is created successfully.
                    associateRole();
                } else {
                    Log.e("Admin User Creation", e.getMessage());
                }
            }
        });
    }

    private static void associateRole() {
        ParseQuery<ParseRole> roleQuery = ParseRole.getQuery();
        roleQuery.whereEqualTo("name", ADMIN_ROLE);
        ParseRole role = null;

        try {
            // ParseRoles are unique.
            role = roleQuery.getFirst();
        } catch (ParseException pe) {
            Log.e("Admin Role Query", pe.getMessage());
        }

        // For the first time we create a ParseRole. Useful if we purge it from database.
        if (role == null) {
            role = new ParseRole(ADMIN_ROLE);
            ParseACL roleACL = new ParseACL();

            /**
             * TODO: Once we have a super user, these should only be true to super user.
             *
             * This is fairly annoying because once we "signup" the new admin user, it gets set as
             * currentUser, which is born with public access. May exist a workaround, not sure.
             */
            roleACL.setPublicReadAccess(true);
            roleACL.setPublicWriteAccess(true);
            role.setACL(roleACL);
        }

        // Add the admin that has just "signed up."
        role.getUsers().add(ParseUser.getCurrentUser());
        role.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i("Admin Role Save", "User has been added to Admin role.");
                } else {
                    Log.e("Admin Role Save", e.getMessage());
                }
            }
        });
    }

    private void setSchoolPermissions(String[] schools) {
        // TODO: Implement certain school restrictions for an admin.
    }

    public void remove() {
        // TODO: Remove the admin.
    }
}
