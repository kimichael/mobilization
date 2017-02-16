package kimichael.com.yandexapp.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by mikim on 16.02.17.
 */

public class ArtistProvider extends ContentProvider {

    private ArtistsDbHelper mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    //Codes
    private static final int ARTIST = 100;
    private static final int GENRE = 200;

    static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ArtistsContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, ArtistsContract.PATH_ARTIST, ARTIST);
        uriMatcher.addURI(authority, ArtistsContract.PATH_GENRE, GENRE);
        return uriMatcher;
    }

    private static Cursor getArtistByID(Uri uri, String[] projection, String sortOrder){
        //TODO: implement this
        return null;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new ArtistsDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            case ARTIST:
                //TODO: implement this

        }

        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        //TODO: implement this
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //TODO: implement this
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //TODO: implement this
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //TODO: implement this
        return 0;
    }
}
