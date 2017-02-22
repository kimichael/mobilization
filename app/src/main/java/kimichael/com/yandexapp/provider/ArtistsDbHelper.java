package kimichael.com.yandexapp.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import kimichael.com.yandexapp.Artist;
import kimichael.com.yandexapp.provider.ArtistsContract.ArtistEntry;
import kimichael.com.yandexapp.provider.ArtistsContract.GenreEntry;
import kimichael.com.yandexapp.provider.ArtistsContract.ArtistsGenresEntry;

public class ArtistsDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;

    private static final String DATABASE_NAME = "artists.db";

    public ArtistsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_ARTISTS_TABLE = "CREATE TABLE " + ArtistEntry.TABLE_NAME + " (" +
                ArtistEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ArtistEntry.COLUMN_NAME + " TEXT UNIQUE NOT NULL, " +
                ArtistEntry.COLUMN_ALBUMS + " INTEGER, " +
                ArtistEntry.COLUMN_GENRES + " TEXT, " +
                ArtistEntry.COLUMN_LINK + " TEXT, " +
                ArtistEntry.COLUMN_DESC + " TEXT, " +
                ArtistEntry.COLUMN_LINK_SMALL + " TEXT, " +
                ArtistEntry.COLUMN_LINK_BIG + " TEXT, " +
                ArtistEntry.COLUMN_TRACKS + " INTEGER " + ");";

        final String SQL_CREATE_GENRES_TABLE = "CREATE TABLE " + GenreEntry.TABLE_NAME + " (" +
                GenreEntry.COLUMN_NAME + " TEXT UNIQUE " + ");";

        final String SQL_CREATE_ARTISTS_GENRES_TABLE = "CREATE TABLE " + ArtistsGenresEntry.TABLE_NAME + "(" +
                ArtistsGenresEntry.COLUMN_ARTIST_ID + " INTEGER, " +
                ArtistsGenresEntry.COLUMN_GENRE_ID + " INTEGER, " +
                "FOREIGN KEY(" + ArtistsGenresEntry.COLUMN_ARTIST_ID + ") REFERENCES "
                                    + ArtistEntry.TABLE_NAME + "(" + ArtistEntry._ID + ") , " +
                "FOREIGN KEY(" + ArtistsGenresEntry.COLUMN_GENRE_ID + ") REFERENCES "
                                    + GenreEntry.TABLE_NAME + "(" + GenreEntry._ID + ")" + ")";

        db.execSQL(SQL_CREATE_ARTISTS_TABLE);
//        db.execSQL(SQL_CREATE_GENRES_TABLE);
//        db.execSQL(SQL_CREATE_ARTISTS_GENRES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ArtistsGenresEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ArtistEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + GenreEntry.TABLE_NAME);
        onCreate(db);
    }
}
