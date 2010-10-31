package net.karlmartens.accounting;

public interface AccountVisitor<T> {

	T accept(AccountGroup gorup);
	
	T accept(AccountDetail detail);
}
