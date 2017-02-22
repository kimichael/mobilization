package kimichael.com.yandexapp;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

//public class ArtistAdapter<T> extends ArrayAdapter<Artist> {
//
//    private ArrayList<Artist> objects;
//    private LayoutInflater inflater;
//
//    public ArtistAdapter(Context context, int textViewResourceId, ArrayList<Artist> objects) {
//        super(context, textViewResourceId, objects);
//        this.objects = objects;
//        inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//    }
//
//    static class ViewHolder{
//        public ImageView cover;
//        public TextView name, genres, albumTrackCount;
//    }
//
//
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//
//        ViewHolder holder;
//
//        if (convertView == null) {
//            convertView = inflater.inflate(R.layout.list_item_artist, parent, false);
//            holder = new ViewHolder();
//            holder.cover = (ImageView) convertView.findViewById(R.id.artist_cover_image);
//            holder.name = (TextView) convertView.findViewById(R.id.name);
//            holder.genres = (TextView) convertView.findViewById(R.id.genres);
//            holder.albumTrackCount = (TextView) convertView.findViewById(R.id.album_track_count);
//            convertView.setTag(holder);
//        } else {
//            holder = (ViewHolder) convertView.getTag();
//        }
//
//        Artist artist = objects.get(position);
//        holder.name.setText(artist.name);
//        Picasso.with(parent.getContext())
//                .load(artist.linkCoverSmall)
//                .placeholder(ContextCompat.getDrawable(getContext(), R.drawable.placeholder))
//                .into(holder.cover);
//        holder.genres.setText(TextUtils.join(", ", artist.genres));
//        holder.albumTrackCount.setText(String.format("%d альбомов, %d песен", artist.albums, artist.tracks));
//
//        if (!artist.isShowedAlready) {
//            artist.isShowedAlready = true;
//            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.list_item_animation);
//            convertView.startAnimation(animation);
//        }
//
//        return convertView;
//    }
//
//}

public class ArtistAdapter extends CursorAdapter {

    public ArtistAdapter(Context context, Cursor cursor, int flags) {
        super(context, cursor, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_artist, parent, false);
        ArtistViewHolder viewHolder = new ArtistViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ArtistViewHolder viewHolder = (ArtistViewHolder) view.getTag();

        String linkCoverSmall = cursor.getString(MainActivityFragment.COL_ARTIST_LINK_SMALL);
        ImageView cover = viewHolder.cover;
        Picasso.with(context)
                .load(linkCoverSmall)
                .placeholder(ContextCompat.getDrawable(context, R.drawable.placeholder))
                .into(cover);

        TextView nameTextView = viewHolder.name;
        String name = cursor.getString(MainActivityFragment.COL_ARTIST_NAME);
        nameTextView.setText(name);

        TextView albumTrackCountTextView = viewHolder.albumTrackCount;
        int albumCount = cursor.getInt(MainActivityFragment.COL_ARTIST_ALBUMS);
        int trackCount = cursor.getInt(MainActivityFragment.COL_ARTIST_TRACKS);
        albumTrackCountTextView.setText(String.format(context.getString(R.string.album_track_count_format), albumCount, trackCount));

        TextView genresTextView = viewHolder.genres;
        String genres = cursor.getString(MainActivityFragment.COL_ARTIST_GENRES);
        genresTextView.setText(genres);
    }
}