package com.github.skjolber.stcsv.databinder;

import java.io.Reader;

import com.github.skjolber.stcsv.CsvReader;

public interface StaticCsvMapper2<T, D> {

	CsvReader<T> newInstance(Reader reader, D delegate);
	
	CsvReader<T> newInstance(Reader reader, char[] current, int offset, int length, D delegate);

}
