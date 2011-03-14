/**
 *  net.karlmartens.platform, is a library of shared basic utility classes
 *  Copyright (C) 2011
 *  Karl Martens
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as 
 *  published by the Free Software Foundation, either version 3 of the 
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.karlmartens.platform.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ReflectSupport {
	
	public static Object invoke(String methodName, Object instance, Object... args) {
		try {
			final Class<?>[] argTypes = new Class[args.length];
			for (int i=0; i<args.length; i++) {
				argTypes[i] = args[i].getClass().getComponentType();
			}
			
			final Method method = instance.getClass().getDeclaredMethod(methodName,  argTypes);
			method.setAccessible(true);
			return method.invoke(instance, args);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

}
