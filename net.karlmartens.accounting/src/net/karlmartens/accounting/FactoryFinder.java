package net.karlmartens.accounting;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

final class FactoryFinder {

	private static final SecuritySupport SECURITY = new SecuritySupport();
	
	public static Object find(String factoryId, String fallbackClassname) throws ConfigurationError {
		ClassLoader classLoader = SECURITY.getContextClassLoader();
		if (classLoader == null)
			classLoader = FactoryFinder.class.getClassLoader();
		
		Object provider = findSystemPropertyServiceProvider(factoryId, classLoader);
		if (provider != null)
			return provider;
		
		provider = findPropertyFileServiceProvider(factoryId, classLoader);
		if (provider != null)
			return provider;
		
		provider = findJarServiceProvider(factoryId, classLoader);
		if (provider != null)
			return provider;
		
		if (fallbackClassname == null)
			throw new ConfigurationError("Provider for " + factoryId + " cannot be found");
		
		return newInstance(fallbackClassname, classLoader, true);
	}

	private static Object findSystemPropertyServiceProvider(String factoryId,
			ClassLoader classLoader) throws ConfigurationError {
		try {
			final String property = SECURITY.getSystemProperty(factoryId);
			if (property != null) {
				return newInstance(property, classLoader, true);
			}
		} catch (SecurityException e) {
			// ignore - try another method
		}
		
		return null;
	}

	private static final Properties PROPERTIES = new Properties();
	private static boolean firstTime = true;

	private static Object findPropertyFileServiceProvider(String factoryId,
			ClassLoader classLoader) {
		try {
			if (firstTime) {
				synchronized (PROPERTIES) {
					if (firstTime) {
						final String javaHome = SECURITY.getSystemProperty("java.home");
						final String configFile = javaHome + File.separator + "lib" + File.separator + "accounting.properties";
						final File file = new File(configFile);
						firstTime = false;
						if (SECURITY.doesFileExist(file)) {
							PROPERTIES.load(SECURITY.getFileInputStream(file));
						}
					}
				}
			}
			final String factoryClassName = PROPERTIES.getProperty(factoryId);
			if (factoryClassName != null) {
				return newInstance(factoryClassName, classLoader, true);
			}
			
		} catch (Exception e) {
			// ignore - try another method
		}

		return null;
	}

	private static Object findJarServiceProvider(String factoryId, ClassLoader classLoader) throws ConfigurationError {
		final String serviceId = "META-INF/services/" + factoryId;
		final InputStream in = SECURITY.getResourceAsStream(classLoader, serviceId);
		if (in == null)
			return null;
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			reader = new BufferedReader(new InputStreamReader(in));
		}
		
		final String factoryClassName;
		try {
			factoryClassName = reader.readLine();
			reader.close();
		} catch (IOException e) {
			return null;
		}
		
		if (factoryClassName != null && !"".equals(factoryClassName)) {
			return newInstance(factoryClassName, classLoader, false);
		}
		
		return null;
	}
	
	private static Object newInstance(String className, ClassLoader classLoader, boolean doFallback) throws ConfigurationError {
		try {
			Class<?> providerClass;
			if (classLoader == null) {
				providerClass = Class.forName(className);
			} else {
				try {
					providerClass = classLoader.loadClass(className);
				} catch (ClassNotFoundException e) {
					if (doFallback) {
						classLoader = FactoryFinder.class.getClassLoader();
						providerClass = classLoader.loadClass(className);
					} else {
						throw e;
					}
				}			
			}
			
			return providerClass.newInstance();
		} catch (ClassNotFoundException e) {
			throw new ConfigurationError("Provider " + className + " not found", e);
		} catch (Exception e) {
			throw new ConfigurationError("Provider " + className + " could not be instantiated: " + e, e);
		}
	}
	
	static class ConfigurationError extends Exception {
		private static final long serialVersionUID = 1L;

		private ConfigurationError(String message) {
			super(message);
		}

		public ConfigurationError(String message, Throwable cause) {
			super(message, cause);
		}
		
	}
}
