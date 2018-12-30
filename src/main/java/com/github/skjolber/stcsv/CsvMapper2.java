package com.github.skjolber.stcsv;

import java.io.Reader;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.skjolber.stcsv.builder.CsvMappingBuilder2;

/**
 * 
 * Dynamic CSV parser generator. Adapts the underlying implementation according 
 * to the first (header) line.
 * <br><br>
 * Uses ASM to build the parsers.
 * <br><br>
 * Thread-safe.
 */

public class CsvMapper2<T, D> extends AbstractCsvMapper<T> {

	public static <T, D> CsvMappingBuilder2<T, D> builder(Class<T> cls, Class<D> delegate) {
		return new CsvMappingBuilder2<T, D>(cls, delegate);
	}

	public CsvMapper2(Class<T> cls, Class<D> intermediate, char divider, List<AbstractColumn> columns, boolean skipEmptyLines,
			boolean skipComments, boolean skippableFieldsWithoutLinebreaks, ClassLoader classLoader, int bufferLength) {
		super(cls, divider, columns, skipEmptyLines, skipComments, skippableFieldsWithoutLinebreaks, classLoader, bufferLength);
		
		this.intermediate = intermediate;
	}

	protected final Class<D> intermediate;
	protected final Map<String, CsvReaderConstructor2<T, D>> factories = new ConcurrentHashMap<>();

	public CsvReader<T> create(Reader reader, D delegate) throws Exception {
		// avoid multiple calls to read when locating the first line
		// so read a full buffer
		char[] current = new char[bufferLength + 1];

		int start = 0;
		int end = 0;
		do {
			int read = reader.read(current, start, bufferLength - start);
			if(read == -1) {
				return new EmptyCsvReader<>();
			} else {
				end += read;
			}

			for(int i = start; i < end; i++) {
				if(current[i] == '\n') {
					return create(reader, new String(current, 0, i), current, i + 1, end, delegate);
				}
			}
			start += end;
		} while(end < bufferLength);

		throw new IllegalArgumentException("No linebreak found in " + current.length + " characters");
	}

	public CsvReader<T> create(Reader reader, String header, char[] current, int offset, int length, D delegate) throws Exception {
		CsvReaderConstructor2<T, D> constructor = factories.get(header); // note: using the stringbuilder as a key does not work
		if(constructor == null) {
			boolean carriageReturns = header.length() > 1 && header.charAt(header.length() - 1) == '\r';
			List<String> fields = parseNames(header);

			constructor = createScannerFactory(carriageReturns, fields);
			if(constructor == null) {
				return new EmptyCsvReader<>();
			}
			factories.put(header, constructor);
		}
		if(delegate != null) {
			return constructor.newInstance(reader, current, offset, length, delegate);
		}
		return constructor.newInstance(reader, current, offset, length, delegate);
	}

	public CsvReaderConstructor2<T, D> createDefaultScannerFactory(boolean carriageReturns) throws Exception {
		return new CsvReaderConstructor2(super.createDefaultReaderClass(carriageReturns), intermediate);
	}

	public CsvReaderConstructor2<T, D> createScannerFactory(boolean carriageReturns, String header) throws Exception {
		return new CsvReaderConstructor2(super.createReaderClass(carriageReturns, header), intermediate);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CsvReaderConstructor2<T, D> createScannerFactory(boolean carriageReturns, List<String> csvFileFieldNames) throws Exception {
		return new CsvReaderConstructor2(super.createReaderClass(carriageReturns, csvFileFieldNames), intermediate);
	}

}
