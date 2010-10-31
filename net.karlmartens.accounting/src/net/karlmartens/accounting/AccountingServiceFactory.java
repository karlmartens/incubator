package net.karlmartens.accounting;

public abstract class AccountingServiceFactory {
	
	public abstract ConfigurationService getConfigurationService();
	
	public abstract ActivityService getActivityService();
	
	public static AccountingServiceFactory newInstance() {
		try {
			return (AccountingServiceFactory) FactoryFinder.find("net.karlmartens.accounting.AccountingServiceFactory", null);
		} catch (FactoryFinder.ConfigurationError e) {
			throw new FactoryConfigurationException(e.getMessage(), e);
		}
	}

}
