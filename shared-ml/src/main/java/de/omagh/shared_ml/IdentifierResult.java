package de.omagh.shared_ml;

/**
 * Simple result wrapper for ML identifiers representing either a successful value
 * or an error with details.
 * @param <T> type of the wrapped value
 */
public abstract class IdentifierResult<T> {
    private IdentifierResult() {}

    /** Success case wrapping the prediction value. */
    public static final class Success<T> extends IdentifierResult<T> {
        private final T value;

        public Success(T value) {
            this.value = value;
        }

        public T getValue() {
            return value;
        }
    }

    /** Failure case containing an error message and optional cause. */
    public static final class Error<T> extends IdentifierResult<T> {
        private final String message;
        private final Throwable cause;

        public Error(String message, Throwable cause) {
            this.message = message;
            this.cause = cause;
        }

        public String getMessage() {
            return message;
        }

        public Throwable getCause() {
            return cause;
        }
    }
}
