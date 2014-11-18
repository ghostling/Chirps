package cs121.jam.chirps;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cs121.jam.model.Chirp;

/**
 * Created by jiexicao on 10/9/14.
 */
public class ChirpList extends ArrayAdapter<Chirp> {
    private final Activity context;
    private final ArrayList<Chirp> chirps;
    private final boolean deleteButton;
    public static SimpleDateFormat PRETTY_DATE_TIME = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm a");

    public ChirpList(Activity context, ArrayList<Chirp> chirps, boolean deleteButton) {
        super(context, R.layout.chirp_row_item, chirps);
        this.context = context;
        this.chirps = chirps;
        this.deleteButton = deleteButton;


    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.chirp_row_item, null, true);
        TextView textTitle = (TextView) rowView.findViewById(R.id.chirp_item_title);
        TextView textExpDate = (TextView) rowView.findViewById(R.id.chirp_item_exp_date);


        final ImageButton deleteChirpButton = (ImageButton) rowView.findViewById(R.id.chirp_item_delete_button);
        final ToggleButton favoriteChirpButton = (ToggleButton) rowView.findViewById(R.id.chirp_item_favorite_button);

        deleteChirpButton.setTag(position);
        favoriteChirpButton.setTag(position);
        try {
            favoriteChirpButton.setChecked(chirps.get(position).isFavoriting(ParseUser.getCurrentUser()));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(deleteButton) {
            deleteChirpButton.setVisibility(View.VISIBLE);
            deleteChirpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(view.getId() == R.id.chirp_item_delete_button) {
                        int position = ((Integer) view.getTag()).intValue();
                        chirps.get(position).deleteInBackground();
                        chirps.remove(position);
                        notifyDataSetChanged();

                        Toast.makeText(context,
                                "Deleting Chirp",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                }
            });
        }
        else{
            favoriteChirpButton.setVisibility(View.VISIBLE);


            favoriteChirpButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if(isChecked) {
                        int position = ((Integer) compoundButton.getTag()).intValue();
                        chirps.get(position).addToFavorites(ParseUser.getCurrentUser());
                    }
                    else {
                        try {
                            int position = ((Integer) compoundButton.getTag()).intValue();

                            chirps.get(position).removeFromFavorites(ParseUser.getCurrentUser());
                        }  catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }



        textTitle.setText(chirps.get(position).getTitle());
        textExpDate.append(PRETTY_DATE_TIME.format(chirps.get(position).getExpirationDate()));

        return rowView;


    }
}
