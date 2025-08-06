package de.omagh.core_domain.sync;

/**
 * Schedules synchronization tasks.
 */
public interface SyncScheduler {
    /**
     * Schedule periodic synchronization.
     */
    void scheduleDaily();
}
