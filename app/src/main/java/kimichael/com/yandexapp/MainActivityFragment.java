package kimichael.com.yandexapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
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

import kimichael.com.yandexapp.provider.ArtistsContract;

public class MainActivityFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, LoaderManager.LoaderCallbacks<Cursor> {

    public MainActivityFragment() {}

    private ArtistAdapter mArtistAdapter;
    private ListView mArtistListView;
    SharedPreferences mSharedPreferences;
    private ArrayList<Artist> mArtistList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    boolean isLoadingData = false;

    private static final int ARTIST_LOADER = 0;
    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private static final String[] ARTIST_COLUMNS = new String[] {
            ArtistsContract.ArtistEntry.TABLE_NAME + "." + ArtistsContract.ArtistEntry._ID,
            ArtistsContract.ArtistEntry.COLUMN_NAME,
            ArtistsContract.ArtistEntry.COLUMN_ALBUMS,
            ArtistsContract.ArtistEntry.COLUMN_GENRES,
            ArtistsContract.ArtistEntry.COLUMN_LINK,
            ArtistsContract.ArtistEntry.COLUMN_DESC,
            ArtistsContract.ArtistEntry.COLUMN_LINK_SMALL,
            ArtistsContract.ArtistEntry.COLUMN_LINK_BIG,
            ArtistsContract.ArtistEntry.COLUMN_TRACKS,
    };

    static final int COL_ARTIST_ID = 0;
    static final int COL_ARTIST_NAME = 1;
    static final int COL_ARTIST_ALBUMS = 2;
    static final int COL_ARTIST_GENRES = 3;
    static final int COL_ARTIST_LINK = 4;
    static final int COL_ARTIST_DESC = 5;
    static final int COL_ARTIST_LINK_SMALL = 6;
    static final int COL_ARTIST_LINK_BIG = 7;
    static final int COL_ARTIST_TRACKS = 8;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        setHasOptionsMenu(true);
        getLoaderManager().initLoader(ARTIST_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = ArtistsContract.BASE_CONTENT_URI.buildUpon()
                .appendPath(ArtistsContract.PATH_ARTIST).build();

        String sortOrder = ArtistsContract.ArtistEntry.COLUMN_NAME + " ASC";
        return new CursorLoader(getContext(), uri, ARTIST_COLUMNS,
                null,
                null,
                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mArtistAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mArtistAdapter.swapCursor(null);
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
        mArtistAdapter = new ArtistAdapter(getContext(), null, 0);

        mArtistListView = (ListView) rootview.findViewById(R.id.artist_list_view);
        mArtistListView.setAdapter(mArtistAdapter);
        mArtistListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Artist artist = (Artist) mArtistAdapter.getItem(position);
                Intent detailIntent = new Intent(getActivity(), ArtistDetailActivity.class)
                        .putExtra("artist", artist);
                startActivity(detailIntent);
            }
        });
        mArtistListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
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
        if (!(isNetworkAvailable())){
            Snackbar.make(getView(),
                getString(R.string.no_connection),
                Snackbar.LENGTH_SHORT).show();
            mSwipeRefreshLayout.setRefreshing(false);}
        //Load data
        if (!(isLoadingData)) {
            mSwipeRefreshLayout.setRefreshing(true);
            mArtistAdapter.notifyDataSetChanged();
            mArtistList = new ArrayList<>();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
