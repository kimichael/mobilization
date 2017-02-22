package kimichael.com.yandexapp.provider;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by mikim on 16.02.17.
 */

public class ArtistsContract {

    public static final String CONTENT_AUTHORITY = "kimichael.com.yandexapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ARTIST = "artist";
    public static final String PATH_GENRE = "genre";
    public static final String PATH_ARTISTS_GENRE = "artists_genre";

    public static final class ArtistEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTIST).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTIST;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTIST;

        public static final String TABLE_NAME = "artist";
        /*  public int id;
        public String name;
        public String[] genres;
        public int tracks, albums;
        public String link;
        public String description;
        public String linkCoverBig, linkCoverSmall;
        public boolean isShowedAlready; */
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_GENRES = "genres";
        public static final String COLUMN_TRACKS = "tracks";
        public static final String COLUMN_ALBUMS = "albums";
        public static final String COLUMN_LINK = "link";
        public static final String COLUMN_DESC = "desc";
        public static final String COLUMN_LINK_BIG = "link_big";
        public static final String COLUMN_LINK_SMALL = "link_small";

        public static Uri buildArtistUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class GenreEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_GENRE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GENRE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GENRE;

        public static final String TABLE_NAME = "genre";

        public static final String COLUMN_NAME = "name";

        public static Uri buildGenreUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class ArtistsGenresEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_ARTISTS_GENRE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTISTS_GENRE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_ARTISTS_GENRE;

        public static final String TABLE_NAME = "artists_genre";

        public static final String COLUMN_ARTIST_ID = "artist_id";
        public static final String COLUMN_GENRE_ID = "genre_id";

    }
}
