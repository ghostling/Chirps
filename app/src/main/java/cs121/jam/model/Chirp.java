package cs121.jam.model;

import com.parse.ParseACL;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import java.util.Date;

/**
 * TODO: Instead of "void," we should really return success/failure messages to enable us to have
 * a starting point for our future unit tests.
 *
 * Whether or not attributes are correct or required will be specified by the "View." It is not the
 * intent of the data model.
 */
@ParseClassName("Chirp")
public class Chirp extends ParseObject{
    public static String TITLE = "title";
    public static String DESCRIPTION = "description";
    public static String IMAGE = "image";
    public static String EXPIRATION_DATE = "expirationDate";
    public static String CONTACT_EMAIL = "contactEmail";
    public static String SCHOOLS = "schools";
    public static String CATEGORIES = "categories";
    public static String USER = "user";
    public static String CHIRP_APPROVAL = "chirpApproval";

    public String getTitle() {
        return getString(TITLE);
    }

    public void setTitle(String title) {
        put(TITLE, title);
    }

    public String getDescription() {
        return getString(DESCRIPTION);
    }

    public void setDescription(String description) {
        put(DESCRIPTION, description);
    }

    public ParseFile getImage() {
        return getParseFile(IMAGE);
    }

    public void setImage(ParseFile image) {
        put(IMAGE, image);
    }

    public Date getExpirationDate() {
        return getDate(EXPIRATION_DATE);
    }

    public void setExpirationDate(Date expirationDate) {
        put(EXPIRATION_DATE, expirationDate);
    }

    /**
     * When no expiration date is specified, auto it to a week from now.
     */
    public void setExpirationDate() {
        Date expirationDate = new Date();
        put(EXPIRATION_DATE, expirationDate);
    }

    public String getContactEmail() {
        return getString(CONTACT_EMAIL);
    }

    public void setContactEmail(String email) {
        put(CONTACT_EMAIL, email);
    }

    public JSONArray getSchools() {
        return getJSONArray(SCHOOLS);
    }

    public void setSchools(JSONArray schools) {
        put(SCHOOLS, schools);
    }

    public JSONArray getCategories() {
        return getJSONArray(CATEGORIES);
    }

    public void setCategories(JSONArray categories) {
        put(CATEGORIES, categories);
    }

    public ParseUser getUser() {
        return getParseUser(USER);
    }

    public void setUser(ParseUser user) {
        put(USER, user);
    }

    public void approveChirp() {
        put(CHIRP_APPROVAL, true);
    }

    public void rejectChirp() {
        put(CHIRP_APPROVAL, false);
    }

    public void saveChirp() {
        ParseACL chirpACL = new ParseACL();
        chirpACL.setPublicReadAccess(true);
        chirpACL.setRoleWriteAccess(Admin.ADMIN_ROLE, true);

        // Allows the current user to read/modify its own objects.
        ParseACL.setDefaultACL(chirpACL, true);

        this.setACL(chirpACL);
        this.saveInBackground();
    }

    public void deleteChirp() {
        // TODO: Implement.
    }

    @Override
    public String toString() {
        return getClassName() + "[" +
                "title=" + getTitle() + ", " +
                "description=" + getDescription() + "]";
    }
}
