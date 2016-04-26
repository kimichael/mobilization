package kimichael.com.yandexapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ArtistAdapter<T> extends ArrayAdapter<Artist> {

    private ArrayList<Artist> objects;
    private LayoutInflater inflater;

    public ArtistAdapter(Context context, int textViewResourceId, ArrayList<Artist> objects) {
        super(context, textViewResourceId, objects);
        this.objects = objects;
        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    static class ViewHolder{
        public ImageView cover;
        public TextView name, genres, albumTrackCount;
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_artist, parent, false);
            holder = new ViewHolder();
            holder.cover = (ImageView) convertView.findViewById(R.id.artist_cover_image);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.genres = (TextView) convertView.findViewById(R.id.genres);
            holder.albumTrackCount = (TextView) convertView.findViewById(R.id.album_track_count);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Artist artist = objects.get(position);
        holder.name.setText(artist.name);
        Picasso.with(parent.getContext())
                .load(artist.linkCoverSmall)
                .placeholder(ContextCompat.getDrawable(getContext(), R.drawable.placeholder))
                .into(holder.cover);
        holder.genres.setText(TextUtils.join(", ", artist.genres));
        holder.albumTrackCount.setText(String.format("%d альбомов, %d песен", artist.albums, artist.tracks));

        if (!artist.isShowedAlready) {
            artist.isShowedAlready = true;
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.list_item_animation);
            convertView.startAnimation(animation);
        }

        return convertView;
    }

}
