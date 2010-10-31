package net.karlmartens.accounting;

import java.util.Collection;
import java.util.Date;

public interface ActivityService {
	
	Journal findJournal(String name);
	
	Account findAccount(String name);

	JournalEntryBuilder getJournalEntryBuilder();
	
	Collection<Entry> getEntriesPostedBetween(Date from, Date to);
	
	Collection<Entry> getEntriesTransactedBetween(Date from, Date to);
		
	void post(JournalEntry journalEntry);

}
