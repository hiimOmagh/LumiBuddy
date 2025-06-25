package de.omagh.app_di;


/**
 * Marker interface for the application level Dagger component.
 * <p>
 * Feature modules should obtain their dependencies directly from
 * {@code CoreComponent} or provide them within their own modules.
 */
public interface ApplicationComponent {
}