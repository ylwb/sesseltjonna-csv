package com.github.skjolber.stcsv.databinder.column.bi;

import java.util.function.BiConsumer;

import com.github.skjolber.stcsv.builder.CsvBuilderException;
import com.github.skjolber.stcsv.databinder.builder.AbstractCsvMappingBuilder;
import com.github.skjolber.stcsv.databinder.builder.AbstractTypedCsvFieldMapperBuilder;
import com.github.skjolber.stcsv.databinder.builder.SetterProjectionHelper;
import com.github.skjolber.stcsv.databinder.projection.BiConsumerProjection;
import com.github.skjolber.stcsv.databinder.projection.ValueProjection;

public class StringCsvFieldMapperBuilder<T, B extends AbstractCsvMappingBuilder<T, ?>> extends AbstractTypedCsvFieldMapperBuilder<T, B> {

	protected BiConsumer<T, String> consumer;
	protected BiConsumer<T, String> setter;

	public StringCsvFieldMapperBuilder(B parent, String name) {
		super(parent, name);
	}

	public StringCsvFieldMapperBuilder<T, B> consumer(BiConsumer<T, String> consumer) {
		this.consumer = consumer;
		
		return this;
	}

	public StringCsvFieldMapperBuilder<T, B> setter(BiConsumer<T, String> setter) {
		this.setter = setter;
		
		return this;
	}
	
	public StringCsvFieldMapperBuilder<T, B> fixedSize(int fixedSize) {
		super.fixedSize(fixedSize);
		
		return this;
	}

	/**
	 * Indicate that this field is quoted. If there is no linebreaks, 
	 * rather use the method {@linkplain #quotedWithoutLinebreaks()} to improve performance.
	 * 
	 * @return this instance.
	 */
	
	public StringCsvFieldMapperBuilder<T, B> quoted() {
		super.quoted();
		
		return this;
	}

	/**
	 * Indicate that this field is quoted, but has no linebreaks.
	 * 
	 * @return this instance.
	 */
	public StringCsvFieldMapperBuilder<T, B> quotedWithoutLinebreaks() {
		super.quotedWithoutLinebreaks();
		
		return this;
	}

	public StringCsvFieldMapperBuilder<T, B> trimTrailingWhitespaces() {
		super.trimTrailingWhitespaces();
		
		return this;
	}

	public StringCsvFieldMapperBuilder<T, B> trimLeadingWhitespaces() {
		super.trimLeadingWhitespaces();
		
		return this;
	}

	protected ValueProjection getProjection(int index, SetterProjectionHelper<T> proxy) throws CsvBuilderException {
		if(consumer != null) {
			return new BiConsumerProjection(new StringCsvColumnValueConsumer<>(consumer), index);
		}
		return super.getProjection(index, proxy);
	}
	
	@Override
	protected Class<?> getColumnClass() {
		return String.class;
	}

	@Override
	protected void invokeSetter(T value) {
		setter.accept(value, null);
	}

	@Override
	protected boolean hasSetter() {
		return setter != null;
	}
	
}

