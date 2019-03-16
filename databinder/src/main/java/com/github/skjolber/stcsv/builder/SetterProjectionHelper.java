package com.github.skjolber.stcsv.builder;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class SetterProjectionHelper<T> implements InvocationHandler {
	
	protected Class<T> target;
	protected Method method;
	protected T proxy;
	
	public SetterProjectionHelper(Class<T> target) {
		this.target = target;
	}
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		this.method = method;
		
		return null;
	}

	public Method invokeSetter(String name, Class<?> cls) {
		try {
			this.method = target.getMethod(getSetterName(name), cls);
		} catch (NoSuchMethodException e1) {
			try {
				this.method = target.getMethod(getNormalizedSetterName(name), cls);
			} catch (NoSuchMethodException e2) {
				throw new IllegalArgumentException("Unable to detect setter for class " + target.getName() + " field '" + name + "' (" + getSetterName(name) + "/ "+ getNormalizedSetterName(name) + ").");
			}
		}
		
		return this.method;
	}
	
	protected String getSetterName(String name) {
		return "set" + Character.toUpperCase(name.charAt(0)) + name.substring(1);
	}

	protected String getNormalizedSetterName(String name) {
		
		StringBuilder builder = new StringBuilder("set");
		
		boolean high = true;
		for(int i = 0; i < name.length(); i++) {
			if(high) {
				builder.append(Character.toUpperCase(name.charAt(i)));
				
				high = false;
			} else if(name.charAt(i) == '_') {
				high = true;
			} else {
				builder.append(name.charAt(i));
			}
		}
		
		return builder.toString();
	}

	public Method toMethod(AbstractCsvFieldMapperBuilder<T, ?> abstractCsvFieldMapperBuilder) throws CsvBuilderException {
		// detect setter using reflection, based on the name
		return invokeSetter(abstractCsvFieldMapperBuilder.getName(), abstractCsvFieldMapperBuilder.getColumnClass());
	}
}