package net.karlmartens.accounting;

public final class DuplicateNameException extends Exception {
	private static final long serialVersionUID = 1L;

	public DuplicateNameException(String message) {
		super(message);
	}
}
