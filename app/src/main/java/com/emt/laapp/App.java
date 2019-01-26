package com.emt.laapp;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;

import com.pixplicity.easyprefs.library.Prefs;

import timber.log.Timber;

public class App extends Application {
    private static App app;
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        App.context = getApplicationContext();

        // Initialize the Prefs class
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

    }

    public static App getApp() {
        return app;
    }

    public static Context getAppContext() {
        return App.context;
    }
}