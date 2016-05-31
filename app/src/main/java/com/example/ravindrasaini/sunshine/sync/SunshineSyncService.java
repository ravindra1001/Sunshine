package com.example.ravindrasaini.sunshine.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by ravindrasaini on 5/26/16.
 */
public class SunshineSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static SunshineSyncAdapter sSunshineSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("SunshineSyncService","onCreate - SunshineSyncSrevice");
        synchronized (sSyncAdapterLock){
            if (sSunshineSyncAdapter == null){
                sSunshineSyncAdapter = new SunshineSyncAdapter(getApplicationContext(),true);
            }
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sSunshineSyncAdapter.getSyncAdapterBinder();
    }
}
