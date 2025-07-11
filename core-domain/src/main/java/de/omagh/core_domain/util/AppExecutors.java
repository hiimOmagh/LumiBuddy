package de.omagh.core_domain.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Provides application wide ExecutorServices for IO and single threaded work.
 */
public class AppExecutors {
    private final ExecutorService ioExecutor;
    private final ExecutorService singleThreadExecutor;

    public AppExecutors() {
        this(Executors.newCachedThreadPool(), Executors.newSingleThreadExecutor());
    }

    public AppExecutors(ExecutorService ioExecutor, ExecutorService singleThreadExecutor) {
        this.ioExecutor = ioExecutor;
        this.singleThreadExecutor = singleThreadExecutor;
    }

    /**
     * Returns a general IO executor.
     */
    public ExecutorService io() {
        return ioExecutor;
    }

    /**
     * Returns a single threaded executor.
     */
    public ExecutorService single() {
        return singleThreadExecutor;
    }

    /**
     * Shutdown executors when the app is terminating.
     */
    public void shutdown() {
        ioExecutor.shutdown();
        singleThreadExecutor.shutdown();
    }
}
