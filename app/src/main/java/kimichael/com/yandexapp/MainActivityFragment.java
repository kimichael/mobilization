package kimichael.com.yandexapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    private ArtistAdapter<Artist> artistAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.refresh_button){
            FetchArtistsTask fetchArtistsTask = new FetchArtistsTask();
            fetchArtistsTask.execute();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);

        ArrayList<Artist> artistsArray = new ArrayList();

        artistAdapter = new ArtistAdapter<>(
                getActivity(),
                R.layout.list_item_artist,
                artistsArray
        );
        ListView artistList = (ListView) rootview.findViewById(R.id.artist_list_view);
        artistList.setAdapter(artistAdapter);

        return rootview;
    }

    public class FetchArtistsTask extends AsyncTask<Void,Void,Artist[]>{

        String LOG_TAG = FetchArtistsTask.class.getSimpleName();

        private Artist[] getArtistsDataFromJSON(String JSONData) throws JSONException{

            final String TAG_ID = "id";
            final String TAG_NAME = "name";
            final String TAG_GENRES = "genres";
            final String TAG_TRACKS = "tracks";
            final String TAG_ALBUMS = "albums";
            final String TAG_LINK = "link";
            final String TAG_DESCRIPTION = "description";
            final String TAG_COVER = "cover";
            final String TAG_SMALL = "small";
            final String TAG_BIG = "big";

            JSONArray artistsJson = new JSONArray(JSONData);
            Artist[] artists = new Artist[artistsJson.length()];
            for (int i = 0; i < artistsJson.length(); i++){
                JSONObject artistJson = artistsJson.getJSONObject(i);

                artists[i] = new Artist();

                artists[i].setId(artistJson.getInt(TAG_ID));
                artists[i].setName(artistJson.getString(TAG_NAME));

                JSONArray genres = artistJson.getJSONArray(TAG_GENRES);
                ArrayList<String> list = new ArrayList<String>();
                for (int j=0; j<genres.length(); j++) {
                    list.add( genres.getString(j) );
                }
                String[] genresArray = list.toArray(new String[list.size()]);
                artists[i].setGenres(genresArray);

                artists[i].setTracks(artistJson.getInt(TAG_TRACKS));
                artists[i].setAlbums(artistJson.getInt(TAG_ALBUMS));
                try{artists[i].setLink(artistJson.getString(TAG_LINK));}catch (JSONException e){artists[i].setLink("");}
                artists[i].setDescription(artistJson.getString(TAG_DESCRIPTION));

                JSONObject covers = artistJson.getJSONObject(TAG_COVER);
                artists[i].setCoverSmall(getBitmapFromURL(covers.getString(TAG_SMALL)));
                artists[i].setLinkCoverBig(covers.getString(TAG_BIG));

            }
            return artists;
        }

        public Bitmap getBitmapFromURL(String src) {
            try {
                java.net.URL url = new java.net.URL(src);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected Artist[] doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String artistsJSON = null;

            try{
                URL url = new URL("https://cache-default06g.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null){
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null){
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0){
                    return null;
                }
                artistsJSON = buffer.toString();

            } catch (IOException e){
                Log.e(LOG_TAG, "Error: ", e);
                return null;

            }finally{
                if (urlConnection != null){
                    urlConnection.disconnect();
                }

                if (reader != null){
                    try{
                        reader.close();
                    }catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                        e.printStackTrace();
                    }
                }
            }
            try{
                return getArtistsDataFromJSON(artistsJSON);
            } catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e );
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Artist[] artists) {
            if (artists != null){
                artistAdapter.clear();
                for (Artist artist : artists){
                    artistAdapter.add(artist);
                }
            }
        }
    }

}
