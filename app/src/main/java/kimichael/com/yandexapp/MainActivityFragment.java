package kimichael.com.yandexapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivityFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public MainActivityFragment() {}

    private ArtistAdapter<Artist> mArtistAdapter;
    private ListView mArtistListView;
    SharedPreferences mSharedPreferences;
    private ArrayList<Artist> mArtistList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    boolean isLoadingData = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_fragment, menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootview = inflater.inflate(R.layout.fragment_main, container, false);
        mArtistList = new ArrayList<>();
        //Refresher indicator that appears on swiping to bottom at the top of the list
        mSwipeRefreshLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary, R.color.colorPrimaryDark);
        mArtistAdapter = new ArtistAdapter<>(
                getActivity(),
                R.layout.list_item_artist,
                mArtistList);

        mArtistListView = (ListView) rootview.findViewById(R.id.artist_list_view);
        mArtistListView.setAdapter(mArtistAdapter);
        mArtistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Artist artist = mArtistAdapter.getItem(position);
                Intent detailIntent = new Intent(getActivity(), ArtistDetailActivity.class)
                        .putExtra("artist", artist);
                startActivity(detailIntent);
            }
        });
        mArtistListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        mArtistListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });


        if (savedInstanceState != null){
            //Restore our list of artists
            mArtistList = savedInstanceState.getParcelableArrayList("mArtistList");
            for (Artist artist : mArtistList){
                mArtistAdapter.add(artist);
            }
            mArtistAdapter.notifyDataSetChanged();
        } else {
            refresh();
        }
        return rootview;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_refresh){
            refresh();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Save out list of artists
        outState.putParcelableArrayList("mArtistList", mArtistList);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(true);
        refresh();
    }



    private void refresh(){
        //Check for Internet
        if (!(isNetworkAvailable())){Toast.makeText(getContext(),
                getString(R.string.no_connection),
                Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.setRefreshing(false);}
        //Load data
        if (!(isLoadingData)) {
            mSwipeRefreshLayout.setRefreshing(true);
            mArtistAdapter.clear();
            mArtistAdapter.notifyDataSetChanged();
            mArtistList = new ArrayList<>();
            FetchArtistsTask fetchArtistsTask = new FetchArtistsTask();
            fetchArtistsTask.execute();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public class FetchArtistsTask extends AsyncTask<Integer,Void,ArrayList<Artist>>{

        public static final String LAST_MODIFIED = "Last-Modified";
        public static final String LAST_RESPONSE = "lastResponse";
        public static final String FETCH_JSON_URL = "https://cache-default06g.cdn.yandex.net/download.cdn.yandex.net/mobilization-2016/artists.json";
        String LOG_TAG = FetchArtistsTask.class.getSimpleName();

        private ArrayList<Artist> getArtistsDataFromJSON(String JSONData) throws JSONException{

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

            JSONArray mArtistsJSON = new JSONArray(JSONData);

            for (int i = 0; i < mArtistsJSON.length(); i++){
                JSONObject artistJson = mArtistsJSON.getJSONObject(i);
                Artist artist = new Artist();

                artist.id = artistJson.getInt(TAG_ID);
                artist.name = artistJson.getString(TAG_NAME);
                JSONArray genres = artistJson.getJSONArray(TAG_GENRES);
                ArrayList<String> list = new ArrayList<String>();
                for (int j=0; j<genres.length(); j++) {list.add( genres.getString(j) );}
                String[] genresArray = list.toArray(new String[list.size()]);
                artist.genres = (genresArray);
                artist.tracks = artistJson.getInt(TAG_TRACKS);
                artist.albums = artistJson.getInt(TAG_ALBUMS);
                try{artist.link = artistJson.getString(TAG_LINK);} catch (JSONException e){artist.link = "";}
                artist.description = artistJson.getString(TAG_DESCRIPTION);
                JSONObject covers = artistJson.getJSONObject(TAG_COVER);
                artist.linkCoverSmall = covers.getString(TAG_SMALL);
                artist.linkCoverBig = covers.getString(TAG_BIG);

                mArtistList.add(i, artist);
            }
            return mArtistList;
        }

        @Override
        protected void onPreExecute() {
            mSwipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected ArrayList<Artist> doInBackground(Integer... params) {
            //Simple connection and fetching JSON data
            String artistsData = null;
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            Date lastModifiedDate = null;
            Date lastCachedDate = null;
            String lastData = mSharedPreferences.getString("lastData", null);
            String lastModified = null;

            if (!(isNetworkAvailable() && lastData == null)){
                try {
                return getArtistsDataFromJSON(lastData);
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }

            try{

                URL url = new URL(FETCH_JSON_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();


                //Checking if we can get data from cache and loading from cache if we need
                lastModified = urlConnection.getHeaderField(LAST_MODIFIED);
                String cachedLastResponse = mSharedPreferences.getString(LAST_RESPONSE, null);
                SimpleDateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss ZZZ", Locale.ENGLISH);
                if (cachedLastResponse != null && lastData != null) {
                    try {
                        lastModifiedDate = formatter.parse(lastModified);
                        lastCachedDate = formatter.parse(cachedLastResponse);
                    } catch (ParseException e){
                        e.printStackTrace();
                    }

                    if (lastCachedDate.compareTo(lastModifiedDate) == 0) {
                        try {
                            return getArtistsDataFromJSON(lastData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                //Retrieving data from server
                if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK){return null;}
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
                //Saving data to shared preferences
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putString("lastData", artistsData);
                editor.putString("lastResponse", lastModified);
                editor.commit();

                return getArtistsDataFromJSON(artistsData);
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
                    mArtistAdapter.add(artist);
                    mArtistAdapter.notifyDataSetChanged();
                }
            }
            isLoadingData = false;
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

}
