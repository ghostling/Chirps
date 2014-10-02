package cs121.jam.model;

import com.parse.ParseUser;

/**
 * Created by maiho on 10/2/14.
 */
public class User extends ParseUser {
    private String NAME = "name";
    private String SCHOOL = "school";

    public String getName() {
        return getString(NAME);
    }

    public void setName(String name) {
        put(NAME, name);
    }

    public String getSchool() {
        return getString(SCHOOL);
    }

    public void setSchool(String school) {
        put(SCHOOL, school);
    }
}



