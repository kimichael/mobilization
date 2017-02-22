package kimichael.com.yandexapp;

import android.content.Intent;
import android.media.Image;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.PathInterpolatorCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Locale;

public class ArtistDetailActivityFragment extends Fragment {

    ImageView mCover;

    public ArtistDetailActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_artist_detail, container, false);
        Intent artistIntent = getActivity().getIntent();
        Artist artist = artistIntent.getParcelableExtra("artist");

        mCover = (ImageView) rootview.findViewById(R.id.artist_cover_image);
        ((TextView) rootview.findViewById(R.id.name))
                .setText(artist.name);
        ((TextView) rootview.findViewById(R.id.genres))
                .setText(TextUtils.join(", ", artist.genres));
        ((TextView) rootview.findViewById(R.id.biography))
                .setText(artist.description.substring(0, 1).toUpperCase() + artist.description.substring(1));
        ((TextView) rootview.findViewById(R.id.album_track_count))
                .setText(String.format("%d альбомов, %d песен", artist.albums, artist.tracks));
        Picasso.with(getContext())
                .load(artist.linkCoverBig)
                .placeholder(ContextCompat.getDrawable(getContext(), R.drawable.placeholder))
                .into(mCover);
        return rootview;
    }
}