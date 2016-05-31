package com.example.ravindrasaini.sunshine.sync;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.ravindrasaini.sunshine.sync.SunshineAuthenticator;

/**
 * A bound Service that instantiates the authenticator when started
 */
public class SunshineAuthenticatorService extends Service {
    private SunshineAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new SunshineAuthenticator(this);

        super.onCreate();
    }


    /**
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
