package com.github.skjolber.stcsv.column.tri;

import com.github.skjolber.stcsv.builder.CsvBuilderException;
import com.github.skjolber.stcsv.builder.CsvMappingBuilder2;
import com.github.skjolber.stcsv.builder.SetterProjectionHelper;
import com.github.skjolber.stcsv.column.bi.LongCsvFieldMapperBuilder;
import com.github.skjolber.stcsv.projection.TriConsumerProjection;
import com.github.skjolber.stcsv.projection.ValueProjection;

public class DoubleCsvFieldMapperTriBuilder<T, D, B extends CsvMappingBuilder2<T, ?>> extends LongCsvFieldMapperBuilder<T, B> {

	protected ObjDoubleTriConsumer<T, D> triConsumer;
	protected Class<D> intermediate;
	
	public DoubleCsvFieldMapperTriBuilder(B parent, String name, Class<D> intermediate) {
		super(parent, name);
		
		this.intermediate = intermediate; 
	}

	public DoubleCsvFieldMapperTriBuilder<T, D, B> consumer(ObjDoubleTriConsumer<T, D>  consumer) {
		this.triConsumer = consumer;
		
		return this;
	}

	protected ValueProjection getProjection(int index, SetterProjectionHelper<T> proxy) throws CsvBuilderException {
		if(triConsumer != null) {
			return new TriConsumerProjection(new DoubleCsvColumnValueTriConsumer<>(triConsumer), index);
		}
		return super.getProjection(index, proxy);
	}

}


