package net.karlmartens.accounting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

final class SecuritySupport {

	ClassLoader getContextClassLoader() {
		return AccessController.doPrivileged(new PrivilegedAction<ClassLoader>(){
			@Override
			public ClassLoader run() {
				try {
					return Thread.currentThread().getContextClassLoader();
				} catch (SecurityException e) {
					return null;
				}
			}});
	}

	String getSystemProperty(final String propertyName) {
		return AccessController.doPrivileged(new PrivilegedAction<String>() {
			@Override
			public String run() {
				return System.getProperty(propertyName);
			}});
	}

	boolean doesFileExist(final File file) {
		return AccessController.doPrivileged(new PrivilegedAction<Boolean>(){
			@Override
			public Boolean run() {
				return file.exists();
			}}).booleanValue();
	}

	InputStream getFileInputStream(final File file) throws FileNotFoundException {
		try {
			return AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>(){
				@Override
				public InputStream run() throws FileNotFoundException {
					return new FileInputStream(file);
				}});
		} catch(PrivilegedActionException e) {
			throw (FileNotFoundException)e.getException();
		}
	}

	InputStream getResourceAsStream(final ClassLoader classLoader,
			final String name) {
		return AccessController.doPrivileged(new PrivilegedAction<InputStream>(){
			@Override
			public InputStream run() {
				if (classLoader == null)
					return ClassLoader.getSystemResourceAsStream(name);
				
				return classLoader.getResourceAsStream(name);
			}});
	}

}
