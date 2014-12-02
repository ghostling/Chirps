package cs121.jam.chirps;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cs121.jam.model.Chirp;
import cs121.jam.model.User;

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
    public static final String GENERAL_USER_CHIRP_QUERY = "GeneralUserQuery";
    public static final String CATEGORY_CHIRP_QUERY = "CatQuery";
    public static final String ALL_CHIRP_QUERY = "AllQuery";
    public static final String FAVORITES_CHIRP_QUERY = "FavoriteQuery";
    public static final String SEARCH_CHIRP_QUERY = "SearchQuery";

    // TODO: Rename and change types of parameters
    private String mParamQueryType;
    private String mParamQueryValue;

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

    private ArrayList<String> idArray;


    public static String CHIRP_OBJECT_ID = "chirpObjectId";

    private SwipeRefreshLayout swipeListLayout;

    private TextView messageView;

    // TODO: Rename and change types of parameters
    public static ChirpFragment newInstance(String param1, String param2) {

        // param1 = query type (For my chirps, all chirps, or by category, etc)
        // param2 = query value (values needed to perform that specific query)
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

        messageView = (TextView) view.findViewById(R.id.search_message);
        return view;
    }


    public ParseQuery setQueryFromParams() {
        if (getArguments() != null) {
            mParamQueryType = getArguments().getString(ARG_PARAM1);
            mParamQueryValue = getArguments().getString(ARG_PARAM2);

            // TODO: Maybe this goes somewhere else?
            ParseObject.registerSubclass(Chirp.class);

            // TODO: Change Adapter to display your content
            ParseQuery chirpQuery = ParseQuery.getQuery("Chirp");

            if (mParamQueryType.equals(USER_CHIRP_QUERY)) {
                boolean approved = false;
                if (mParamQueryValue.equals("TRUE"))
                    approved = true;
                ParseUser currentUser = ParseUser.getCurrentUser();
                chirpQuery.whereEqualTo(Chirp.USER, currentUser);
                chirpQuery.whereEqualTo(Chirp.CHIRP_APPROVAL, approved);
            } else if (mParamQueryType.equals(GENERAL_USER_CHIRP_QUERY)) {
                ParseQuery<User> user = new ParseQuery<User>(User.class);
                ParseUser profUser = User.createWithoutData(User.class, mParamQueryValue);
                chirpQuery.whereEqualTo(Chirp.USER, profUser);
                chirpQuery.whereEqualTo(Chirp.CHIRP_APPROVAL, true);
            } else if (mParamQueryType.equals(CATEGORY_CHIRP_QUERY)) {

                ParseUser currentUser = ParseUser.getCurrentUser();
                ArrayList<String> school = new ArrayList<String>();
                school.add(currentUser.getString("school"));
                ArrayList<String> category = new ArrayList<String>();
                category.add(mParamQueryValue.toString());

                chirpQuery.whereEqualTo(Chirp.CHIRP_APPROVAL, true);
                chirpQuery.whereContainsAll(Chirp.SCHOOLS, school);
                chirpQuery.whereContainsAll(Chirp.CATEGORIES, category);
                chirpQuery.whereGreaterThan(Chirp.EXPIRATION_DATE, new Date());
            } else if (mParamQueryType.equals(FAVORITES_CHIRP_QUERY)) {

                ParseUser currentUser = ParseUser.getCurrentUser();
                ArrayList<String> userId = new ArrayList<String>();
                userId.add(currentUser.getObjectId());

                chirpQuery.whereEqualTo(Chirp.CHIRP_APPROVAL, true);
                chirpQuery.whereContainsAll(Chirp.FAVORITING, userId);
            } else if (mParamQueryType.equals(ALL_CHIRP_QUERY)) {

                ParseUser currentUser = ParseUser.getCurrentUser();
                ArrayList<String> school = new ArrayList<String>();
                school.add(currentUser.getString("school"));

                chirpQuery.whereEqualTo(Chirp.CHIRP_APPROVAL, true);
                chirpQuery.whereContainsAll(Chirp.SCHOOLS, school);
                chirpQuery.whereGreaterThan(Chirp.EXPIRATION_DATE, new Date());
            } else if (mParamQueryType.equals(SEARCH_CHIRP_QUERY)) {

                ParseUser currentUser = ParseUser.getCurrentUser();
                ArrayList<String> school = new ArrayList<String>();
                school.add(currentUser.getString("school"));
                List<String> searchWords = Arrays.asList((mParamQueryValue.toLowerCase()).trim().split("\\s+"));

                chirpQuery.whereEqualTo(Chirp.CHIRP_APPROVAL, true);
                chirpQuery.whereContainsAll(Chirp.SCHOOLS, school);
                chirpQuery.whereContainsAll(Chirp.KEYWORDS, searchWords);
                chirpQuery.whereGreaterThan(Chirp.EXPIRATION_DATE, new Date());
            }

            chirpQuery.orderByAscending(Chirp.EXPIRATION_DATE);

            return chirpQuery;
        }
        return null;
    }

    public void showChirpList() {


        chirpListView = (ListView) getView().findViewById(R.id.chirp_list_view);

        ParseQuery chirpQuery = setQueryFromParams();

        if(chirpQuery != null) {
            chirpQuery.findInBackground(new FindCallback<Chirp>() {
                public void done(List<Chirp> chirps, ParseException e) {
                    if (chirps == null)
                        return;

                    if (chirps.size() == 0) {

                        messageView.setText("No Chirps Found");
                        messageView.setVisibility(View.VISIBLE);
                    } else {

                        final ArrayList<Chirp> chirpList = new ArrayList<Chirp>();
                        idArray = new ArrayList<String>();
                        for (int i = 0; i < chirps.size(); i++) {
                            chirpList.add(chirps.get(i));
                            idArray.add(chirps.get(i).getObjectId());
                        }
                        if (chirpList != null && getActivity() != null) {
                            Log.e("Chirp Fragment", "Chirp List being created");
                            Log.e("Chirp Fragment", "Number of Chirps in list: " + idArray.size());

                            ChirpList chirpListAdapter = new ChirpList(getActivity(), chirpList, mParamQueryType.equals(USER_CHIRP_QUERY));

                            chirpListView = (ListView) getView().findViewById(R.id.chirp_list_view);
                            chirpListView.setItemsCanFocus(false);
                            chirpListView.setAdapter(chirpListAdapter);
                        }
                    }
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
            mListener.onFragmentChirpClick(idArray.get(position));
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
