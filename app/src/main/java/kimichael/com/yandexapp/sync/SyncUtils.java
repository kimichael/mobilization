package kimichael.com.yandexapp.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;

import kimichael.com.yandexapp.provider.ArtistsContract;

/**
 * Created by mikim on 26.02.17.
 */

public class SyncUtils {

    private static final long SYNC_FREQUENCY = 60 * 60;
    private static final String CONTENT_AUTHORITY = ArtistsContract.CONTENT_AUTHORITY;
    private static final String PREF_SETUP_COMPLETE = "pref_setup_complete";

    public static void createSyncAccount(Context context) {
        boolean newAccount = false;
        boolean setupComplete = PreferenceManager
                .getDefaultSharedPreferences(context).getBoolean(PREF_SETUP_COMPLETE, false);

        Account account = StubAuthenticatorService.getAccount();
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        if (accountManager.addAccountExplicitly(account, null, null)) {
            ContentResolver.setIsSyncable(account, CONTENT_AUTHORITY, 1);
            ContentResolver.setSyncAutomatically(account, CONTENT_AUTHORITY, true);
            ContentResolver.addPeriodicSync(account, CONTENT_AUTHORITY, new Bundle(), SYNC_FREQUENCY);
            newAccount = true;
        }

        if (newAccount || !setupComplete) {
            refresh();
            PreferenceManager.getDefaultSharedPreferences(context).edit()
                    .putBoolean(PREF_SETUP_COMPLETE, true).apply();
        }
    }

    public static void refresh() {
        Bundle b = new Bundle();
        b.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.requestSync(
                StubAuthenticatorService.getAccount(),
                ArtistsContract.CONTENT_AUTHORITY,
                b
        );
    }
}
