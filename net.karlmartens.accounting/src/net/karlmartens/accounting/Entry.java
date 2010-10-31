package net.karlmartens.accounting;

import java.math.BigDecimal;

public interface Entry {

	enum Type { DEBIT, CREDIT };
	
	Type getType();
	
	AccountDetail getAccount();
	
	JournalEntry getJournalEntry();
	
	BigDecimal getAmount();
	
}
