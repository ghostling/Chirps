package cs121.jam.chirps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.chirp_row_item, null, true);
        TextView textTitle = (TextView) rowView.findViewById(R.id.chirp_item_title);
        TextView textExpDate = (TextView) rowView.findViewById(R.id.chirp_item_exp_date);

        // The delete and favorite button never appear together, since they will always apply to different chirps
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
                    DeleteChirpDialog deleteChirpDialog = new DeleteChirpDialog(getContext(), position);

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

        // Days until chirp

        long different = chirps.get(position).getExpirationDate().getTime() - (new Date()).getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long weeksInMilli = daysInMilli * 7;

        // Show the most relevant time span till the chirp

        String showTime = "In ";

        long elapsedWeeks = different / weeksInMilli;
        different = different % weeksInMilli;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        if(elapsedWeeks != 0)
            showTime += elapsedWeeks + " weeks";
        else if(elapsedDays != 0)
            showTime += elapsedDays + " days";
        else if(elapsedHours != 0)
            showTime += elapsedHours + " hours";
        else
            showTime += elapsedMinutes + " minutes";
        textExpDate.setText(showTime);

        return rowView;


    }


    public class DeleteChirpDialog extends AlertDialog implements  DialogInterface.OnClickListener {
        private int chirpToDelete;
        protected DeleteChirpDialog(Context context, int pos) {
            super(context);
            chirpToDelete = pos;

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Are you sure?")
                    .setPositiveButton("Yes", this)
                    .setNegativeButton("No", this).show();
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            switch (i) {
                case DialogInterface.BUTTON_POSITIVE: // Yes button clicked
                    chirps.get(chirpToDelete).deleteInBackground();
                    chirps.remove(chirpToDelete);
                    notifyDataSetChanged();

                    Toast.makeText(context,
                            "Deleting Chirp",
                            Toast.LENGTH_LONG)
                            .show();
                    break;
                case DialogInterface.BUTTON_NEGATIVE: // No button clicked
                    // do nothing
                    Toast.makeText(getContext(), "Chirp not deleted", Toast.LENGTH_SHORT).show();
                    break; }
        }
    }

}
