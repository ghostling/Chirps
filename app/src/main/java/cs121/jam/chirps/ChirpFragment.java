package cs121.jam.chirps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cs121.jam.model.Chirp;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the
 * interface.
 */
public class ChirpFragment extends Fragment implements AbsListView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_PARAM1 = "QueryType";
    public static final String ARG_PARAM2 = "QueryValue";

    public static final String USER_CHIRP_QUERY = "UserQuery";
    public static final String CATEGORY_CHIRP_QUERY = "CatQuery";
    public static final String ALL_CHIRP_QUERY = "AllQuery";

    // TODO: Rename and change types of parameters
    private String mParamQueryType;
    private Object mParamQueryValue;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;


    /**
     * Used to store the last screen title. For use in RESTOREACTIONBAR
     */
    private CharSequence mTitle;

    /**
     * Used to display the list of Chirps.
     */
    private ListView chirpListView;
    private ArrayAdapter<String> chirpListAdapter;

    private String[] idArray;

    private ParseQuery<Chirp> chirpQuery;

    public static String CHIRP_OBJECT_ID = "chirpObjectId";

    private SwipeRefreshLayout swipeListLayout;

    // TODO: Rename and change types of parameters
    public static ChirpFragment newInstance(String param1, String param2) {
        ChirpFragment fragment = new ChirpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ChirpFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParamQueryType = getArguments().getString(ARG_PARAM1);
            mParamQueryValue = getArguments().getString(ARG_PARAM2);


            // TODO: Change Adapter to display your content
            chirpQuery = ParseQuery.getQuery("Chirp");

            if (mParamQueryType.equals(USER_CHIRP_QUERY)) {
                boolean approved = false;
                if (mParamQueryValue.equals("TRUE"))
                    approved = true;
                ParseUser currentUser = ParseUser.getCurrentUser();
                chirpQuery.whereEqualTo(Chirp.USER, currentUser);
                chirpQuery.whereEqualTo(Chirp.CHIRP_APPROVAL, approved);
            } else if (mParamQueryType.equals(CATEGORY_CHIRP_QUERY)) {

                ParseUser currentUser = ParseUser.getCurrentUser();
                ArrayList<String> school = new ArrayList<String>();
                school.add(currentUser.getString("school"));
                ArrayList<String> category = new ArrayList<String>();
                category.add(mParamQueryValue.toString());
                // TODO: Maybe this goes somewhere else?
                ParseObject.registerSubclass(Chirp.class);

                chirpQuery.whereEqualTo(Chirp.CHIRP_APPROVAL, true);
                chirpQuery.whereContainsAll(Chirp.SCHOOLS, school);
                chirpQuery.whereContainsAll(Chirp.CATEGORIES, category);
                chirpQuery.whereGreaterThan(Chirp.EXPIRATION_DATE, new Date());
            } else if (mParamQueryType.equals(ALL_CHIRP_QUERY)) {

                ParseUser currentUser = ParseUser.getCurrentUser();
                ArrayList<String> school = new ArrayList<String>();
                school.add(currentUser.getString("school"));
                // TODO: Maybe this goes somewhere else?
                ParseObject.registerSubclass(Chirp.class);

                chirpQuery.whereEqualTo(Chirp.CHIRP_APPROVAL, true);
                chirpQuery.whereContainsAll(Chirp.SCHOOLS, school);
                chirpQuery.whereGreaterThan(Chirp.EXPIRATION_DATE, new Date());
            }

            chirpQuery.orderByAscending(Chirp.EXPIRATION_DATE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chirp, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(R.id.chirp_list_view);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);


        swipeListLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_list);
        swipeListLayout.setOnRefreshListener(this);
        int orange = getResources().getColor(R.color.orange_loading);
        swipeListLayout.setColorSchemeColors(orange, orange, orange, orange);

        return view;
    }

    public void setChirpQuery(ParseQuery<Chirp> query) {
        this.chirpQuery = query;
    }

    public void setQueryFromParams() {
        if (getArguments() != null) {
            mParamQueryType = getArguments().getString(ARG_PARAM1);
            mParamQueryValue = getArguments().getString(ARG_PARAM2);


            // TODO: Change Adapter to display your content
            ParseQuery<Chirp> chirpQuery = ParseQuery.getQuery("Chirp");

            if (mParamQueryType.equals(USER_CHIRP_QUERY)) {
                boolean approved = false;
                if (mParamQueryValue.equals("TRUE"))
                    approved = true;
                ParseUser currentUser = ParseUser.getCurrentUser();
                chirpQuery.whereEqualTo(Chirp.USER, currentUser.getObjectId());
                chirpQuery.whereEqualTo(Chirp.CHIRP_APPROVAL, approved);
            } else if (mParamQueryType.equals(CATEGORY_CHIRP_QUERY)) {

                ParseUser currentUser = ParseUser.getCurrentUser();
                ArrayList<String> school = new ArrayList<String>();
                school.add(currentUser.getString("school"));
                ArrayList<String> category = new ArrayList<String>();
                category.add(mParamQueryValue.toString());
                // TODO: Maybe this goes somewhere else?
                ParseObject.registerSubclass(Chirp.class);

                chirpQuery.whereEqualTo(Chirp.CHIRP_APPROVAL, true);
                chirpQuery.whereContainsAll(Chirp.SCHOOLS, school);
                chirpQuery.whereContainsAll(Chirp.CATEGORIES, category);
                chirpQuery.whereGreaterThan(Chirp.EXPIRATION_DATE, new Date());
            } else if (mParamQueryType.equals(ALL_CHIRP_QUERY)) {

                ParseUser currentUser = ParseUser.getCurrentUser();
                ArrayList<String> school = new ArrayList<String>();
                school.add(currentUser.getString("school"));
                // TODO: Maybe this goes somewhere else?
                ParseObject.registerSubclass(Chirp.class);

                chirpQuery.whereEqualTo(Chirp.CHIRP_APPROVAL, true);
                chirpQuery.whereContainsAll(Chirp.SCHOOLS, school);
                chirpQuery.whereGreaterThan(Chirp.EXPIRATION_DATE, new Date());
            }

            chirpQuery.orderByAscending(Chirp.EXPIRATION_DATE);
        }
    }

    public void showChirpList() {


        chirpListView = (ListView) getView().findViewById(R.id.chirp_list_view);

        /**
        ParseUser currentUser = ParseUser.getCurrentUser();
        ArrayList<String> school = new ArrayList<String>();
        school.add(currentUser.getString("school"));
        // TODO: Maybe this goes somewhere else?
        ParseObject.registerSubclass(Chirp.class);

        ParseQuery<Chirp> chirpQuery = ParseQuery.getQuery("Chirp");
        chirpQuery.whereEqualTo(Chirp.CHIRP_APPROVAL, true);
        chirpQuery.whereContainsAll(Chirp.SCHOOLS, school);
        chirpQuery.whereGreaterThan(Chirp.EXPIRATION_DATE, new Date());
        chirpQuery.orderByAscending(Chirp.EXPIRATION_DATE);
         **/

        if(chirpQuery == null)
            setQueryFromParams();

        if(chirpQuery != null) {
            chirpQuery.findInBackground(new FindCallback<Chirp>() {
                public void done(List<Chirp> chirps, ParseException e) {
                    if (chirps == null)
                        return;

                    final String[] titleArray = new String[chirps.size()];
                    final Date[] expDateArray = new Date[chirps.size()];
                    idArray = new String[chirps.size()];
                    for (int i = 0; i < chirps.size(); i++) {
                        titleArray[i] = chirps.get(i).getTitle();
                        expDateArray[i] = chirps.get(i).getExpirationDate();
                        idArray[i] = chirps.get(i).getObjectId();
                    }

                    ChirpList chirpListAdapter = new ChirpList(getActivity(), titleArray, expDateArray, mParamQueryType.equals(USER_CHIRP_QUERY));

                    chirpListView = (ListView) getView().findViewById(R.id.chirp_list_view);
                    chirpListView.setAdapter(chirpListAdapter);
                }
            });
        }
    }


    public void onResume() {
        showChirpList();
        super.onResume();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentChirpClick(idArray[position]);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyText instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    public void refreshList() {
        swipeListLayout.setRefreshing(true);
        onRefresh();
    }

    @Override
    public void onRefresh() {
        showChirpList();
        swipeListLayout.setRefreshing(false);
    }

    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    * <p>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentChirpClick(String chirpId);
    }

}
