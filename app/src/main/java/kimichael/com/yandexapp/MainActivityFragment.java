package kimichael.com.yandexapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

import kimichael.com.yandexapp.provider.ArtistsContract;
import kimichael.com.yandexapp.sync.SyncUtils;

public class MainActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener {

    public MainActivityFragment() {}

    private ArtistAdapter mArtistAdapter;
    private ListView mArtistListView;
    SharedPreferences mSharedPreferences;
    private ArrayList<Artist> mArtistList;
    boolean isLoadingData = false;

    private static final int ARTIST_LOADER = 0;
    private static final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        SyncUtils.createSyncAccount(context);
    }

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

    SwipeRefreshLayout mSwipeRefreshLayout;

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
        mArtistAdapter = new ArtistAdapter(getContext(), null, 0);

        mArtistListView = (ListView) rootview.findViewById(R.id.artist_list_view);
        mArtistListView.setAdapter(mArtistAdapter);
        mArtistListView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootview.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        return rootview;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Save out list of artists
        outState.putParcelableArrayList("mArtistList", mArtistList);
        super.onSaveInstanceState(outState);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onRefresh() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
