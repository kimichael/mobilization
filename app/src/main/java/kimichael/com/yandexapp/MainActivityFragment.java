package kimichael.com.yandexapp;

import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    private ArtistAdapter<Artist> artistAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_main, container, false);

        //First initialization of listView
        FetchArtistsTask fetchArtistsTask = new FetchArtistsTask();
        fetchArtistsTask.execute();

        ArrayList<Artist> artistsArray = new ArrayList();

        artistAdapter = new ArtistAdapter(
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

            }
        }
    }

}
