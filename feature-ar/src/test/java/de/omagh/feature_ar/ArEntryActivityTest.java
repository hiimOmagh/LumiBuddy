package de.omagh.feature_ar;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import de.omagh.core_data.repository.DiaryRepository;
import de.omagh.core_domain.repository.MeasurementRepository;
import de.omagh.core_domain.util.AppExecutors;
import de.omagh.core_infra.ar.ARGrowthTracker;

/**
 * Instrumented tests for {@link ArEntryActivity} verifying screenshot capture and cleanup.
 */
@RunWith(AndroidJUnit4.class)
public class ArEntryActivityTest {
    @Mock
    MeasurementRepository measurementRepository;
    @Mock
    DiaryRepository diaryRepository;

    private AutoCloseable mocks;

    @Before
    public void setup() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        mocks.close();
    }

    @Test
    public void screenshotCallback_receivesUri() throws Exception {
        try (ActivityScenario<TestActivity> scenario = ActivityScenario.launch(TestActivity.class)) {
            scenario.onActivity(a -> {
                CountDownLatch latch = new CountDownLatch(1);
                a.takeScreenshot(uri -> {
                    assertNotNull(uri);
                    latch.countDown();
                });
                try {
                    assertTrue(latch.await(2, TimeUnit.SECONDS));
                } catch (InterruptedException e) {
                    fail();
                }
            });
        }
    }

    @Test
    public void onDestroy_cleansGrowthTracker() {
        try (ActivityScenario<TestActivity> scenario = ActivityScenario.launch(TestActivity.class)) {
            scenario.onActivity(a -> {
                assertNotNull(a.growthTracker);
            });
            scenario.moveToState(androidx.lifecycle.Lifecycle.State.DESTROYED);
            verify(TestActivity.tracker).cleanup();
        }
    }

    /** Test activity overriding dependency setup. */
    public static class TestActivity extends ArEntryActivity {
        static ARGrowthTracker tracker = mock(ARGrowthTracker.class);
        @Override
        protected boolean checkArSupport() { return true; }
        @Override
        protected boolean requestArInstall() { return true; }
        @Override
        protected void setupScene() {
            executors = new AppExecutors();
            growthTracker = tracker;
        }
        void takeScreenshot(Consumer<Uri> cb) { saveScreenshotAsync(cb); }
    }
}
