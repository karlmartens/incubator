package net.karlmartens.accounting;

import java.util.Collection;

public interface Account {
	
	enum Type { ASSET, LIABILITY, EQUITY, EXPENSE }

	Type getType();
	
	String getName();
	
	void setName(String name);
	
	Journal getJournal();
	
	Collection<Entry> getEntries();
	
	void visit(AccountVisitor<?> visitor);
	
}
