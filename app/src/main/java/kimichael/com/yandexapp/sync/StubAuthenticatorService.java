package kimichael.com.yandexapp.sync;

import android.accounts.Account;
import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.util.Log;

import kimichael.com.yandexapp.R;
import kimichael.com.yandexapp.provider.ArtistsContract;

public class StubAuthenticatorService extends Service {

    // Instance field that stores the authenticator object
    private static final String LOG_TAG = StubAuthenticatorService.class.getSimpleName();
    private static final String ACCOUNT_TYPE = "kimichael.com.yandexapp";
    public static final String ACCOUNT_NAME = "YandexApp";
    private StubAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        Log.i(LOG_TAG, "Service created");
        mAuthenticator = new StubAuthenticator(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "Service destroyed");
    }

    public static Account getAccount() {
        final String accountName = ACCOUNT_NAME;
        return new Account(accountName, ACCOUNT_TYPE);
    }
    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
