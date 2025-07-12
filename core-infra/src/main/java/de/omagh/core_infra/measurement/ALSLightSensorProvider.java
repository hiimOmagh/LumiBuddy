package de.omagh.core_infra.measurement;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Observable;

/**
 * Implementation of LightSensorProvider using Android's ALSManager.
 */
public class ALSLightSensorProvider implements LightSensorProvider {
    private final ALSManager alsManager;

    @Inject
    public ALSLightSensorProvider(ALSManager alsManager) {
        this.alsManager = alsManager;
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