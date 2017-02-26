package kimichael.com.yandexapp.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import kimichael.com.yandexapp.provider.ArtistsContract;

public class ArtistSyncAdapter extends AbstractThreadedSyncAdapter {

    private static final String ARTISTS_FETCH_URL = "https://cache-default06g.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json";
    private static final String LOG_TAG = ArtistSyncAdapter.class.getSimpleName();

    public ArtistSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        HttpURLConnection httpURLConnection = null;
        BufferedReader reader = null;

        try {

            URL url = new URL(ARTISTS_FETCH_URL);
            String artistsJSONStr = null;

            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();

            InputStream inputStream = httpURLConnection.getInputStream();
            if (null == inputStream)
                return;

            reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer = new StringBuffer();

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line + '\n');
            }

            Log.d(LOG_TAG, stringBuffer.toString());

            if (stringBuffer.length() == 0) {
                return;
            }

            artistsJSONStr = stringBuffer.toString();
            getArtistDataFromJSON(artistsJSONStr);

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        } finally {
            if (null != httpURLConnection) {
                httpURLConnection.disconnect();
            }
            if (null != reader) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error closing reader");
                    e.printStackTrace();
                }
            }
        }

    }

    private void getArtistDataFromJSON(String artistsJSONStr) {

        final String ID_KEY = "id";
        final String NAME_KEY = "name";
        final String GENRES_KEY = "genres";
        final String TRACKS_KEY = "tracks";
        final String ALBUMS_KEY = "albums";
        final String LINK_KEY = "link";
        final String DESC_KEY = "description";
        final String COVER_KEY = "cover";
        final String COVER_SMALL_KEY = "small";
        final String COVER_BIG_KEY = "big";

        try {
            JSONArray artistsJSONArray = new JSONArray(artistsJSONStr);
            List<ContentValues> contentValuesVector = new Vector<>(artistsJSONArray.length());

            for (int i = 0; i < artistsJSONArray.length(); i++){
                JSONObject artistJSON = artistsJSONArray.getJSONObject(i);

                int id = artistJSON.getInt(ID_KEY);
                String name = artistJSON.getString(NAME_KEY);

                JSONArray genresJSON = artistJSON.getJSONArray(GENRES_KEY);
                ArrayList<String> list = new ArrayList<>();
                for (int j=0; j<genresJSON.length(); j++) {list.add( genresJSON.getString(j) );}
                String genres = TextUtils.join(", ", list);

                int tracks = artistJSON.getInt(TRACKS_KEY);
                int albums = artistJSON.getInt(ALBUMS_KEY);
                String link = artistJSON.getString(LINK_KEY);
                String description = artistJSON.getString(DESC_KEY);
                JSONObject cover = artistJSON.getJSONObject(COVER_KEY);
                String coverSmallLink = cover.getString(COVER_SMALL_KEY);
                String coverBigLink = cover.getString(COVER_BIG_KEY);

                ContentValues artistValues = new ContentValues();
                artistValues.put(ArtistsContract.ArtistEntry.COLUMN_ID, id);
                artistValues.put(ArtistsContract.ArtistEntry.COLUMN_NAME, name);
                artistValues.put(ArtistsContract.ArtistEntry.COLUMN_GENRES, genres);
                artistValues.put(ArtistsContract.ArtistEntry.COLUMN_TRACKS, tracks);
                artistValues.put(ArtistsContract.ArtistEntry.COLUMN_ALBUMS, albums);
                artistValues.put(ArtistsContract.ArtistEntry.COLUMN_LINK, link);
                artistValues.put(ArtistsContract.ArtistEntry.COLUMN_DESC, description);
                artistValues.put(ArtistsContract.ArtistEntry.COLUMN_LINK_SMALL, coverSmallLink);
                artistValues.put(ArtistsContract.ArtistEntry.COLUMN_LINK_BIG, coverBigLink);

                contentValuesVector.add(artistValues);

            }

            int rowsInserted = 0;
            if (contentValuesVector.size() > 0) {
                ContentValues[] contentValues = new ContentValues[contentValuesVector.size()];
                contentValuesVector.toArray(contentValues);

                rowsInserted = getContext().getContentResolver().bulkInsert(ArtistsContract.ArtistEntry.CONTENT_URI,
                        contentValues);

                }

            Log.d(LOG_TAG, "Inserted " + rowsInserted + " rows");

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing JSON ", e);
        }
    }
}
