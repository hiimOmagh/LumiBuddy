package de.omagh.core_infra.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;

@Singleton
@Component(modules = {
        NetworkModule.class,
        DataModule.class,
        SensorModule.class
})
public interface CoreComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);

        CoreComponent build();
    }
}
