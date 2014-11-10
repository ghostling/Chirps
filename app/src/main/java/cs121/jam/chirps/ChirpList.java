package cs121.jam.chirps;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by jiexicao on 10/9/14.
 */
public class ChirpList extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] title;
    private final Date[] expDate;
    private final boolean deleteButton;
    public static SimpleDateFormat PRETTY_DATE_TIME = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm a");

    public ChirpList(Activity context,
                      String[] title, Date[] expDate, boolean deleteButton) {
        super(context, R.layout.chirp_row_item, title);
        this.context = context;
        this.title = title;
        this.expDate = expDate;
        this.deleteButton = deleteButton;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.chirp_row_item, null, true);
        TextView textTitle = (TextView) rowView.findViewById(R.id.chirp_item_title);
        TextView textExpDate = (TextView) rowView.findViewById(R.id.chirp_item_exp_date);

        /*
        Button deleteChirpButton = (Button) rowView.findViewById(R.id.chirp_item_delete_button);
        Button favoriteChirpButton = (Button) rowView.findViewById(R.id.chirp_item_favorite_button);

        if(deleteButton)
            favoriteChirpButton.setVisibility(View.GONE);
        else
            deleteChirpButton.setVisibility(View.GONE);
        */

        textTitle.setText(title[position]);
        textExpDate.append(PRETTY_DATE_TIME.format(expDate[position]));

        return rowView;
    }
}
