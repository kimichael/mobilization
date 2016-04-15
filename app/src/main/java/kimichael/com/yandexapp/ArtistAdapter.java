package kimichael.com.yandexapp;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ArtistAdapter<T> extends ArrayAdapter<Artist> {

    private ArrayList<Artist> objects;

    public ArtistAdapter(Context context, int textViewResourceId, ArrayList<Artist> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_item_artist, null);
        }

        Artist artist = objects.get(position);

        if (artist != null){
            ImageView cover = (ImageView) v.findViewById(R.id.artist_cover_image);
            TextView name = (TextView) v.findViewById(R.id.name);
            TextView genres = (TextView) v.findViewById(R.id.genres);
            TextView trackCount = (TextView) v.findViewById(R.id.album_track_count);

            if (cover != null){
                cover.setImageBitmap(artist.getCoverSmall());
            }

            if (name != null){
                name.setText(artist.getName());
            }

            if (genres != null){
                genres.setText(TextUtils.join(", ", artist.getGenres()));
            }

            if (trackCount != null){
                trackCount.setText(Integer.toString(artist.getAlbums()) + " альбомов, "+ Integer.toString(artist.getTracks()) + " песен");
            }
        }
    return v;
    }
}
