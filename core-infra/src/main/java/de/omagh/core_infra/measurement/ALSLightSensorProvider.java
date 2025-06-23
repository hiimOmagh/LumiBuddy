package de.omagh.core_infra.measurement;

import android.app.Application;

import io.reactivex.Observable;

/**
 * Implementation of LightSensorProvider using Android's ALSManager.
 */
public class ALSLightSensorProvider implements LightSensorProvider {
    private final ALSManager alsManager;

    public ALSLightSensorProvider(Application app) {
        this.alsManager = new ALSManager(app);
    }

    @Override
    public Observable<Float> observeLux() {
        return Observable.create(emitter -> {
            ALSManager.OnLuxChangedListener listener = emitter::onNext;
            alsManager.start(listener);
            emitter.setCancellable(alsManager::stop);
        });
    }

    @Override
    public void stop() {
        alsManager.stop();
    }
}