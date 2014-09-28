package cs121.jam.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import java.util.Date;


@ParseClassName("Chirp")
public class Chirp extends ParseObject{
    private String TITLE = "title";
    private String DESCRIPTION = "description";
    private String IMAGE = "image";
    private String EXPIRATION_DATE = "expirationDate";
    private String CONTACT_EMAIL = "contactEmail";
    private String SCHOOLS = "schools";
    private String CATEGORIES = "categories";
    private String USER = "user";

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
}
