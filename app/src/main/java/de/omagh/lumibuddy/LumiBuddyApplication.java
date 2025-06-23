package de.omagh.lumibuddy;

import android.app.Application;

import timber.log.Timber;

/**
 * Simplified Application setup used for instrumentation tests.
 */
public class LumiBuddyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Timber.plant(new Timber.DebugTree());
    }
}