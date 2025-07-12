package de.omagh.feature_plantdb;

import static org.junit.Assert.*;

import android.app.Application;

import androidx.test.core.app.ApplicationProvider;

import de.omagh.core_infra.user.UserProfileManager;
import de.omagh.feature_plantdb.ui.ProfileViewModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class ProfileViewModelTest {
    @Mock
    UserProfileManager manager;

    private ProfileViewModel vm;

    @Before
    public void setup() {
        MockitoAnnotations.openMocks(this);
        Application app = ApplicationProvider.getApplicationContext();
        Mockito.when(manager.getUsername()).thenReturn("u");
        Mockito.when(manager.getAvatarUri()).thenReturn("a");
        Mockito.when(manager.getTheme()).thenReturn("t");
        vm = new ProfileViewModel(app, manager);
    }

    @Test
    public void loadsValuesFromManager() {
        assertEquals("u", vm.getUsername().getValue());
        assertEquals("a", vm.getAvatarUri().getValue());
        assertEquals("t", vm.getTheme().getValue());
    }

    @Test
    public void setUsername_updatesManager() {
        vm.setUsername("x");
        Mockito.verify(manager).setUsername("x");
        assertEquals("x", vm.getUsername().getValue());
    }
}
