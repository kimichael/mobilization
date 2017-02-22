package kimichael.com.yandexapp.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.Nullable;

public class ArtistProvider extends ContentProvider {

    private ArtistsDbHelper mOpenHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    //Codes
    private static final int ARTIST = 100;
    private static final int ARTIST_WITH_ID = 101;
    private static final int GENRE = 200;

    static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ArtistsContract.CONTENT_AUTHORITY;

        uriMatcher.addURI(authority, ArtistsContract.PATH_ARTIST, ARTIST);
        uriMatcher.addURI(authority, ArtistsContract.PATH_ARTIST + "/#", ARTIST_WITH_ID);
        uriMatcher.addURI(authority, ArtistsContract.PATH_GENRE, GENRE);
        return uriMatcher;
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
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(ArtistsContract.ArtistEntry.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;
            case GENRE:
                retCursor = mOpenHelper.getReadableDatabase()
                        .query(ArtistsContract.GenreEntry.TABLE_NAME,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case ARTIST:
                return ArtistsContract.ArtistEntry.CONTENT_TYPE;
            case GENRE:
                return ArtistsContract.ArtistEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        long _id;
        Uri returnUri;

        switch (match) {
            case ARTIST: {
                _id = db.insert(ArtistsContract.ArtistEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = ArtistsContract.ArtistEntry.buildArtistUri(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            case GENRE: {
                _id = db.insert(ArtistsContract.GenreEntry.TABLE_NAME, null, values);
                if (_id > 0) {
                    returnUri = ArtistsContract.GenreEntry.buildGenreUri(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default:
                throw new UnsupportedOperationException("Failed to insert row into " + uri);

        }
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rowsDeleted;
        if ( null == selection ) selection = "1";
        switch (match) {
            case ARTIST:
                rowsDeleted = db.delete(ArtistsContract.ArtistEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case GENRE:
                rowsDeleted = db.delete(ArtistsContract.ArtistEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int rowsUpdated;
        if ( null == selection ) selection = "1";
        switch (match) {
            case ARTIST:
                rowsUpdated = db.update(ArtistsContract.ArtistEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case GENRE:
                rowsUpdated = db.update(ArtistsContract.ArtistEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
