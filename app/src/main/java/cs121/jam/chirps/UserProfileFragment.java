package cs121.jam.chirps;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import cs121.jam.model.User;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "USERID";

    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment UserProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserProfileFragment newInstance(String param1) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public UserProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);

        Button resetPasswordButton = (Button) view.findViewById(R.id.reset_password_button);

        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseUser profileUser;


        if(currentUser.getUsername().equals(mParam1)) {
            profileUser = currentUser;
            resetPasswordButton.setVisibility(View.VISIBLE);
            resetPasswordButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onFragmentResetPassword();
                }
            });
        }
        else {
            ParseQuery<ParseUser> getUser = ParseQuery.getQuery("User");
            getUser.whereEqualTo(User.USERNAME, mParam1);
            List<ParseUser> user = new ArrayList<ParseUser>();
            try {
                user = getUser.find();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            profileUser = user.get(0);
        }

        displayUserProfile(profileUser, view);
        return view;
    }

    public void displayUserProfile(ParseUser profileUser, View view) {
        String name = profileUser.getString("name");
        String email = profileUser.getEmail();
        String school = profileUser.getString("school");


        Log.e("User Profile", name);
        TextView nameView = (TextView) view.findViewById(R.id.user_profile_name);
        nameView.setText(name);

        TextView emailView = (TextView) view.findViewById(R.id.user_profile_email);
        emailView.setText(email);

        TextView schoolView = (TextView) view.findViewById(R.id.user_profile_school);
        schoolView.setText(school);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
        }
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentResetPassword();
    }

}