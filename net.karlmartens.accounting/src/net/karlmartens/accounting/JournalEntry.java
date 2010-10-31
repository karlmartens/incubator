package net.karlmartens.accounting;

import java.util.Collection;
import java.util.Date;

public interface JournalEntry {
	
	Date getTransactionDate();
	
	Date getPostingDate();
	
	String getDescription();
	
	Collection<Entry> getEntries();
}
