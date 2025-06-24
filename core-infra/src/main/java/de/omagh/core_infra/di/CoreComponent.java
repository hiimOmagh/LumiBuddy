package de.omagh.core_infra.di;

import android.app.Application;

import dagger.BindsInstance;
import dagger.Component;

import javax.inject.Singleton;
import de.omagh.core_data.repository.PlantRepository;
import de.omagh.core_domain.repository.MeasurementRepository;

@Singleton
@Component(modules = {
        NetworkModule.class,
        DataModule.class,
        SensorModule.class
})
public interface CoreComponent {
    PlantRepository plantRepository();

    MeasurementRepository measurementRepository();
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        CoreComponent build();
    }
}
