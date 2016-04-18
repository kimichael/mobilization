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
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;

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
    boolean isLoading = false;
    private JSONArray artistsJSON;
    ArrayList<Artist> artists;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refresh_button){}

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
                artistsArray);

        artists = new ArrayList<>();
        ListView artistList = (ListView) rootview.findViewById(R.id.artist_list_view);
        ProgressBar progressBar = (ProgressBar) rootview.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        artistList.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {}

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                int lastVisibleItem = firstVisibleItem + visibleItemCount;
                int threshold = 10;
                if ((lastVisibleItem >= totalItemCount - threshold) && !(isLoading)){
                    isLoading = true;
                    FetchArtistsTask fetchArtistsTask = new FetchArtistsTask();
                    fetchArtistsTask.execute(lastVisibleItem, visibleItemCount);
                }
            }
        });
        artistList.setAdapter(artistAdapter);

        return rootview;
    }

    public class FetchArtistsTask extends AsyncTask<Integer,Void,ArrayList<Artist>>{

        String LOG_TAG = FetchArtistsTask.class.getSimpleName();

        private ArrayList<Artist> getArtistsDataFromJSON(String JSONData,
                                                         int lastVisibleCount,
                                                         int visibleItemCount) throws JSONException{
            if (visibleItemCount == 0) {visibleItemCount = 7;}

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


            if (JSONData != null){artistsJSON = new JSONArray(JSONData);}

            ArrayList<Artist> artistsToAdd = new ArrayList<>();
            int size = artists.size();
            for (int i = size; i < size + visibleItemCount; i++){
                JSONObject artistJson = artistsJSON.getJSONObject(i);

                Artist artist = new Artist();

                artist.setId(artistJson.getInt(TAG_ID));
                artist.setName(artistJson.getString(TAG_NAME));
                JSONArray genres = artistJson.getJSONArray(TAG_GENRES);
                ArrayList<String> list = new ArrayList<String>();
                for (int j=0; j<genres.length(); j++) {
                    list.add( genres.getString(j) );
                }
                String[] genresArray = list.toArray(new String[list.size()]);
                artist.setGenres(genresArray);
                artist.setTracks(artistJson.getInt(TAG_TRACKS));
                artist.setAlbums(artistJson.getInt(TAG_ALBUMS));
                try{artist.setLink(artistJson.getString(TAG_LINK));}catch (JSONException e){artist.setLink("");}
                artist.setDescription(artistJson.getString(TAG_DESCRIPTION));
                JSONObject covers = artistJson.getJSONObject(TAG_COVER);
                artist.setCoverSmall(getBitmapFromURL(covers.getString(TAG_SMALL)));
                artist.setLinkCoverBig(covers.getString(TAG_BIG));

                artists.add(i, artist);
                artistsToAdd.add(artist);

            }
            return artistsToAdd;
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
        protected ArrayList<Artist> doInBackground(Integer... params) {
            String artistsData = null;

            if (artistsJSON != null){
                try{
                    return getArtistsDataFromJSON(artistsData, params[0], params[1]);
                } catch (JSONException e){
                    Log.e(LOG_TAG, "Error: ", e);
                    return null;
                }
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            if (params == null){
                return null;
            }

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
                artistsData = buffer.toString();

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
                return getArtistsDataFromJSON(artistsData, params[0], params[1]);
            } catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e );
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Artist> artists) {
            if (artists != null){
                for (Artist artist : artists){
                    artistAdapter.add(artist);
                }
            }
            isLoading = false;
        }
    }

}
