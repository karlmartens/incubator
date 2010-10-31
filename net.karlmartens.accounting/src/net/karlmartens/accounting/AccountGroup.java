package net.karlmartens.accounting;

import java.util.Collection;

public interface AccountGroup extends Account {
	
	Collection<Account> getChildren();

}
