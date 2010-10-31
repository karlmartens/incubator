package net.karlmartens.accounting;

import java.util.Collection;
import java.util.Currency;

public interface Journal {
	
	String getName();
	
	void setName(String name);
	
	Currency getCurrency();
	
	Collection<Account> getAccounts();
	
	Collection<JournalEntry> getEntries();
}
