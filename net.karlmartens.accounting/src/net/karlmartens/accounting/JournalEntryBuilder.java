package net.karlmartens.accounting;

import java.math.BigDecimal;
import java.util.Date;

public interface JournalEntryBuilder {
	
	JournalEntryBuilder entry(AccountDetail account, Entry.Type type, BigDecimal amount);
	
	JournalEntry build(Date transactionDate, String description) throws InvalidJournalEntryException;

}
