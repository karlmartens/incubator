package net.karlmartens.accounting;

public final class InvalidJournalEntryException extends Exception {
	private static final long serialVersionUID = 1L;

	public InvalidJournalEntryException(String message) {
		super(message);
	}
}
