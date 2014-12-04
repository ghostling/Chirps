package cs121.jam.chirps.test.Activities;

import android.content.Intent;
import android.test.ActivityUnitTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.widget.TextView;

import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.mockito.Mockito;

import java.text.SimpleDateFormat;
import java.util.Date;

import cs121.jam.chirps.ChirpDetailsActivity;
import cs121.jam.chirps.R;
import cs121.jam.model.Chirp;

import static org.mockito.Mockito.when;

/**
 * Created by maiho on 12/3/14.
 */
public class ChirpDetailsActivityUnitTest extends ActivityUnitTestCase<ChirpDetailsActivity> {
    public static SimpleDateFormat PRETTY_DATE_TIME = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm a");
    Chirp mockChirp;

    public ChirpDetailsActivityUnitTest() {
        super(ChirpDetailsActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());
        ParseObject.registerSubclass(Chirp.class);
    }

    @Override
    protected void tearDown() throws Exception {
        // Does nothing.
    }

    /* Tests that the chirp details are correctly displayed.
     */
    @MediumTest
    public void testChirpDetailsDisplay() {
        Intent intent = new Intent(getInstrumentation()
                .getTargetContext(), ChirpDetailsActivity.class);
        startActivity(intent, null, null);

        mockChirp = Mockito.mock(Chirp.class);

        JSONArray schools = new JSONArray();
        JSONArray categories = new JSONArray();

        when(mockChirp.getSchools()).thenReturn(schools);
        when(mockChirp.getCategories()).thenReturn(categories);
        when(mockChirp.getTitle()).thenReturn("Test title");
        when(mockChirp.getContactEmail()).thenReturn("test@hmc.edu");
        when(mockChirp.getDescription()).thenReturn("Test description.");
        Date currentDate = new Date();
        when(mockChirp.getExpirationDate()).thenReturn(currentDate);
        when(mockChirp.getUser()).thenReturn(ParseUser.getCurrentUser());

        getActivity().setChirp(mockChirp);
        getActivity().getAndDisplayChirpDetails("");

        // Verify that the mockChirp methods have been called.
        Mockito.verify(mockChirp).getSchools();
        Mockito.verify(mockChirp).getCategories();
        Mockito.verify(mockChirp).getTitle();
        Mockito.verify(mockChirp).getContactEmail();
        Mockito.verify(mockChirp).getDescription();
        Mockito.verify(mockChirp).getExpirationDate();
        Mockito.verify(mockChirp).getUser();

        // Verify that the fields have been filled.
        TextView chirpTitleView = (TextView) getActivity().findViewById(R.id.chirp_details_title);
        assertEquals("Test title", chirpTitleView.getText().toString());

        TextView chirpDescriptionView = (TextView) getActivity().findViewById(R.id.chirp_details_description);
        assertEquals("Test description.", chirpDescriptionView.getText().toString());

        TextView chirpExpirationDateView = (TextView) getActivity().findViewById(R.id.chirp_details_expiration_date);
        assertEquals(PRETTY_DATE_TIME.format(currentDate), chirpExpirationDateView.getText().toString());

        TextView chirpContactView = (TextView) getActivity().findViewById(R.id.chirp_details_contact);
        assertEquals("test@hmc.edu", chirpContactView.getText().toString());

        TextView relevantSchoolsView = (TextView) getActivity().findViewById(R.id.chirp_relevant_schools);
        assertEquals("", relevantSchoolsView.getText().toString());

        TextView relevantCategoriesView = (TextView) getActivity().findViewById(R.id.chirp_details_categories);
        assertEquals("", relevantCategoriesView.getText().toString());
    }
}