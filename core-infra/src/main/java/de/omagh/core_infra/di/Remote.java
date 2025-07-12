package de.omagh.core_infra.di;

import java.lang.annotation.Retention;

import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Qualifier for remote data sources.
 */
@Qualifier
@Retention(RUNTIME)
public @interface Remote {
}