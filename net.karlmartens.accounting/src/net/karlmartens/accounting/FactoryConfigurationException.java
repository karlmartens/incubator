package net.karlmartens.accounting;

public final class FactoryConfigurationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	FactoryConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}
}
