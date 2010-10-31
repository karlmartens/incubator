package net.karlmartens.accounting;

import java.util.Collection;
import java.util.Currency;

public interface ConfigurationService {

	Collection<Journal> getJournals();
	
	Journal createJournal(String name, Currency Currency);
	
	AccountGroup createAccountGroup(Journal journal, Account.Type type, String name);
	
	AccountGroup createAccountGroup(AccountGroup group, String name);
	
	AccountDetail createAccountDetail(Journal journal, Account.Type type, String name);
	
	AccountDetail createAccountDetail(AccountGroup group, String name);
	
	void save(Journal journal) throws DuplicateNameException;
	
	void save(Account account) throws DuplicateNameException;
		
	void delete(Journal journal) throws IllegalOperationException;
	
	void delete(Account account) throws IllegalOperationException;
	
}
