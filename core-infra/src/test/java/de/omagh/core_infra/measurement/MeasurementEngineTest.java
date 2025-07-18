package de.omagh.core_infra.measurement;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Test;

import io.reactivex.rxjava3.core.Observable;

/**
 * Unit tests for {@link MeasurementEngine}.
 */
public class MeasurementEngineTest {
    @Test
    public void observeLux_delegatesToProvider() {
        LightSensorProvider provider = mock(LightSensorProvider.class);
        Observable<Float> stream = Observable.just(42f);
        when(provider.observeLux()).thenReturn(stream);

        MeasurementEngine engine = new MeasurementEngine(provider);
        assertSame(stream, engine.observeLux());
        verify(provider).observeLux();
    }

    @Test
    public void stopALS_invokesProviderStop() {
        LightSensorProvider provider = mock(LightSensorProvider.class);
        MeasurementEngine engine = new MeasurementEngine(provider);

        engine.stopALS();

        verify(provider).stop();
    }
}
