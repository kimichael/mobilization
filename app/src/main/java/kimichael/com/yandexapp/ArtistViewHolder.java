package kimichael.com.yandexapp;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by mikim on 18.02.17.
 */

public class ArtistViewHolder {

    public final ImageView cover;
    public final TextView name;
    public final TextView genres;
    public final TextView albumTrackCount;

    public ArtistViewHolder(View view) {
        cover = (ImageView) view.findViewById(R.id.artist_cover_image);
        name = (TextView) view.findViewById(R.id.name);
        genres = (TextView) view.findViewById(R.id.genres);
        albumTrackCount = (TextView) view.findViewById(R.id.album_track_count);
    }
}
