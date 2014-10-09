package cs121.jam.chirps;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by jiexicao on 10/9/14.
 */
public class ChirpList extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] title;
    private final String[] expDate;
    public ChirpList(Activity context,
                      String[] title, String[] expDate) {
        super(context, R.layout.chirp_row_item, title);
        this.context = context;
        this.title = title;
        this.expDate = expDate;
    }
    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.chirp_row_item, null, true);
        TextView textTitle = (TextView) rowView.findViewById(R.id.chirp_item_title);
        TextView textExpDate = (TextView) rowView.findViewById(R.id.chirp_item_exp_date);
        textTitle.setText(title[position]);
        textExpDate.setText(expDate[position]);
        return rowView;
    }
}
