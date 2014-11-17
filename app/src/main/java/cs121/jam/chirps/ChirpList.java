package cs121.jam.chirps;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import cs121.jam.model.Chirp;

/**
 * Created by jiexicao on 10/9/14.
 */
public class ChirpList extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> title;
    private final ArrayList<Date> expDate;
    private final ArrayList<String> objectId;
    private final boolean deleteButton;
    public static SimpleDateFormat PRETTY_DATE_TIME = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm a");

    public ChirpList(Activity context,
                     ArrayList<String> title, ArrayList<Date> expDate, ArrayList<String> objectId, boolean deleteButton) {
        super(context, R.layout.chirp_row_item, title);
        this.context = context;
        this.title = title;
        this.expDate = expDate;
        this.objectId = objectId;
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
        if(deleteButton)
            deleteChirpButton.setVisibility(View.VISIBLE);
        else
            favoriteChirpButton.setVisibility(View.VISIBLE);

        deleteChirpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseQuery<Chirp> chirpQuery = ParseQuery.getQuery(Chirp.class);
                Chirp chirp = null;
                try {
                    int position = ((Integer) view.getTag()).intValue();
                    String id = objectId.get(position);
                    Log.e("Chirp Details", "Trying to delete chirpid: " + id);
                    chirp = chirpQuery.get(id);
                    if(chirp != null)
                        Log.e("Chirp Details", "Chirp was created " + view.getTag().toString());
                    chirp.deleteInBackground();
                    title.remove(position);
                    expDate.remove(position);
                    objectId.remove(position);
                    notifyDataSetChanged();
                } catch (ParseException pe) {
                    Log.e("Chirp Details", pe.getMessage());
                }

                Toast.makeText(context,
                        "Deleting Chirp",
                        Toast.LENGTH_LONG)
                        .show();
            }
        });

        favoriteChirpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        textTitle.setText(title.get(position));
        textExpDate.append(PRETTY_DATE_TIME.format(expDate.get(position)));

        return rowView;


    }
}
